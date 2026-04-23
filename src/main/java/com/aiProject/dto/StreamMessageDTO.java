package com.aiProject.dto;

import lombok.Data;

@Data
public class StreamMessageDTO {
    private String content;     // 内容片段
    private boolean finish;     // 是否结束
    private int index;          // 序号
}
