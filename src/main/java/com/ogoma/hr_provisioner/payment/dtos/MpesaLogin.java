package com.ogoma.hr_provisioner.payment.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MpesaLogin {

    private String accessToken;
    private String expiry;

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }
    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    @JsonProperty("expires_in")
    public String getExpiry() {
        return expiry;
    }
    @JsonProperty("expires_in")
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
