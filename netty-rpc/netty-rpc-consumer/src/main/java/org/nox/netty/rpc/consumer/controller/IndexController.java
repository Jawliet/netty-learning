package org.nox.netty.rpc.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nox.netty.rpc.consumer.util.IdUtil;
import org.nox.netty.rpc.network.entity.InfoUser;
import org.nox.netty.rpc.network.service.InfoUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jawliet on 2019/3/14
 */
@Slf4j
@Controller
public class IndexController {
    @Resource
    private InfoUserService userService;

    @RequestMapping("index")
    @ResponseBody
    public String index() {
        return new Date().toString();
    }

    @RequestMapping("insert")
    @ResponseBody
    public List<InfoUser> getUserList() throws InterruptedException {

        long start = System.currentTimeMillis();
        int thread_count = 100;
        CountDownLatch countDownLatch = new CountDownLatch(thread_count);
        for (int i = 0; i < thread_count; i++) {
            new Thread(() -> {
                InfoUser infoUser = new InfoUser(IdUtil.getId(), "Jeen", "BeiJing");
                List<InfoUser> users = userService.insertInfoUser(infoUser);
                log.info("返回用户信息记录:{}", JSON.toJSONString(users));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        log.info("线程数：{},执行时间:{}", thread_count, (end - start));
        return null;
    }

    @RequestMapping("getById")
    @ResponseBody
    public InfoUser getById(String id) {
        log.info("根据ID查询用户信息:{}", id);
        return userService.getInfoUserById(id);
    }

    @RequestMapping("getNameById")
    @ResponseBody
    public String getNameById(String id) {
        log.info("根据ID查询用户名称:{}", id);
        return userService.getNameById(id);
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    public Map<String, InfoUser> getAllUser() throws InterruptedException {
        Map<String, InfoUser> map = new HashMap<>();
        long start = System.currentTimeMillis();
        int thread_count = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(thread_count);
        for (int i = 0; i < thread_count; i++) {
            new Thread(() -> {
                Map<String, InfoUser> allUser = userService.getAllUser();
                log.info("查询所有用户信息：{}", JSONObject.toJSONString(allUser));
                map.putAll(allUser);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        log.info("线程数：{},执行时间:{}", thread_count, (end - start));

        return map;
    }
}
