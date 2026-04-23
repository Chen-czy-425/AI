package com.aiProject.config;


/**
 * Ollama 配置类
 */
public class OllamaConfig {
    // Ollama API 基础地址
    public static final String OLLAMA_BASE_URL = "http://localhost:11434";
    // Generate API 路径
    public static final String OLLAMA_GENERATE_API = OLLAMA_BASE_URL + "/api/generate";
    // 默认流式读取超时（分钟）
    public static final int DEFAULT_STREAM_READ_TIMEOUT = 5;
    // JSON 媒体类型
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
}
