package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class GetDynamoDbRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Configuração do REST DSL
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        // Endpoint GET
        rest("/tasks")
                .get()
                .param().name("status").defaultValue("PENDING").endParam()
                .to("direct:getTasks");

        // Rota para buscar itens no DynamoDB
        from("direct:getTasks")
                .log("Fetching tasks with status: ${header.status}")
                .toD("aws2-ddb://MyTable?operation=query" +
                        "&keyAttributeName=status" +
                        "&keyAttributeValues=${header.status}" +
                        "&attributeNames=transactionId,taskToken,startTime,orderId,status")
                .log("Query result: ${body}");
    }
}