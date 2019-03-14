package org.nox.netty.rpc.network.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JiangJian on 2019/3/13 16:53
 */
@Getter
@Setter
@AllArgsConstructor
public class InfoUser {
    private String id;

    private String name;

    private String address;

}
