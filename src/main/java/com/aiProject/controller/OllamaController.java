package com.aiProject.controller;

import com.aiProject.common.Result;
import com.aiProject.dto.OllamaRequest;
import com.aiProject.dto.StreamMessageDTO;
import com.aiProject.exception.OllamaInvokeException;
import com.aiProject.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@RestController
@RequestMapping("/api/ollama")
@CrossOrigin
public class OllamaController {

    // 注入服务
    @Autowired
    private OllamaService ollamaService;

    /**
     * 同步接口（一次性返回答案）
     */
    @PostMapping("/chat/sync")
    public Result<String> chatSync(@RequestBody OllamaRequest request) {
        try {
            log.info("同步提问：{}", request.getPrompt());
            String answer = ollamaService.invokeSync(request).getFullResponse();
            return Result.success(answer);
        } catch (OllamaInvokeException e) {
            log.error("调用失败", e);
            return Result.error("调用失败：" + e.getMessage());
        }
    }

    /**
     * 流式接口（实时打字）
     */
    @PostMapping("/chat/stream")
    public SseEmitter chatStream(@RequestBody OllamaRequest request) {
        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        final int[] index = {0};

        new Thread(() -> {
            try {
                // 执行流式调用
                ollamaService.invokeStream(request, fragment -> {
                    try {
                        StreamMessageDTO msg = new StreamMessageDTO();
                        msg.setContent(fragment);
                        msg.setFinish(false);
                        msg.setIndex(index[0]++);

                        Result<StreamMessageDTO> result = Result.success(msg);
                        emitter.send(result);

                    } catch (Exception e) {
                        // 客户端断开 → 安全关闭并退出
                        log.error("发送消息失败，断开连接: {}", e.getMessage());
                        emitter.completeWithError(e);
                        throw new RuntimeException("客户端断开", e);
                    }
                });

                // 正常结束
                StreamMessageDTO end = new StreamMessageDTO();
                end.setContent("");
                end.setFinish(true);
                end.setIndex(index[0]++);
                emitter.send(Result.success(end));

                emitter.complete();
                log.info("流式对话正常结束");

            } catch (Exception e) {
                log.error("流式调用异常", e);

                try {
                    // 给前端返回错误结构
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
