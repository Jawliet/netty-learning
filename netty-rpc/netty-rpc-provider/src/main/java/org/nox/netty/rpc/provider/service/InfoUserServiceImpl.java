package org.nox.netty.rpc.provider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nox.netty.rpc.network.entity.InfoUser;
import org.nox.netty.rpc.network.service.InfoUserService;
import org.nox.netty.rpc.provider.annotation.RpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JiangJian on 2019/3/13 18:56
 */
@RpcService
@Slf4j
public class InfoUserServiceImpl implements InfoUserService {


    private Map<String, InfoUser> infoUserMap = new ConcurrentHashMap<>();

    public List<InfoUser> insertInfoUser(InfoUser infoUser) {
        log.info("新增用户信息:{}", JSONObject.toJSONString(infoUser));
        infoUserMap.put(infoUser.getId(), infoUser);
        return getInfoUserList();
    }

    public InfoUser getInfoUserById(String id) {
        InfoUser infoUser = infoUserMap.get(id);
        log.info("查询用户ID:{}", id);
        return infoUser;
    }

    public List<InfoUser> getInfoUserList() {
        List<InfoUser> userList = new ArrayList<>();
        for (Map.Entry<String, InfoUser> next : infoUserMap.entrySet()) {
            userList.add(next.getValue());
        }
        log.info("返回用户信息记录:{}", JSON.toJSONString(userList));
        return userList;
    }

    public void deleteInfoUserById(String id) {
        log.info("删除用户信息:{}", JSONObject.toJSONString(infoUserMap.remove(id)));
    }

    public String getNameById(String id) {
        log.info("根据ID查询用户名称:{}", id);
        return infoUserMap.get(id).getName();
    }

    public Map<String, InfoUser> getAllUser() {
        log.info("查询所有用户信息{}", JSONObject.toJSONString(infoUserMap));
        return infoUserMap;
    }
}
