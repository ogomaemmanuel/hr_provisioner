package com.ogoma.hr_provisioner.utils.helpers;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class FormatStatusCode {

    public static ResponseEntity<HashMap<Object,Object>> formatStatusCode(HashMap<Object,Object> response){
        var status_code = response.get("status");
        response.remove("status");
        return ResponseEntity.status((Integer) status_code).body(response);
    }
}
