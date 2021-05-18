package com.madai.mock.rule;

import com.madai.mock.model.MoConfig;
import com.madai.mock.model.RequestConfig;
import org.mockserver.client.server.ForwardChainExpectation;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import com.madai.mock.utils.Config;

import java.util.List;
import java.util.Map;

import static org.mockserver.model.HttpClassCallback.callback;

public class SimpleMappingRules {

//    private static RequestHandler requestHandler = new RequestHandler();

    private static MockServerClient mockServerClient;

    public static void initRules(MockServerClient serverClient) {
        mockServerClient = serverClient;
        addRules();
    }

    public static void resetRules() {
        mockServerClient.reset();
        addRules();
    }

    private static void addRules() {
        for (Map.Entry<String, List<MoConfig>> p : Config.getDataMap().entrySet()) {
            List<MoConfig> moConfigs = p.getValue();
            for (MoConfig moConfig : moConfigs) {
                RequestConfig req = moConfig.getReq();
                ForwardChainExpectation forwardChainExpectation = mockServerClient.when(HttpRequest.request()
                        .withPath(req.getUri()));
                forwardChainExpectation.callback(callback().withCallbackClass("com.madai.mock.rule.RequestHandler"));
            }
        }
    }
}
