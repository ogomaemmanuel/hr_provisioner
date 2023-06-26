package com.ogoma.hr_provisioner.config;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KubernetesClientConfig {

    @Bean
    KubernetesClient kubernetesClient() {
        KubernetesClient client = new KubernetesClientBuilder().build();
        return client;
    }



}
