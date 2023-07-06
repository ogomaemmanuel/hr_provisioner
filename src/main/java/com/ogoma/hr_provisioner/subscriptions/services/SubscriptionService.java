package com.ogoma.hr_provisioner.subscriptions.services;

import com.ogoma.hr_provisioner.payment.entities.TransactionEntity;
import com.ogoma.hr_provisioner.payment.repositories.TransactionRepository;
import com.ogoma.hr_provisioner.payment.service.TransactionService;
import com.ogoma.hr_provisioner.plans.enums.Type;
import com.ogoma.hr_provisioner.plans.repositories.PlanRepository;
import com.ogoma.hr_provisioner.subscriptions.SubsciptionQuery;
import com.ogoma.hr_provisioner.subscriptions.dtos.SubscriptionDto;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import com.ogoma.hr_provisioner.payment.enums.Status;
import com.ogoma.hr_provisioner.subscriptions.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository, TransactionRepository transactionRepository, TransactionService transactionService) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }



    public HashMap<Object, Object> addASubscription(SubscriptionDto data) {

        // RESPONDER
        var responder = new HashMap<>();


        // GET THE PLAN
        var plan = this.planRepository.findById(data.getPlanId());
        plan.ifPresentOrElse(result->{
            var subscription = new SubscriptionEntity();
            subscription.setEmail(data.getEmail());
            subscription.setPlan(result);
            subscription.setFirstName(data.getFirstName());
            subscription.setLastName((data.getLastName()));
            subscription.setPhoneNumber(data.getPhoneNumber());

            // CURRENT TIME
            LocalDateTime currentTime = LocalDateTime.now();

            // VALIDATE SUBDOMAIN
            var checkExistingSubscriptions = this.subscriptionRepository.findBySubDomainAndArchive(data.getSubDomain(),false);
            checkExistingSubscriptions.ifPresentOrElse(
                    present->{
                        responder.put("error","SubDomain already taken.");
                        responder.put("status",400);
                    },
                    ()->{
                        subscription.setSubDomain(data.getSubDomain());
                        subscription.setStatus(Status.Pending);

                        var mpesaResponse = this.transactionService.mpesaPayPrompt(result,subscription.getPhoneNumber());
                        var addedSubscription = this.subscriptionRepository.save(subscription);

                        var trans = new TransactionEntity();
                        trans.setSubscription(addedSubscription);
                        trans.setMerchantRequestID(mpesaResponse.getMerchantRequestID());
                        trans.setCheckoutRequestID(mpesaResponse.getCheckoutRequestID());
                        this.transactionRepository.save(trans);
                        responder.put("data", addedSubscription);
                        responder.put("status", 201);
                    }
            );


        },()->{
            responder.put("error","Plan with the given id doesn't exist");
            responder.put("status",400);
        });

        return responder;

    }

    public HashMap<Object,Object> getAllSubscriptions() {
        var responder = new HashMap<>();
        var subscriptions = this.subscriptionRepository.findAll();
        responder.put("data",subscriptions);
        return responder;

    }

    public HashMap<Object,Object> getAllSubscriptions(SubsciptionQuery query) {
        query.setArchive(false);
        var responder = new HashMap<>();
        var subscriptions = this.subscriptionRepository.findAll(query.toSpec());
        responder.put("data",subscriptions);
        return responder;

    }

    public HashMap<Object, Object> getASubscription(Long id) {
        var subscription = this.subscriptionRepository.findById(id);
        var responder = new HashMap<>();
        subscription.ifPresentOrElse(result->{
            if(!result.isArchive()) {
                responder.put("data", result);
                responder.put("status", 200);
            }else{
                responder.put("error","Subscription with the given id doesn't exists");
                responder.put("status",400);
            }
        },()->{
            responder.put("error","Subscription with the given id doesn't exists");
            responder.put("status",400);
        });
        return responder;
    }

    public HashMap<Object, Object> softDeleteASubscription(Long id) {
        var record = this.subscriptionRepository.findById(id);
        var responder = new HashMap<>();
        record.ifPresentOrElse(result->{
            if (!result.isArchive()){
                result.setArchive(true);
                this.subscriptionRepository.save(result);
                responder.put("message","Subscription delete successfully");
                responder.put("status",200);
            }else{
                responder.put("error","Subscription with the given id doesn't exists");
                responder.put("status",400);
            }
        },()->{
            responder.put("error","Subscription with the given id doesn't exist");
            responder.put("status",400);
        });
        return responder;
    }

    public HashMap<Object, Object> updateASubscription(Long id, SubscriptionDto data) {
        var results = this.subscriptionRepository.findById(id);
        var responder = new HashMap<>();
        results.ifPresentOrElse(result->{

            // PLAN UPDATE TO BE HANDLED LATER

            if(!result.isArchive()) {

                if (data.getEmail() != null) {
                    result.setEmail(data.getEmail());
                }

                if (data.getFirstName() != null) {
                    result.setFirstName(data.getFirstName());
                }

                if (data.getLastName() != null) {
                    result.setLastName(data.getLastName());
                }

                if (data.getSubDomain() != null) {
                    result.setSubDomain(data.getSubDomain());
                }

                if (data.getPhoneNumber() != null) {
                    result.setPhoneNumber(data.getPhoneNumber());
                }

                if (data.getPlanId()!=null) {
                    // UPGRADING A SUBSCRIPTION
                    if(Objects.equals(data.getPlanId(), result.getPlan().getId())){
                        responder.put("error","Provide a different plan");
                        responder.put("status",400);
                    }else {
                        var newPlan = this.planRepository.findById(data.getPlanId());
                        newPlan.ifPresentOrElse(
                                present -> {

                                    switch (result.getPlan().getType()) {
                                        case Monthly:
                                            if (present.getType() == Type.Annual) {
                                                // PROMPT PAYMENT
                                                var mpesaResponse = this.transactionService.mpesaPayPrompt(present, result.getPhoneNumber());
                                                result.setStatus(Status.Pending);
                                                result.setPlan(present);
                                                var trans = new TransactionEntity();
                                                trans.setSubscription(result);
                                                trans.setMerchantRequestID(mpesaResponse.getMerchantRequestID());
                                                trans.setCheckoutRequestID(mpesaResponse.getCheckoutRequestID());
                                                this.transactionRepository.save(trans);
                                                this.subscriptionRepository.save(result);
                                                responder.put("message", "Upgrade Processed successfully");
                                                responder.put("status", 200);
                                            }else{
                                                responder.put("error","Can't downgrade");
                                                responder.put("status",400);
                                            }
                                            break;
                                        case Annual:
                                            responder.put("error","you have the max subscription");
                                            responder.put("status",400);
                                            break;
                                    }
                                },
                                () -> {
                                    responder.put("error", "Provided plan doesn't exists");
                                    responder.put("status", 400);
                                }
                        );
                    }
                }else {
                    this.subscriptionRepository.save(result);
                    responder.put("message", "Updated successfully");
                    responder.put("status", 200);
                }
            }else{
                responder.put("error","Record with the given id doesn't exists");
                responder.put("status",400);
            }

        },()->{
            responder.put("errors","Record with the given id doesn't exists");
            responder.put("status",400);
        });
        return responder;
    }

    public HashMap<Object, Object> addATrialSubscription(SubscriptionDto data) {

        // RESPONDER
        var responder = new HashMap<>();


        // GET THE PLAN
        var plan = this.planRepository.findById(data.getPlanId());
        plan.ifPresentOrElse(result->{
            // CURRENT TIME
            LocalDateTime currentTime = LocalDateTime.now();

            var subscription = new SubscriptionEntity();
            subscription.setEmail(data.getEmail());
            subscription.setPlan(result);
            subscription.setFirstName(data.getFirstName());
            subscription.setLastName((data.getLastName()));
            subscription.setPhoneNumber(data.getPhoneNumber());

            // VALIDATE THAT THE SELECTED DOMAIN ISN'T IN USE
            var userSelectedSubdomain = data.getSubDomain() + "-trial";
            var checkExisingTrial = this.subscriptionRepository.findBySubDomainAndExpiryTimeGreaterThanEqualAndStatus(userSelectedSubdomain,currentTime, Status.Trial);
            checkExisingTrial.ifPresentOrElse(
                    present->{
                        responder.put("error","SubDomain already taken!");
                        responder.put("status",400);
                    },
                    ()->{
                        subscription.setSubDomain(userSelectedSubdomain);
                        var existingTrialSubscription = subscriptionRepository.findTopByEmailOrderByCreatedAtDesc(data.getEmail());

                        existingTrialSubscription.ifPresentOrElse(trial->{
                            var expired = currentTime.compareTo(trial.getExpiryTime());
                            if (expired <= 0) {
                                responder.put("error","Your trial hasn't yet expired.");
                                responder.put("status",400);
                            }else {
                                responder.put("error", "You already used up your free trial");
                                responder.put("status", 400);
                            }
                        },()->{
                            subscription.setStatus(Status.Trial);
                            subscription.setExpiryTime(currentTime.plusDays(14));
                            this.subscriptionRepository.save(subscription);
                            responder.put("message","You have successfully subscribed to your free trial.");
                            responder.put("status",201);
                        });
                    }
            );
            subscription.setSubDomain(data.getSubDomain() + "-trial");



        },()->{
            responder.put("error","Plan with the given id doesn't exist");
            responder.put("status",400);
        });

        return responder;



    }

    public HashMap<Object, Object> cancelASubscription(Long id) {
        var responder = new HashMap<Object,Object>();
        var sub = this.subscriptionRepository.findById(id);
        sub.ifPresentOrElse(
                result->{
                    if(result.isArchive()){
                        responder.put("error","Subscription with the given id doesn't exists");
                        responder.put("status",400);
                    }else {
                        result.setArchive(true);
                        this.subscriptionRepository.save(result);
                        responder.put("message", "Subscription canceled successfully.");
                        responder.put("status", 200);
                    }
                },
                ()->{
                    responder.put("error","Subscription with the given id doesn't exists");
                    responder.put("status",400);
                }
        );
        return responder;
    }
}
