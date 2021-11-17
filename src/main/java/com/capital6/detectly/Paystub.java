package com.capital6.detectly;

public class Paystub {

    OverallStats overallStats = new OverallStats();

    private Entity employee;
    private Entity employer;
    private SSN ssn;

    private Income income;
    private Deductions deductions;

    private Dates dates;

    public Paystub(String name, String ssn) {
        this.employee = new Entity(name, "");
        this.ssn = new SSN(ssn);
    }

    public Paystub(Entity employee, Entity employer, SSN ssn, Income income, Deductions deductions, Dates dates) {
        this.employee = employee;
        this.employer = employer;
        this.ssn = ssn;
        this.income = income;
        this.deductions = deductions;
        this.dates = dates;
    }

    public Paystub() {
    }

    public OverallStats getOverallStats() {
        return overallStats;
    }

    public void setOverallStats(OverallStats overallStats) {
        this.overallStats = overallStats;
    }

    public Entity getEmployee() {
        return employee;
    }

    public void setEmployee(Entity employee) {
        this.employee = employee;
    }

    public SSN getSsn() {
        return ssn;
    }

    public void setSsn(SSN ssn) {
        this.ssn = ssn;
    }

    public Entity getEmployer() {
        return employer;
    }

    public void setEmployer(Entity employer) {
        this.employer = employer;
    }

    public Deductions getDeductions() {
        return deductions;
    }

    public void setDeductions(Deductions deductions) {
        this.deductions = deductions;
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(Income income) {
        this.income = income;
    }


    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }


    public void invalidateSSN(String msg) {
        this.ssn.invalidate(msg);
        this.overallStats.fraudMessages.add(ssn.getMsg());
        this.overallStats.setFraudDetected(true);
        //System.out.println(this.overallStats.isFraudDetected());
    }
    public void invalidateEmployer(String msg) {
        this.employer.invalidate(msg);
        this.overallStats.setFraudDetected(true);
        this.overallStats.fraudMessages.add(employer.getMsg());
    }
    public void invalidateEmployee(String msg) {
        this.employee.invalidate(msg);
        this.overallStats.setFraudDetected(true);
        this.overallStats.fraudMessages.add(employee.getMsg());
    }
    public void invalidateIncome(String msg) {
        this.income.invalidate(msg);
        this.overallStats.setFraudDetected(true);
        this.overallStats.fraudMessages.add(income.getMsg());
    }
    public void invalidateDeductions(String msg) {
        this.deductions.invalidate(msg);
        this.overallStats.setFraudDetected(true);
        this.overallStats.fraudMessages.add(deductions.getMsg());
    }
    public void invalidateDates(String msg) {
        this.dates.invalidate(msg);
        this.overallStats.setFraudDetected(true);
        this.overallStats.fraudMessages.add(dates.getMsg());
    }

}
