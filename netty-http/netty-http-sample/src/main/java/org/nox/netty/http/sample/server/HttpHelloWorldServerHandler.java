package org.nox.netty.http.sample.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.nox.netty.http.sample.pojo.User;
import org.nox.netty.http.sample.serialize.impl.JSONSerializer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Jawliet on 2019/3/6 15:24
 */
@Slf4j
public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private HttpHeaders headers;
    private FullHttpRequest fullRequest;

    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");


    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        User user = new User();
        user.setUserName("user");
        user.setDate(new Date());

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            headers = request.headers();
            String uri = request.uri();
            log.info("http uri: " + uri);
            if (uri.equals(FAVICON_ICO)) {
                return;
            }
            HttpMethod method = request.method();
            if (HttpMethod.GET.equals(method)) {
                /*
                 * QueryStringDecoder 的作用就是把 HTTP uri 分割成 path 和 key-value 参数对，
                 * 也可以用来解码 Content-Type = "application/x-www-form-urlencoded" 的 HTTP POST。特别注意的是，该 decoder 仅能使用一次
                 * */
                QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                //此处仅打印请求参数（你可以根据业务需求自定义处理）
                uriAttributes.forEach((key, value) -> {
                    for (String attrVal : value) {
                        log.info(key + "=" + attrVal);
                    }
                });
                user.setMethod("get");
            } else if (HttpMethod.POST.equals(method)) {
                //POST请求,由于你需要从消息体中获取数据,因此有必要把msg转换成FullHttpRequest
                fullRequest = (FullHttpRequest) msg;
                //根据不同的Content_Type处理body数据
                dealWithContentType();
                user.setMethod("post");

            }

            JSONSerializer jsonSerializer = new JSONSerializer();
            byte[] content = jsonSerializer.serialize(user);

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/plain");
            //添加header描述length。这一步是很重要的一步，如果没有这一步，你会发现用postman发出请求之后就一直在刷新，因为http请求方不知道返回的数据到底有多长
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 简单处理常用几种 Content-Type 的 POST 内容（可自行扩展）
     */
    private void dealWithContentType() {
        String contentType = getContentType();
        //可以使用HttpJsonDecoder
        switch (contentType) {
            case "application/json": {
                String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                JSONObject obj = JSON.parseObject(jsonStr);
                obj.forEach((key, value) -> log.info(key + "=" + value.toString()));

                break;
            }
            case "application/x-www-form-urlencoded": {
                //方式一：使用 QueryStringDecoder
                String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        log.info(attr.getKey() + "=" + attrVal);
                    }
                }

                break;
            }
            case "multipart/form-data":
                //TODO 用于文件上传
                break;
            default:
                //do nothing...
                break;
        }
    }

    private String getContentType() {
        String typeStr = headers.get("Content-Type");
        String[] list = typeStr.split(";");
        return list[0];
    }
}
