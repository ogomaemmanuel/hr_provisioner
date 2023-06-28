package com.ogoma.hr_provisioner.payment.dtos;

import java.util.ArrayList;

public class MpesaCallbackMetadata {
    private ArrayList<MpesaItem> Item;

    public ArrayList<MpesaItem> getItem() {
        return Item;
    }

    public void setItem(ArrayList<MpesaItem> item) {
        Item = item;
    }
}
