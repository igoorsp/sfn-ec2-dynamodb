package com.example.camel.routes.stepfunctions;

import com.example.camel.service.StepFunctionsService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PostStepFunctionRoute extends RouteBuilder {

    private final StepFunctionsService stepFunctionsService;
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    public PostStepFunctionRoute(StepFunctionsService stepFunctionsService) {
        this.stepFunctionsService = stepFunctionsService;
    }

    @Override
    public void configure() throws Exception {

        // Rota para lidar com o callback
        from("direct:handleCallback")
                .doTry()
                .log("Received callback with body: ${body}")
                .log("Headers: ${headers}")
                .process(exchange -> {
                    // Log do corpo da mensagem antes da conversão
                    Map<String, Object> body = exchange.getIn().getBody(Map.class);
                    final String status = (String) body.get("status");
                    final String taskToken = (String) body.get("taskToken");

                    if (status == null || status.trim().isEmpty()) {
                        throw new IllegalArgumentException("Status is missing or empty in the message body");
                    }

                    if (taskToken == null || taskToken.trim().isEmpty()) {
                        throw new IllegalArgumentException("taskToken is missing or empty in the message body");
                    }

                    // Validar se o status é APPROVED ou REJECTED
                    if (!APPROVED.equals(status) && !REJECTED.equals(status)) {
                        throw new IllegalArgumentException("Invalid status. Expected 'APPROVED' or 'REJECTED', but got: " + status);
                    }

                    // Processar com base no status
                    if (APPROVED.equals(status)) {
                        stepFunctionsService.sendTaskSuccess(taskToken, "{\"result\": \"Task completed successfully\"}");
                        log.info("TaskToken: {} - APPROVED sent", taskToken);
                    } else {
                        stepFunctionsService.sendTaskFailure(taskToken, "Task failed", "Task was rejected or failed");
                        log.info("TaskToken: {} - FAILURE sent", taskToken);
                    }
                })
                .doCatch(Exception.class)
                .log("Error processing callback: ${exception.message}")
                .end()
                .log("Callback handled successfully");
    }
}