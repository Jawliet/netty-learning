package org.nox.netty.rpc.network.service;


import org.nox.netty.rpc.network.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * @author JiangJian on 2019/3/13 16:53
 */
public interface InfoUserService {

    List<InfoUser> insertInfoUser(InfoUser infoUser);

    InfoUser getInfoUserById(String id);

    void deleteInfoUserById(String id);

    String getNameById(String id);

    Map<String, InfoUser> getAllUser();
}
