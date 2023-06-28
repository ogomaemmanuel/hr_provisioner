package com.ogoma.hr_provisioner.subscriptions.controllers;

import com.ogoma.hr_provisioner.subscriptions.SubsciptionQuery;
import com.ogoma.hr_provisioner.subscriptions.dtos.SubscriptionDto;
import com.ogoma.hr_provisioner.subscriptions.services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.ogoma.hr_provisioner.utils.helpers.FormatStatusCode.formatStatusCode;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<HashMap<Object,Object>> getAllSubscription(SubsciptionQuery query){
        var subscriptions = this.subscriptionService.getAllSubscriptions(query);
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping
    public ResponseEntity<HashMap<Object,Object>> addASubscription(@Valid @RequestBody SubscriptionDto data) {
        var response = this.subscriptionService.addASubscription(data);
        return formatStatusCode(response);
    }

    @PostMapping("/trial")
    public ResponseEntity<HashMap<Object,Object>> addAFreeTrial(@Valid @RequestBody SubscriptionDto data){
        var response = this.subscriptionService.addATrialSubscription(data);
        return formatStatusCode(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> getASubscription(@PathVariable Long id){
        var response = this.subscriptionService.getASubscription(id);
        return formatStatusCode(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> softDeleteASubscription(@PathVariable Long id){
        var response = this.subscriptionService.softDeleteASubscription(id);
        return formatStatusCode(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HashMap<Object,Object>> updateASubscription(@PathVariable Long id,@RequestBody SubscriptionDto data){
        var response = this.subscriptionService.updateASubscription(id,data);
        return formatStatusCode(response);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<HashMap<Object,Object>> cancelASubscription(@PathVariable Long id){
        var message = this.subscriptionService.cancelASubscription(id);
        return formatStatusCode(message);
    }


}
