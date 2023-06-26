package com.ogoma.hr_provisioner.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AppProvisioningActivity {


    public String createDataSchema(String clientName);
    @ActivityMethod
    public void createNamespace(String namespace);

    @ActivityMethod
    public void createSecrets(String namespace);

    @ActivityMethod
    public void createConfigMaps(String namespace);

    @ActivityMethod
    public void createDeployment(String namespace);

    @ActivityMethod
    public void createService(String namespace);

    @ActivityMethod
    public void createIngress(String namespace);

    @ActivityMethod
    public void sendEmailToCustomer();

}
