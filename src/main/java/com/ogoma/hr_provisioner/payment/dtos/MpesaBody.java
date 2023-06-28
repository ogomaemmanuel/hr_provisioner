package com.ogoma.hr_provisioner.payment.dtos;

public class MpesaBody {
    private MpesaStkCallBack stkCallback;

    public MpesaStkCallBack getStkCallback() {
        return stkCallback;
    }

    public void setStkCallback(MpesaStkCallBack stkCallback) {
        this.stkCallback = stkCallback;
    }
}
