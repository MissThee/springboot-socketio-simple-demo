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
1. application.properties中server.port修改为任意空闲端口号（此端口不是socket使用的端口，而是为了让springboot无端口冲突，启动起来），或自行配置springboot停用内置tomcat
2. 运行服务端
3. 使用chrome浏览器打开项目目录html/socketio-test.html文件。  
  输入正确的后台地址，端口号。输入用户id （0到9任意）。连接，测试。
注：本项目，单个用户可在多个客户端登录，同时接收消息
