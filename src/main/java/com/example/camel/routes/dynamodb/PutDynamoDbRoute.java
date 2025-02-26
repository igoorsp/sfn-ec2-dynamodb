package com.example.camel.routes.dynamodb;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.ddb.Ddb2Constants;
import org.apache.camel.component.aws2.ddb.Ddb2Operations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Component
public class PutDynamoDbRoute extends RouteBuilder {

    private static final String STATUS = "status";
    private static final String EXECUTION_ID = "executionId";
    private static final String BUSINESS_KEY = "businessKey";
    private static final String EXECUTION_START_TIME = "executionStartTime";
    private static final String TASK_TOKEN = "taskToken";

    @Value("${app.dynamodb.table}")
    String tableName;

    @Override
    public void configure() {

        // Implementação do PUT (Inserção de Item no DynamoDB)
        from("direct:putTask")
                .log("Inserting new item into DynamoDB: ${body}")
                .process(exchange -> {
                    Map<String, Object> requestBody = exchange.getIn().getBody(Map.class);

                    if (!requestBody.containsKey(EXECUTION_ID)) {
                        throw new IllegalArgumentException("executionId is required in the request body");
                    }

                    Map<String, AttributeValue> item = new HashMap<>();
                    item.put(EXECUTION_ID, AttributeValue.builder().s(requestBody.get(EXECUTION_ID).toString()).build());
                    item.put(BUSINESS_KEY, AttributeValue.builder().s(requestBody.getOrDefault(BUSINESS_KEY, "").toString()).build());
                    item.put(EXECUTION_START_TIME, AttributeValue.builder().s(requestBody.getOrDefault(EXECUTION_START_TIME, "").toString()).build());
                    item.put(STATUS, AttributeValue.builder().s(requestBody.getOrDefault(STATUS, "").toString()).build());
                    item.put(TASK_TOKEN, AttributeValue.builder().s(requestBody.getOrDefault(TASK_TOKEN, "").toString()).build());

                    exchange.getIn().setHeader(Ddb2Constants.ITEM, item);
                    exchange.getIn().setHeader(Ddb2Constants.OPERATION, Ddb2Operations.PutItem);
                })
                .toF("aws2-ddb://%s?amazonDDBClient=#amazonDDBClient", tableName)
                .setBody(simple("Item inserted successfully"));
    }
}
