package com.capital6.detectly;

public class Dates {

    private String reportStartDate;
    private String reportEndDate;
    private String payDate;
    private boolean valid;
    private String msg;

    public Dates(String reportStartDate, String reportEndDate, String payDate) {
        this.reportStartDate = reportStartDate;
        this.reportEndDate = reportEndDate;
        this.payDate = payDate;
        this.valid = true;
        this.msg = "No fraudulence detected.";
    }

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

    public String getReportStartDate() {
        return reportStartDate;
    }

    public void setReportStartDate(String reportStartDate) {
        this.reportStartDate = reportStartDate;
    }

    public String getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(String reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public void invalidate(String msg) {
        this.valid = false;
        this.msg = msg;
    }
}
