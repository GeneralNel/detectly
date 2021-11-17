package com.capital6.detectly;

public class EmployeeDetail {

    String content;
    boolean isValid;
    String msg;

    public EmployeeDetail(String content) {
        this.content = content;
        this.isValid = true;
        this.msg = "No fraudulence detected.";
    }

    public EmployeeDetail(String content, boolean isValid, String msg) {
        this.content = content;
        this.isValid = isValid;
        this.msg = msg;
    }

    public EmployeeDetail() {
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isValid() {
        return isValid;
    }
    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void invalidate(String msg) {
        this.isValid = false;
        this.msg = msg;
    }

}
