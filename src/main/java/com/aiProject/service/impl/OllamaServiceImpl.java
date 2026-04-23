package com.aiProject.service.impl;

import com.aiProject.common.Result;
import com.aiProject.config.OllamaConfig;
import com.aiProject.dto.OllamaRequest;
import com.aiProject.dto.OllamaResponse;
import com.aiProject.dto.StreamMessageDTO;
import com.aiProject.exception.OllamaInvokeException;
import com.aiProject.service.OllamaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class OllamaServiceImpl implements OllamaService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(OllamaConfig.DEFAULT_STREAM_READ_TIMEOUT, TimeUnit.MINUTES)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String URL = OllamaConfig.OLLAMA_GENERATE_API;
    private static final MediaType JSON = MediaType.get(OllamaConfig.MEDIA_TYPE_JSON);

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

    /**
     * 创建流式对话
     * @param request
     * @return
     */
    @Override
    public SseEmitter createStreamChat(OllamaRequest request) {
        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        final int[] index = {0};

        new Thread(() -> {
            try {
                invokeStream(request, fragment -> {
                    try {
                        StreamMessageDTO msg = new StreamMessageDTO();
                        msg.setContent(fragment);
                        msg.setFinish(false);
                        msg.setIndex(index[0]++);
                        emitter.send(Result.success(msg));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                        throw new RuntimeException("客户端断开");
                    }
                });
                StreamMessageDTO end = new StreamMessageDTO();
                end.setContent("");
                end.setFinish(true);
                end.setIndex(index[0]++);
                emitter.send(Result.success(end));
                emitter.complete();
            } catch (Exception e) {
                try {
                    StreamMessageDTO errorMsg = new StreamMessageDTO();
                    errorMsg.setContent("服务异常：" + e.getMessage());
                    errorMsg.setFinish(true);
                    emitter.send(Result.error(500, "流式调用失败", errorMsg));
                } catch (Exception ex) {
                    emitter.completeWithError(e);
                }
            }
        }).start();

        return emitter;
    }
}
