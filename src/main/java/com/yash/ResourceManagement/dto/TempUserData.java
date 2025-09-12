package com.yash.ResourceManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempUserData
{
    String password;
    String otp;

    public TempUserData(String pass,String otp)
    {
        this.password = pass;
        this.otp = otp;
    }
}
