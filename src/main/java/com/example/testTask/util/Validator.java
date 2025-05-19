package com.example.testTask.util;

public class Validator {
     public static boolean isPhoneValid(String phone){
         return phone != null && phone.matches("\\d{11,13}");
     }
     public static boolean isEmail(String email){
         return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
     }
}
