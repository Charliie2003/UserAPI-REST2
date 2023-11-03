package com.openApi2.OpenApi2.validation;

import com.openApi2.OpenApi2.service.DTO.BodyUserPut;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public boolean putIsValid(BodyUserPut bodyUserPut){
        if(bodyUserPut == null ){
            return false;
        }
        if (containsInvalidString(bodyUserPut.getFirst_name()) ||
                containsInvalidString(bodyUserPut.getSecond_name()) ||
                containsInvalidString(bodyUserPut.getFirst_surname()) ||
                containsInvalidString(bodyUserPut.getEmail())) {
            return false;
        }
        return true;

    }
    private boolean containsInvalidString(String str) {
        return str != null && str.trim().equalsIgnoreCase("string");
    }
}
