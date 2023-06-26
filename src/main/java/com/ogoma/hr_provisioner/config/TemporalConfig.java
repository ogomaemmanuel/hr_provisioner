package com.ogoma.hr_provisioner.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Bean
    WorkflowClient workflowClient() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        var clientOptions= WorkflowClientOptions.newBuilder()
                .build();
        WorkflowClient client = WorkflowClient.newInstance(service,clientOptions);

        return client;
    }
    @Bean
    WorkerFactory workerFactory() {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient());
        return factory;
    }

}
