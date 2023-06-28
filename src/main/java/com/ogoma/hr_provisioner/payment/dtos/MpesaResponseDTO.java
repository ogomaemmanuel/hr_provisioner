package com.ogoma.hr_provisioner.payment.dtos;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

public class MpesaResponseDTO {
    private Body body;

    @JsonProperty("Body")
    public Body getBody() { return body; }
    @JsonProperty("Body")
    public void setBody(Body value) { this.body = value; }

    public static class Body {
        private StkCallback stkCallback;

        @JsonProperty("stkCallback")
        public StkCallback getStkCallback() { return stkCallback; }
        @JsonProperty("stkCallback")
        public void setStkCallback(StkCallback value) { this.stkCallback = value; }
    }


    public static class StkCallback {
        private String merchantRequestID;
        private String checkoutRequestID;
        private long resultCode;
        private String resultDesc;
        private CallbackMetadata callbackMetadata;

        @JsonProperty("MerchantRequestID")
        public String getMerchantRequestID() { return merchantRequestID; }
        @JsonProperty("MerchantRequestID")
        public void setMerchantRequestID(String value) { this.merchantRequestID = value; }

        @JsonProperty("CheckoutRequestID")
        public String getCheckoutRequestID() { return checkoutRequestID; }
        @JsonProperty("CheckoutRequestID")
        public void setCheckoutRequestID(String value) { this.checkoutRequestID = value; }

        @JsonProperty("ResultCode")
        public long getResultCode() { return resultCode; }
        @JsonProperty("ResultCode")
        public void setResultCode(long value) { this.resultCode = value; }

        @JsonProperty("ResultDesc")
        public String getResultDesc() { return resultDesc; }
        @JsonProperty("ResultDesc")
        public void setResultDesc(String value) { this.resultDesc = value; }

        @JsonProperty("CallbackMetadata")
        public CallbackMetadata getCallbackMetadata() { return callbackMetadata; }
        @JsonProperty("CallbackMetadata")
        public void setCallbackMetadata(CallbackMetadata value) { this.callbackMetadata = value; }
    }

    public static class CallbackMetadata {
        private List<Item> item;

        @JsonProperty("Item")
        public List<Item> getItem() { return item; }
        @JsonProperty("Item")
        public void setItem(List<Item> value) { this.item = value; }
    }


    public static class Item {
        private String name;
        private Object value;

        @JsonProperty("Name")
        public String getName() { return name; }
        @JsonProperty("Name")
        public void setName(String value) { this.name = value; }

        @JsonProperty("Value")
        public Object getValue() { return value; }
        @JsonProperty("Value")
        public void setValue(Object value) { this.value = value; }
    }
}
