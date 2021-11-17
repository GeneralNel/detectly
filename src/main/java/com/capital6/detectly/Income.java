package com.capital6.detectly;

public class Income {
    double rate;
    double hours;

    double total;
    double netPay;

    double ytdGross;
    double ytdNetPay;

    boolean valid;
    String msg;


    public Income(double rate, double hours, double total, double netPay, double YTDGross, double YTDNetPay) {
        this.rate = rate;
        this.hours = hours;
        this.total = total;
        this.netPay = netPay;
        this.ytdGross = YTDGross;
        this.ytdNetPay = YTDNetPay;
        this.valid = true;
        this.msg = "No fraudulence detected.";
    }

    public Income(String rate, String hours, String total, String netPay, String YTDGross, String YTDNetPay) {
        this.rate = parseFromStr(rate);
        this.hours = parseFromStr(hours);
        this.total = parseFromStr(total);
        this.netPay = parseFromStr(netPay);
        this.ytdGross = parseFromStr(YTDGross);
        this.ytdNetPay = parseFromStr(YTDNetPay);
        this.valid = true;
        this.msg = "No fraudulence detected.";
    }

    public Income() {
    }

    private double parseFromStr(String s) {
        s = s.replaceAll(",","").trim();
        return Double.parseDouble(s);
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getNetPay() {
        return netPay;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public double getYtdGross() {
        return ytdGross;
    }

    public void setYtdGross(double ytdGross) {
        this.ytdGross = ytdGross;
    }

    public double getYtdNetPay() {
        return ytdNetPay;
    }

    public void setYtdNetPay(double ytdNetPay) {
        this.ytdNetPay = ytdNetPay;
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

    public void invalidate(String msg) {
        valid = false;
        this.msg = msg;
    }
}
