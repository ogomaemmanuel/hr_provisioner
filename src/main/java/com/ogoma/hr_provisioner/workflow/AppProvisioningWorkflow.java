package com.ogoma.hr_provisioner.workflow;

import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AppProvisioningWorkflow {
    @WorkflowMethod
    public void startProvisioning(SubscriptionEntity subscriptionEntity);
}
