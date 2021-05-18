package com.madai.mock;

import com.madai.mock.utils.autoinject.HandlerInjector;
import org.mockserver.client.server.MockServerClient;
import com.madai.mock.rule.SimpleMappingRules;
import com.madai.mock.utils.Config;
import com.madai.mock.utils.FileMonitor;

import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class Bootstrap {

    /**
     * 启动mock-server
     * @param configPath
     * @throws IOException
     */
    public static void startMockServer(int port, String configPath) throws IOException {
        MockServerClient mockServer = startClientAndServer(port);
        setConfigPath(configPath);
        Config.initConfig();
        HandlerInjector.inject();
        SimpleMappingRules.initRules(mockServer);
        FileMonitor.init();
    }

    /**
     * 设置配置文件路径
     * @param path
     */
    private static void setConfigPath(String path) {
        Config.setConfigPath(path);
    }
}
