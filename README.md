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

### 测试方式：  
1. application.properties中server.port修改为空闲端口号，或自行配置springboot停用内置tomcat
2. 运行服务端
3. 使用chrome浏览器打开html/socketio-test.html。  
  输入正确的后台地址。输入用户id  0到9。连接，测试。