package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class PostStepFunctionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Configuração do REST DSL
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        // Endpoint POST
        rest("/callback")
                .post()
                .consumes("application/json")
                .to("direct:handleCallback");

        // Rota para lidar com o callback
        from("direct:handleCallback")
                .log("Received callback with taskToken: ${body.taskToken}")
                .choice()
                    .when(simple("${body.status} == 'SUCCESS'"))
                        .to("aws2-stepfunctions://MyStateMachine?operation=sendTaskSuccess&taskToken=${body.taskToken}")
                    .otherwise()
                        .to("aws2-stepfunctions://MyStateMachine?operation=sendTaskFailure&taskToken=${body.taskToken}")
                .endChoice()
                .log("Callback handled successfully");
    }
}