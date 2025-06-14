package com.starlwr.bot.bilibili.service;

import com.starlwr.bot.bilibili.model.Room;
import com.starlwr.bot.bilibili.model.Up;
import com.starlwr.bot.bilibili.util.BilibiliApiUtil;
import com.starlwr.bot.common.model.PushUser;
import com.starlwr.bot.common.service.DataSourceService;
import com.starlwr.bot.common.service.DataSourceServiceInterface;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bilibili 数据源服务
 */
@Slf4j
@Service
@DataSourceService(name = "bilibili")
public class BilibiliDataSourceService implements DataSourceServiceInterface {
    @Resource
    private BilibiliApiUtil bilibili;

    /**
     * 补全推送用户信息
     * @param user 推送用户
     */
    @Override
    public void completePushUser(PushUser user) {
        Up up = bilibili.getUpInfoByUid(user.getUid());
        user.setUname(up.getUname());
        user.setRoomId(up.getRoomId());
        user.setFace(up.getFace());
    }

    /**
     * 批量补全推送用户信息
     * @param users 推送用户列表
     */
    @Override
    public void completePushUsers(List<PushUser> users) {
        Map<Long, Room> rooms = bilibili.getLiveInfoByUids(users.stream().map(PushUser::getUid).collect(Collectors.toSet()));
        for (PushUser user: users) {
            Room room = rooms.get(user.getUid());
            if (room == null) {
                completePushUser(user);
            } else {
                user.setUname(room.getUname());
                user.setRoomId(room.getRoomId());
                user.setFace(room.getFace());
            }
        }
    }
}
