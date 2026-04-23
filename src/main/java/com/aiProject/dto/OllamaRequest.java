package com.aiProject.dto;


import lombok.Data;

/**
 * Ollama 调用请求DTO
 * 封装所有调用参数，统一入参格式
 */
@Data
public class OllamaRequest {
    // 模型名（必填）
    private String modelName;
    // 提问内容（必填）
    private String prompt;
    // 回复随机性（0-1，默认0.7）
    private double temperature = 0.7;
    // 超时时间（秒，默认30）
    private int timeout = 30;
}
