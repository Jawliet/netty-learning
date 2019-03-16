package org.nox.netty.rpc.provider.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nox.netty.rpc.network.entity.InfoUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @author Jawliet on 2019/3/13 18:44
 */
@Controller
@Slf4j
public class IndexController {
    @RequestMapping("index")
    @ResponseBody
    public String index() {
        InfoUser user = new InfoUser(UUID.randomUUID().toString(), "王思萌", "BeiJing");
        String json = JSONObject.toJSONString(user);
        log.info(json);
        return json;
    }
}
