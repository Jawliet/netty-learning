package org.nox.netty.http.sample.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author Jawliet on 2019/3/17
 */
@Path("hello")
public class HelloWorldController {
    @GET
    public String get() {
        return "hello world";
    }

}
