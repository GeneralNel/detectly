package com.capital6.detectly;

public class FinancialValue {
    public double total;
    public double ytdTotal;


    public FinancialValue(double total, double YTDtotal) {
        this.total = total;
        this.ytdTotal = YTDtotal;
    }

    public FinancialValue(String total, String YTDtotal) {
        this.total = parseFromStr(total);
        this.ytdTotal = parseFromStr(YTDtotal);
    }

    private double parseFromStr(String s) {
        s = s.replaceAll(",","").trim();
        return Double.parseDouble(s);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getYtdTotal() {
        return ytdTotal;
    }

    public void setYtdTotal(double ytdTotal) {
        this.ytdTotal = ytdTotal;
    }
}
