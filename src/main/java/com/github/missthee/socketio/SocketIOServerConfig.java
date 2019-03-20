package com.github.missthee.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

//SocketIOServer配置类
@Configuration
public class SocketIOServerConfig {
    private static String hostname;
    private static int port;

    @Value("${socketio.host}")
    public void setHostname(String a) {
        hostname = a;
    }

    @Value("${socketio.port}")
    public void setPort(int a) {
        port = a;
    }

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        config.setAuthorizationListener(data -> {//身份验证，修改为自己安全框架的验证，此处只验证id是否为空
            String userId = data.getSingleUrlParam("id");
            return !StringUtils.isEmpty(userId);
        });

        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
