package com.capital6.detectly;

public class Deductions {

    FinancialValue ficaMedicare;
    FinancialValue ficaSocialSecurity;
    FinancialValue fedTax;
    FinancialValue stateTax;

    FinancialValue overall;

    boolean valid;
    String msg;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Deductions(FinancialValue ficaMedicare, FinancialValue ficaSocialSecurity, FinancialValue fedTax, FinancialValue stateTax, FinancialValue overall) {
        this.ficaMedicare = ficaMedicare;
        this.ficaSocialSecurity = ficaSocialSecurity;
        this.fedTax = fedTax;
        this.stateTax = stateTax;
        this.overall = overall;
        this.valid = true;
        this.msg = "No fraudulence detected.";
    }

    public Deductions() {

    }

    public FinancialValue getFicaMedicare() {
        return ficaMedicare;
    }

    public void setFicaMedicare(FinancialValue ficaMedicare) {
        this.ficaMedicare = ficaMedicare;
    }

    public FinancialValue getFicaSocialSecurity() {
        return ficaSocialSecurity;
    }

    public void setFicaSocialSecurity(FinancialValue ficaSocialSecurity) {
        this.ficaSocialSecurity = ficaSocialSecurity;
    }

    public FinancialValue getFedTax() {
        return fedTax;
    }

    public void setFedTax(FinancialValue fedTax) {
        this.fedTax = fedTax;
    }

    public FinancialValue getStateTax() {
        return stateTax;
    }

    public void setStateTax(FinancialValue stateTax) {
        this.stateTax = stateTax;
    }

    public FinancialValue getOverall() {
        return overall;
    }

    public void setOverall(FinancialValue overall) {
        this.overall = overall;
    }

    public void invalidate(String msg) {
        valid = false;
        this.msg = msg;
    }
}
