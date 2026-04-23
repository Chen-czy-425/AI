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
        return ollamaService.createStreamChat(request);
    }
}
