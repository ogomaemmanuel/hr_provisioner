package com.ogoma.hr_provisioner.payment.dtos;

public class MpesaStkCallBack {
    private String MerchantRequestID;
    private String CheckoutRequestID;
    private Integer ResultCode;
    private String ResultDesc;

    private MpesaCallbackMetadata CallbackMetadata;

    public MpesaCallbackMetadata getCallbackMetadata() {
        return CallbackMetadata;
    }

    public void setCallbackMetadata(MpesaCallbackMetadata callbackMetadata) {
        CallbackMetadata = callbackMetadata;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }

    public Integer getResultCode() {
        return ResultCode;
    }

    public void setResultCode(Integer resultCode) {
        ResultCode = resultCode;
    }

    public String getResultDesc() {
        return ResultDesc;
    }

    public void setResultDesc(String resultDesc) {
        ResultDesc = resultDesc;
    }
}
