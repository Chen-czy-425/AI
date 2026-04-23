package com.aiProject.dto;

import lombok.Data;

/**
 * Ollama 调用响应DTO
 * 封装响应结果，统一出参格式
 */
@Data
public class OllamaResponse {
    private String fullResponse;
}
