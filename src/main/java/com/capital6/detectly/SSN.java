package com.capital6.detectly;

public class SSN extends EmployeeDetail{

    public SSN(String content) {
        super(content);
    }

    public SSN(String content, boolean isValid, String msg) {
        super(content, isValid, msg);
    }


    public SSN() {
    }
}