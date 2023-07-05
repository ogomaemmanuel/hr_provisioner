package com.ogoma.hr_provisioner.workflow;

import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AppProvisioningActivity {


    public void createDataSchema(SubscriptionEntity subscriptionEntity);
    @ActivityMethod
    public void createNamespace(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void createSecrets(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void createConfigMaps(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void createDeployment(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void createService(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void createIngress(SubscriptionEntity subscriptionEntity);

    @ActivityMethod
    public void sendEmailToCustomer(SubscriptionEntity subscriptionEntity);

}
