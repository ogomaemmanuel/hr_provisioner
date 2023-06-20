package com.ogoma.hr_provisioner.utils.reponses;

import java.util.HashMap;
import java.util.Map;


public class Responder {
    private HashMap<Object, Object> returnObj = new HashMap<>();

    public HashMap<Object, Object> getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object key, Object value) {
        this.returnObj.put(key,value);
    }
}
