package com.ogoma.hr_provisioner.workflow.impl;

import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
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
    public String createDataSchema(SubscriptionEntity subscriptionEntity) {
        return null;
    }

    @Override
    public void createNamespace(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());
        this.kubernetesClient.namespaces().withName(namespace).create();
    }

    @Override
    public void createSecrets(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());
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
    public void createConfigMaps(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());
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
    public void createDeployment(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());

    }

    @Override
    public void createService(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());

    }

    @Override
    public void createIngress(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d",subscriptionEntity.getId());

    }

    @Override
    public void sendEmailToCustomer(SubscriptionEntity subscriptionEntity) {

    }
}
