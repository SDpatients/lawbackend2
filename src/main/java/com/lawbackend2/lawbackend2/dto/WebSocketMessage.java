package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type;
    private Long userId;
    private String title;
    private String content;
    private Object data;
    private Long timestamp;
}
