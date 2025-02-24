package com.example.camel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamoDbItemResponse {
    private String transactionId;
    private String taskToken;
    private String startTime;
    private String orderId;
    private String status;
}