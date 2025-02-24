package com.example.camel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskTokenRequest {
    private String taskToken;
    private boolean success;
}