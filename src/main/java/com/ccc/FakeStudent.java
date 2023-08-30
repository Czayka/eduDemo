package com.ccc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FakeStudent implements Serializable {
    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private String email;
    private String Name;
    private String SSN;
    private String Address;
    private String City;
    //邮编
    private String Zip;
    private String Phone;

    public String getFullBirthDay(){
        return "00"+birthYear + "-" + birthMonth + "-" + birthDay;
    }

    public String getFullAddress(){
        return Address + ", Oklahoma City, Oklahoma, " + Zip;
    }
}
