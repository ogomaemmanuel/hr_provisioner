package com.ogoma.hr_provisioner.workflow.workers;

import com.ogoma.hr_provisioner.workflow.AppProvisioningActivity;
import com.ogoma.hr_provisioner.workflow.impl.AppProvisioningWorkflowImpl;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class AppProvisioningWorker {

    private static final String  workerQueueName="APP_PROVISIONING";
    private  WorkerFactory workerFactory;
    private  final AppProvisioningActivity appProvisioningActivity;

    public AppProvisioningWorker(AppProvisioningActivity appProvisioningActivity) {
        this.appProvisioningActivity = appProvisioningActivity;
    }

    @PostConstruct
   public void start() {
       var worker = workerFactory.newWorker(workerQueueName);
       worker.registerWorkflowImplementationTypes(AppProvisioningWorkflowImpl.class);
       worker.registerActivitiesImplementations(appProvisioningActivity);
       workerFactory.start();
   }

    @PreDestroy
    public void destroy() {
        if (workerFactory != null) {
            workerFactory.shutdown();
        }
    }
}
