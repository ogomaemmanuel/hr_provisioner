package com.ogoma.hr_provisioner.workflow.impl;

import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import com.ogoma.hr_provisioner.workflow.AppProvisioningActivity;
import com.ogoma.hr_provisioner.workflow.AppProvisioningWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class AppProvisioningWorkflowImpl implements AppProvisioningWorkflow {

    private final AppProvisioningActivity provisioningActivity = Workflow.newActivityStub(AppProvisioningActivity.class,
            ActivityOptions.newBuilder()
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setBackoffCoefficient(1)
                            .build())
                    .setStartToCloseTimeout(Duration.ofDays(1))
                    .build());
    @Override
    public void startProvisioning(SubscriptionEntity subscriptionEntity) {
        provisioningActivity.createDataSchema(subscriptionEntity);
        provisioningActivity.createNamespace(subscriptionEntity);
        provisioningActivity.createSecrets(subscriptionEntity);
        provisioningActivity.createDeployment(subscriptionEntity);
        provisioningActivity.createService(subscriptionEntity);
        provisioningActivity.createIngress(subscriptionEntity);
        provisioningActivity.sendEmailToCustomer(subscriptionEntity);
    }
}
