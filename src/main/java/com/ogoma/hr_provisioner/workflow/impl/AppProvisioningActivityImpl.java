package com.ogoma.hr_provisioner.workflow.impl;

import com.ogoma.hr_provisioner.config.HrAppConfigProperties;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import com.ogoma.hr_provisioner.workflow.AppProvisioningActivity;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppProvisioningActivityImpl implements AppProvisioningActivity {
    private final KubernetesClient kubernetesClient;

    private final HrAppConfigProperties hrAppConfigProperties;

    public AppProvisioningActivityImpl(KubernetesClient kubernetesClient, HrAppConfigProperties hrAppConfigProperties) {
        this.kubernetesClient = kubernetesClient;
        this.hrAppConfigProperties = hrAppConfigProperties;
    }

    @Override
    public void createDataSchema(SubscriptionEntity subscriptionEntity) {
        String schemaName = generateSchemaName(subscriptionEntity);
        try (Connection connection = DriverManager.getConnection(
                this.hrAppConfigProperties.getDbHost(),
                this.hrAppConfigProperties.getDbUsername(), this.hrAppConfigProperties.getDbPassword())) {
            String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            connection.createStatement().execute(createSchemaSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createNamespace(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
        NamespaceBuilder namespaceBuilder = new NamespaceBuilder();
        namespaceBuilder.withNewMetadata().withName(namespace).endMetadata();
        this.kubernetesClient.namespaces().resource(namespaceBuilder.build()).create();
    }

    @Override
    public void createSecrets(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
        Secret secret = new SecretBuilder()
                .withNewMetadata()
                .withName("hr-secret")
                .withNamespace(namespace)
                .endMetadata()
                .addToData("DB_HOST",
                        this.hrAppConfigProperties.getDbHost())
                .addToData("DB_SCHEMA", generateSchemaName(subscriptionEntity))
                .addToData("DB_PASSWORD", this.hrAppConfigProperties.getDbPassword())
                .build();
        this.kubernetesClient.secrets().resource(secret).create();
    }

    @Override
    public void createConfigMaps(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
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
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
        // Create a new deployment
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName("hr-app-deployment")
                .withLabels(Map.of("app", "hr-app"))
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .withNewSelector()
                .addToMatchLabels("app", "hr-app")
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", "hr-app")
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("hr-app")
                .withImage("nginx:latest")
                .withEnvFrom(new EnvFromSourceBuilder()
                        .withSecretRef(new SecretEnvSourceBuilder()
                                .withName("hr-secret")

                                .build())
                        .build())
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        kubernetesClient.apps().deployments().inNamespace(namespace).resource(deployment).create();

    }

    @Override
    public void createService(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName("hr-app-service")
                .withLabels(Map.of("app","hr-app"))
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withName("http")
                .withProtocol("TCP")
                .withPort(8080)
                .withNewTargetPort(8080)
                .endPort()
                .withSelector(new HashMap<String, String>() {{
                    put("app", "hr-app");
                }})
                .endSpec()
                .build();
        kubernetesClient.services().inNamespace(namespace).resource(service).create();

    }

    @Override
    public void createIngress(SubscriptionEntity subscriptionEntity) {
        String namespace = String.format("sub-%d", subscriptionEntity.getId());
        String subDomain = namespace + "." + hrAppConfigProperties.getBaseDomain();
        Ingress ingress = new IngressBuilder()
                .withNewMetadata()
                .withName("hr-ingress")
                .withLabels(Map.of("app", "hr-app"))
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost(subDomain)
                .withNewHttp()
                .addNewPath()
                .withPath("/")
                .withPathType("Prefix")
                .withNewBackend()
                .withService((new IngressServiceBackendBuilder().withName("http").withNewPort("", 8080).build()))
                .endBackend()
                .endPath()
                .endHttp()
                .endRule()
                .endSpec()
                .build();
        kubernetesClient.network().v1().ingresses().inNamespace(namespace).resource(ingress).create();

    }

    @Override
    public void sendEmailToCustomer(SubscriptionEntity subscriptionEntity) {

    }

    private String generateSchemaName(SubscriptionEntity subscriptionEntity) {
        String schemaName = String.format("sub-%d", subscriptionEntity.getId());
        return schemaName;
    }
}
