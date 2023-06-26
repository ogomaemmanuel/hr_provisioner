package com.ogoma.hr_provisioner.workflow.impl;

import com.ogoma.hr_provisioner.workflow.AppProvisioningActivity;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppProvisioningActivityImpl implements AppProvisioningActivity {
    private final KubernetesClient kubernetesClient;

    public AppProvisioningActivityImpl(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public String createDataSchema(String clientName) {
        return null;
    }

    @Override
    public void createNamespace(String namespace) {
        this.kubernetesClient.namespaces().withName(namespace).create();
    }

    @Override
    public void createSecrets(String namespace) {
        Secret secret = new SecretBuilder()
                .withNewMetadata()
                .withName("hr_secret")
                .withNamespace(namespace)
                .endMetadata()
                .addToData("username", "admin")
                .addToData("password", "secret")
                .build();
         this.kubernetesClient.secrets().resource(secret).create();

    }

    @Override
    public void createConfigMaps(String namespace) {
        ConfigMap secret = new ConfigMapBuilder()
                .withNewMetadata()
                .withName("hr_config")
                .withNamespace(namespace)
                .endMetadata()
                .addToData("username", "admin")
                .addToData("password", "secret")
                .build();
    }

    @Override
    public void createDeployment(String namespace) {

    }

    @Override
    public void createService(String namespace) {

    }

    @Override
    public void createIngress(String namespace) {

    }

    @Override
    public void sendEmailToCustomer() {

    }
}
