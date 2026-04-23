package com.aiProject.service.impl;

import com.aiProject.dto.OllamaRequest;
import com.aiProject.dto.OllamaResponse;
import com.aiProject.exception.OllamaInvokeException;
import com.aiProject.service.OllamaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class OllamaServiceImpl implements OllamaService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String URL = "http://localhost:11434/api/generate";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // ====================== 同步调用 ======================
    @Override
    public OllamaResponse invokeSync(OllamaRequest request) throws OllamaInvokeException {
        try {
            String json = "{\n" +
                    "\"model\":\"" + request.getModelName() + "\",\n" +
                    "\"prompt\":\"" + request.getPrompt() + "\",\n" +
                    "\"stream\":false,\n" +
                    "\"temperature\":" + request.getTemperature() + "\n" +
                    "}";

            RequestBody body = RequestBody.create(json, JSON);
            Request okRequest = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(okRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new OllamaInvokeException("请求失败：" + response.code());
                }

                String res = response.body().string();
                JsonNode node = mapper.readTree(res);

                OllamaResponse r = new OllamaResponse();
                r.setFullResponse(node.get("response").asText());
                return r;
            }
        } catch (Exception e) {
            throw new OllamaInvokeException("同步调用异常", e);
        }
    }

    // ====================== 流式调用 ======================
    @Override
    public void invokeStream(OllamaRequest request, Consumer<String> consumer) throws OllamaInvokeException {
        try {
            String json = "{\n" +
                    "\"model\":\"" + request.getModelName() + "\",\n" +
                    "\"prompt\":\"" + request.getPrompt() + "\",\n" +
                    "\"stream\":true,\n" +
                    "\"temperature\":" + request.getTemperature() + "\n" +
                    "}";

            RequestBody body = RequestBody.create(json, JSON);
            Request okRequest = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            Response response = client.newCall(okRequest).execute();
            BufferedSource source = response.body().source();

            String line;
            while ((line = source.readUtf8Line()) != null) {
                if (line.isEmpty()) continue;

                JsonNode node = mapper.readTree(line);
                if (node.has("response")) {
                    String content = node.get("response").asText();
                    consumer.accept(content);
                }

                if (node.has("done") && node.get("done").asBoolean()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new OllamaInvokeException("流式调用异常", e);
        }
    }
}
