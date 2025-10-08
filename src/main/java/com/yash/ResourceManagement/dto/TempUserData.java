package com.yash.ResourceManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempUserData
{
    String password;
    String otp;
    String userType;

    public TempUserData(String pass,String otp,String userType)
    {
        this.password = pass;
        this.otp = otp;
        this.userType=userType;
    }
}
