package com.stu.delivery_system.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * 虽然@Component默认是单例模式的，但是spring boot 还是会为每个websocket连接初始化一个bean,所以这里使用一个静态的set保存spring boot
 *
 * 创建的bean--MyWebSocket.
 *
 */
@ServerEndpoint(value="/websocket/{nickname}")// websocket连接点映射.
@Component
public class MyWebSocket {

    //用来存储每个客户端对应的MyWebSocket对象.
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
    //用来记录sessionId和该session之间的绑定关系.
    private static Map<String,Session> map = new HashMap<String,Session>();

    private Session session;//当前会话的session.
    private String nickname;//昵称.

    /**
     * 成功建立连接调用的方法.
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("nickname") String nickname){
        this.session = session;
        this.nickname = nickname;
        map.put(session.getId(), session);
        webSocketSet.add(this);//加入set中.
        this.session.getAsyncRemote().sendText(nickname+"上线了,（我的频道号是"+session.getId()+"）");
    }

    /**
     * 连接关闭调用的方法.
     */
    @OnClose
    public void onClose(Session session){
        webSocketSet.remove(this);//从set中移除.
        map.remove(session.getId());
    }

    /**
     * 收到客户端消息后调用的方法.
     */
    @OnMessage
    public void onMessage(String message,Session session,@PathParam("nickname") String nickname){

        //message 不是普通的string ，而是我们定义的SocketMsg json字符串.
        try {
            SocketMsg socketMsg = new ObjectMapper().readValue(message, SocketMsg.class);


            //单聊.
            if(socketMsg.getType() == 1){

                //单聊：需要找到发送者和接受者即可.
                socketMsg.setFromUser(session.getId());//发送者.
                //socketMsg.setToUser(toUser);//这个是由客户端进行设置.
                Session fromSession = map.get(socketMsg.getFromUser());
                Session toSession = map.get(socketMsg.getToUser());
                if(toSession != null){
                    //发送消息.
                    fromSession.getAsyncRemote().sendText(nickname+"："+socketMsg.getMsg());
                    toSession.getAsyncRemote().sendText(nickname+"："+socketMsg.getMsg());
                }else{
                    fromSession.getAsyncRemote().sendText("系统消息：对方不在线或者您输入的频道号有误");
                }
            }else {
                //群发给每个客户端.
                broadcast(socketMsg,nickname);
            }

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时调用.
     */
    public void onError(Session session,Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发的方法.
     * @param message
     */
    private void broadcast(SocketMsg socketMsg ,String nickname){
        for(MyWebSocket item:webSocketSet){
            //发送消息.
            item.session.getAsyncRemote().sendText(nickname+"："+socketMsg.getMsg());
        }
    }

}
