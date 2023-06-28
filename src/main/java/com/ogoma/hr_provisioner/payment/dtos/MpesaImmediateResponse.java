package com.ogoma.hr_provisioner.payment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MpesaImmediateResponse {

    private String merchantRequestID;
    private String checkoutRequestID;
    private String responseCode;
    private String responseDescription;
    private String customerMessage;


    @JsonProperty("MerchantRequestID")
    public String getMerchantRequestID() {
        return merchantRequestID;
    }
    @JsonProperty("MerchantRequestID")
    public void setMerchantRequestID(String merchantRequestID) {
        this.merchantRequestID = merchantRequestID;
    }

    @JsonProperty("CheckoutRequestID")
    public String getCheckoutRequestID() {
        return checkoutRequestID;
    }
    @JsonProperty("CheckoutRequestID")
    public void setCheckoutRequestID(String checkoutRequestID) {
        this.checkoutRequestID = checkoutRequestID;
    }

    @JsonProperty("ResponseCode")
    public String getResponseCode() {
        return responseCode;
    }

    @JsonProperty("ResponseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("ResponseDescription")
    public String getResponseDescription() {
        return responseDescription;
    }

    @JsonProperty("ResponseDescription")
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    @JsonProperty("CustomerMessage")
    public String getCustomerMessage() {
        return customerMessage;
    }

    @JsonProperty("CustomerMessage")
    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
    }
}
