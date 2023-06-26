package com.ogoma.hr_provisioner.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AppProvisioningWorkflow {
    @WorkflowMethod
    public void startProvisioning(String subscriptionId);
}
