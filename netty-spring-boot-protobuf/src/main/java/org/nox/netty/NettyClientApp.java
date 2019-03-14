package org.nox.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author JiangJian on 2019/3/7 17:20
 */
@SpringBootApplication
@ComponentScan({"org.nox.netty.client"})
public class NettyClientApp {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(NettyClientApp.class);
    }
}
