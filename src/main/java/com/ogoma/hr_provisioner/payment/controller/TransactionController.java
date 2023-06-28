package com.ogoma.hr_provisioner.payment.controller;

import com.google.gson.Gson;
import com.ogoma.hr_provisioner.payment.dtos.PaymentDTO;
import com.ogoma.hr_provisioner.payment.service.TransactionService;
import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.subscriptions.dtos.MpesaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.ogoma.hr_provisioner.utils.helpers.FormatStatusCode.formatStatusCode;

@RestController
@RequestMapping("/payments")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/repay")
    public ResponseEntity<HashMap<Object,Object>> replaySubscription(@RequestBody PaymentDTO pay){
        var paid = this.transactionService.repaySubscription(pay);
        return formatStatusCode(paid);
    }

    @PostMapping("/mpesa-callback")
    public void getPaymentStatus(@RequestBody MpesaResponseDTO dto){
        this.transactionService.validatePayment(dto);
    }

}
