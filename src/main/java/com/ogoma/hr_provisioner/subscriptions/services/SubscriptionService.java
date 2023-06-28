package com.ogoma.hr_provisioner.subscriptions.services;

import com.ogoma.hr_provisioner.payment.entities.TransactionEntity;
import com.ogoma.hr_provisioner.payment.repositories.TransactionRepository;
import com.ogoma.hr_provisioner.payment.service.TransactionService;
import com.ogoma.hr_provisioner.plans.repositories.PlanRepository;
import com.ogoma.hr_provisioner.subscriptions.SubsciptionQuery;
import com.ogoma.hr_provisioner.subscriptions.dtos.SubscriptionDto;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import com.ogoma.hr_provisioner.payment.enums.Status;
import com.ogoma.hr_provisioner.subscriptions.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SubscriptionService {

//    @Value("${spring.mpesa.password}")
//    private String password;
//
//
//    @Value("${spring.mpesa.token}")
//    private String token;
//
//    @Value("${spring.mpesa.callback}")
//    private String callback;

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
            subscription.setSubDomain(data.getSubDomain());
            subscription.setPhoneNumber(data.getPhoneNumber());

            // CURRENT TIME
            LocalDateTime currentTime = LocalDateTime.now();
            subscription.setStatus(Status.Pending);

            AtomicBoolean proceed = new AtomicBoolean(true);

            var existingRunningSubscription = subscriptionRepository.findTopByEmailAndArchiveOrderByCreatedAtDesc(data.getEmail(),false);

            existingRunningSubscription.ifPresent(sub->{
                switch (sub.getStatus()){
                    case Paid:
                        if(currentTime.compareTo(sub.getExpiryTime()) <= 0){
                            responder.put("error","You have another running subscription, consider upgrading");
                            responder.put("status",400);
                            proceed.set(false);
                        }
                        break;
                    case Pending:
                        responder.put("error","You have an unpaid subscription.");
                        responder.put("status",400);
                        proceed.set(false);
                        break;
                    case FailedPayment:
                        responder.put("error","You have a subscription with a failed payment.");
                        responder.put("status",400);
                        proceed.set(false);
                        break;
                }
            });

            if(proceed.get()){
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

                this.subscriptionRepository.save(result);
                responder.put("data","Updated successfully");
                responder.put("status",200);
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
            subscription.setSubDomain(data.getSubDomain());
            subscription.setPhoneNumber(data.getPhoneNumber());

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
                subscription.setExpiryTime(currentTime.plusSeconds(10));
                this.subscriptionRepository.save(subscription);
                responder.put("message","You have successfully subscribed to your free trial.");
                responder.put("status",201);
            });



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
