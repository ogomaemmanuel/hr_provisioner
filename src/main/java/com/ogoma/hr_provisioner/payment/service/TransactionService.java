package com.ogoma.hr_provisioner.payment.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ogoma.hr_provisioner.payment.dtos.PaymentDTO;
import com.ogoma.hr_provisioner.payment.entities.TransactionEntity;
import com.ogoma.hr_provisioner.payment.enums.Status;
import com.ogoma.hr_provisioner.payment.repositories.TransactionRepository;
import com.ogoma.hr_provisioner.plans.entities.PlanEntity;
import com.ogoma.hr_provisioner.payment.dtos.MpesaImmediateResponse;
import com.ogoma.hr_provisioner.payment.dtos.MpesaResponseDTO;
import com.ogoma.hr_provisioner.subscriptions.entities.SubscriptionEntity;
import com.ogoma.hr_provisioner.subscriptions.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    // MPESA CONFIGS
    @Value("${mpesa.bearer}")
    private String mpesaBearer;

    @Value("${mpesa.password}")
    private String mpesaPassword;

    @Value("${mpesa.timestamp}")
    private String mpesaTimestamp;


    @Value("${mpesa.callback}")
    private String mpesaCallback;

    @Value("${mpesa.account.reference}")
    private String mpesaAccReference;

    @Value("${mpesa.transaction.description}")
    private String mpesaATransDesc;

    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;

    public TransactionService(TransactionRepository transactionRepository, SubscriptionRepository subscriptionRepository) {
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
    }


    public SubscriptionEntity mapExpiryDate(SubscriptionEntity sub){
        LocalDateTime currentTime = LocalDateTime.now();
        PlanEntity plan = sub.getPlan();
        switch(plan.getType()){
            case Annual:
                sub.setExpiryTime(currentTime.plusYears(1));
                break;
            case Monthly:
                sub.setExpiryTime(currentTime.plusMonths(1));
                break;
        }
        return sub;
    }

    public void validatePayment(MpesaResponseDTO dto) {
        var body = dto.getBody();
        var stk = body.getStkCallback();


        var trans = transactionRepository.findByMerchantRequestIDAndCheckoutRequestIDAndArchive(stk.getMerchantRequestID(),stk.getCheckoutRequestID(),false);
        var sub = subscriptionRepository.findById(trans.getSubscription().getId());

        sub.ifPresent(
                result->{
                    if(stk.getResultCode() == 0){
                        var callbackMeta = stk.getCallbackMetadata().getItem();
                        trans.setAmount((Double) callbackMeta.get(0).getValue());
                        trans.setReceiptNumber((String) callbackMeta.get(1).getValue());
                        trans.setArchive(true);
                        this.transactionRepository.save(trans);
                        result.setStatus(Status.Paid);
                        this.subscriptionRepository.save(this.mapExpiryDate(result));
                    }else{
                        result.setStatus(Status.FailedPayment);
                        trans.setArchive(true);
                        this.transactionRepository.save(trans);
                        this.subscriptionRepository.save(result);
                    }

                }
        );

    }

    public HashMap<Object, Object> repaySubscription(PaymentDTO pay) {
        var responder = new HashMap<Object,Object>();
        var sub = this.subscriptionRepository.findById(pay.getSubscriptionId());
        sub.ifPresentOrElse(
                result->{
                    var plan = result.getPlan();
                    var proceed = true;
                    LocalDateTime currentTime = LocalDateTime.now();
                    switch(result.getStatus()){
                        case Paid:
                            // TO BE DISCUSS IF TO ACCUMILATE THE DAYS IF THE REMAINING AREN'T DEPLITED
                            if(currentTime.compareTo(result.getExpiryTime()) <= 0){
                                responder.put("error","Your subscription is still valid");
                                responder.put("status",400);
                                proceed = false;
                            }
                            break;
                        case Trial:
                            responder.put("error","Trials aren't paid for");
                            responder.put("status",400);
                            proceed = false;
                            break;
                    }

                    if(proceed){
                        var mpesaResponse = this.mpesaPayPrompt(plan,result.getPhoneNumber());
                        var trans = new TransactionEntity();
                        trans.setSubscription(result);
                        trans.setMerchantRequestID(mpesaResponse.getMerchantRequestID());
                        trans.setCheckoutRequestID(mpesaResponse.getCheckoutRequestID());
                        this.transactionRepository.save(trans);
                        result.setStatus(Status.Pending);
                        this.subscriptionRepository.save(result);
                        responder.put("message", "Payment was processed successfully");
                        responder.put("status", 200);
                    }
                },
                ()->{
                    responder.put("error","Subscription with the given id doesn't exist");
                    responder.put("status",400);
                }
                );
        return responder;
    }


    public MpesaImmediateResponse mpesaPayPrompt(PlanEntity result,String phonenumber){
        URL url = null;
        try {
            System.out.println(mpesaBearer);
            System.out.println(mpesaPassword);
            System.out.println(mpesaTimestamp);
            System.out.println(mpesaCallback);
            System.out.println(mpesaAccReference);
            System.out.println(mpesaATransDesc);
            url = new URL("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer "+mpesaBearer);
            con.setDoOutput(true);


            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("BusinessShortCode", 174379);
            requestBody.put("Password", mpesaPassword);
            requestBody.put("Timestamp", mpesaTimestamp);
            requestBody.put("TransactionType", "CustomerPayBillOnline");
            requestBody.put("Amount", result.getAmount());
            requestBody.put("PartyA", phonenumber);
            requestBody.put("PartyB", 174379);
            requestBody.put("PhoneNumber", "254716276313");
            requestBody.put("CallBackURL", mpesaCallback);
            requestBody.put("AccountReference", mpesaAccReference);
            requestBody.put("TransactionDesc", mpesaATransDesc);

            Gson gson = new Gson();
            String jsonBody = gson.toJson(requestBody);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(jsonBody.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = con.getResponseCode();

            if(responseCode!=200){
                throw new IOException("Request failed with response code: " + responseCode);
            }


            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 400) {
                inputStream = con.getInputStream();  // Use getInputStream() for successful responses
            } else {
                inputStream = con.getErrorStream();  // Use getErrorStream() for error responses
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder responseBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            inputStream.close();

            String response = responseBody.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, MpesaImmediateResponse.class);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
