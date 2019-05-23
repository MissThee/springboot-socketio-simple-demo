# springboot-socketio简易实时聊天
### 结构
```$xslt
src
 └─main
     └─java
        └─com
            └─github
                └─missthee
                    │  WebApplication.java
                    │
                    ├─db
                    │  ├─data
                    │  │      FakeDB.java    //模拟用户数据
                    │  │
                    │  └─model
                    │          User.java    //用户类
                    │
                    ├─html
                    │      socketio-test.html    //测试网页
                    │
                    └─socketio
                        │  MessageEventHandler.java    //自定义监听事件
                        │
                        ├─config
                        │      MyCommandLineRunner.java    //socketio启动配置（也可直接在spring启动类中直接启动）
                        │      SocketIOServerConfig.java    //socketio服务配置
                        │
                        └─model
                                AckModel.java    //ack确认信息使用的类
                                MessageModel.java    //收发信息使用的类
      
```
### 聊天功能简介
+ socket.io会为每个`链接`生成uuid，项目中每个`用户`有自身的id，采用id与List<uuid>方式存储`用户`与`链接`的关系，维护`在线用户表`
+ 每个`用户`账号可在多个客户端同时登陆，同时保持多个`链接`，同时接收发消息
+ 当`用户`至少有一个`链接`时，为在线状态；当`用户`没有任何链接时，`用户`从`在线用户表`中移除，为离线状态(目前用户列表由广播发送，于每个用户创建/断开连接时触发)
### 测试方式：  
1. 克隆本项目到本地
2. application.properties中server.port修改为任意空闲端口号（此端口不是socket使用的端口，而是为了让springboot无端口冲突，启动起来），或自行配置springboot停用内置tomcat
3. 运行服务端
4. 使用chrome浏览器打开项目目录html/socketio-test.html文件。  
  输入正确的后台地址，端口号。输入用户id （0到9任意）。连接，测试。
注：本项目，单个用户可在多个客户端登录，同时接收消息
