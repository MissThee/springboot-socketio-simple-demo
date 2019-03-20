package com.github.missthee.socketio;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.github.missthee.db.data.FakeDB;
import com.github.missthee.db.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.missthee.socketio.model.AckModel;
import com.github.missthee.socketio.model.MessageModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//SocketIOServer自定义事件（功能实现）
@Component
@Slf4j
public class MessageEventHandler {
    //此示例暂未与数据库中数据做任何关联，使用静态变量临时存储
    private static SocketIOServer socketIoServer;
    //socketio在每次用户建立连接时会创建uuid，需使用此uuid与自己的用户做关系绑定
    private static Map<String, List<UUID>> userIdUUIDsMap = new ConcurrentHashMap<>();  //存储已登录的<用户id,连接时产生的多个uuid>对应关系，因为一个用户可能在两个设备同时登陆，所以一个用户id，对应一个uuid表
    private static Map<UUID, String> UUIDUserIdMap = new ConcurrentHashMap<>();         //存储已登录的<uuid,用户id>。（仅为方便查询，可直接遍历userIdUUIDsMap，得到对应值）
    private static Map<String, String> userIdNicknameMap = new ConcurrentHashMap<>();   //存储已登录的<用户id,昵称>。（仅为方便查询，可直接查询数据库得到值，但直接数据库读取开销大，尽量做成缓存）

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        socketIoServer = server;
    }

    //客户端连接服务。对应客户端 socket.on('connect',...
    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.debug("客户端:  " + client.getSessionId() + "  已连接");
        String userId = client.getHandshakeData().getSingleUrlParam("id");//从url中获取参数
        User user = FakeDB.selectUserById(Integer.parseInt(userId));//从模拟数据中获取用户信息
        if (user == null) {
            System.out.println("此id无用户：" + userId+"。");
            return;
        }
        addUserInfo(user, client.getSessionId());//从 在线用户列表 中增加此用户的信息
        updateUserListForWeb();//推送在线用户列表
    }

    //客户端断开连接服务。对应客户端 socket.on('disconnect',...
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.debug("客户端:  " + client.getSessionId() + "  断开连接");
        removeUserInfo(client.getSessionId());//从 在线用户列表 中删除此用户的信息
        updateUserListForWeb();//推送在线用户列表
    }

    //-----------------以下为自定义监听事件------------------
    //测试message事件，服务端控制台输出，发送一个成功回调。
    @OnEvent(value = "message")
    public void onMessage(SocketIOClient client, AckRequest ackRequest, MessageModel data) {
        log.debug("message触发");
        //当客户端send/emit有回调函数时，ackRequest.isAckRequested()==true
        //ackRequest.sendAckData(回传的参数)，可回传回调参数，让客户端触发回调函数。客户端的回调函数一般为.emit()方法中的第三个参数。
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(AckModel.success());
        }
    }

    //广播。将接收的内容发送给所有用户
    @OnEvent(value = "broadcast")
    public void onBroadcast(SocketIOClient client, AckRequest ackRequest, MessageModel data) {
        log.debug("broadcast触发: " + data.getContent());
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(AckModel.success());
        }
        data.setFromId(UUIDUserIdMap.get(client.getSessionId()));
        data.setFromNickname(userIdNicknameMap.get(data.getFromId()));
        socketIoServer.getBroadcastOperations().sendEvent("broadcast", data);
    }

    //发送到指定用户。将接收的内容发送给指定用户
    @OnEvent(value = "toOneUserByUserId")
    public static void toOneUserByUserId(SocketIOClient client, AckRequest ackRequest, MessageModel data) {   //向客户端推消息
        log.debug("toOneUserByUserId触发：" + data.getContent() + "；" + UUIDUserIdMap.get(client.getSessionId()) + "→" + data.getToId() + ":" + data.getMsg());
        if (data.getToId() == null) {
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(AckModel.failure());
            }
        } else {
            Boolean[] isSuccess = {false};
            data.setFromId(UUIDUserIdMap.get(client.getSessionId()));
            data.setFromNickname(userIdNicknameMap.get(data.getFromId()));
            data.setToNickname(userIdNicknameMap.get(data.getToId()));
            List<UUID> uuidList = userIdUUIDsMap.get(data.getToId());
            for (UUID uuid : uuidList) {
                if (socketIoServer.getClient(uuid) != null) {
                    socketIoServer.getClient(uuid).sendEvent(
                            "msgToMe",
                            new AckCallback<String>(String.class) {
                                @Override
                                public void onSuccess(String result) {//接收客户端回执的回调函数
                                    isSuccess[0] = true;
                                    log.debug("客户端回执: " + client.getSessionId() + " data: " + result);
                                }
                            },
                            data);
                }
            }
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(AckModel.res(isSuccess[0], data));
            }
        }
    }

    //-----------------------以下为普通自定义工具方法----------------------
    //把在线用户列推送给所有在线用户。所有客户端需监听onlineUser事件
    private void updateUserListForWeb() {
        socketIoServer.getBroadcastOperations().sendEvent("onlineUser", userIdNicknameMap);
    }

    //增加在线用户列表中新连接的用户的uuid
    private void addUserInfo(User user, UUID uuid) {
        String userId = String.valueOf(user.getId());
        String userNickname = user.getNickname();
        //更新UUIDUserIdMap
        UUIDUserIdMap.put(uuid, userId);
        //更新userIdUUIDsMap
        {
            if (userIdUUIDsMap.containsKey(userId)) {
                userIdUUIDsMap.get(userId).add(uuid);
            } else {
                userIdUUIDsMap.put(userId, new ArrayList<UUID>() {{
                    add(uuid);
                }});
            }
        }
        //更新userIdNicknameMap
        userIdNicknameMap.put(userId, userNickname);
        log.debug("userIdUUIDsMap:" + userIdUUIDsMap);
    }

    //去除在线用户列表中断开的用户的uuid。当一个用户对应的uuid表为空时，将其移除。
    private void removeUserInfo(UUID uuid) {
        String userId = UUIDUserIdMap.get(uuid);
        //更新UUIDUserIdMap
        UUIDUserIdMap.remove(uuid);
        //更新userIdUUIDsMap
        {
            List UUIDList = userIdUUIDsMap.get(userId);
            UUIDList.remove(uuid);
            if (UUIDList.size() == 0) {
                userIdUUIDsMap.remove(userId);
            }
        }
        //更新userIdNicknameMap
        userIdNicknameMap.remove(userId);
        log.debug("userIdUUIDsMap:" + userIdUUIDsMap);
    }
}
