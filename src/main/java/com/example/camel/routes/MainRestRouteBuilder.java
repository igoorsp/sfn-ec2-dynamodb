package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainRestRouteBuilder extends RouteBuilder {

    private static final String STATUS = "status";

    @Override
    public void configure() {

        // Configuração do REST DSL
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .contextPath("/")
                .port(8080);

        // AWS DynamoDB
        // GetByStatus
        rest("/tasks")
                .get()
                .param().name(STATUS).required(true).endParam()
                .to("direct:getItemsByStatus");

        // Endpoint para inserir um item no DynamoDB (PUT)
        rest("/tasks")
                .put()
                .type(Map.class) // Recebe um JSON como input
                .to("direct:putTask");

        // AWS Step Functions
        // Endpoint POST
        rest("/callback")
                .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:handleCallback");
    }
}
