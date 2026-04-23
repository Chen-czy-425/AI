package com.aiProject.service;

import com.aiProject.dto.OllamaRequest;
import com.aiProject.dto.OllamaResponse;
import com.aiProject.exception.OllamaInvokeException;
import java.util.function.Consumer;

public interface OllamaService {
    OllamaResponse invokeSync(OllamaRequest request) throws OllamaInvokeException;
    void invokeStream(OllamaRequest request, Consumer<String> consumer) throws OllamaInvokeException;
}
