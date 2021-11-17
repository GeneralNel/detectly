package com.capital6.detectly;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class KVExtractor {
    public static Paystub extractKeyValueToPaystub(File file) {

        HashMap< String, String > payStubMap = new HashMap < String, String > ();

        try {
            Scanner sc = new Scanner(file);
            int lineNumber = 1;

            while (sc.hasNextLine()) {

                String line = sc.nextLine();
                if (lineNumber == 1) {
                    payStubMap.put("Company Name", line);
                }
                if (lineNumber == 2) {

                    String s[] = line.split(" E");
                    payStubMap.put("Company Address", s[0]);
                }
                if (lineNumber == 4) {

                    String s[] = line.split(" No.");
                    String name = s[0];
//                    int idx = s[0].indexOf('|');
//                    if (idx < 0) idx = name.length();
//                    name = name.substring(0, idx);
                    payStubMap.put("Employee Name", name);

                }
                if (lineNumber == 5) {

                    String s[] = line.split(" X");
                    payStubMap.put("Employee Address", s[0]);
                    String y[] = line.split("-XX-");
                    String z[] = y[1].split(" ");
                    payStubMap.put("SSN", "XXX-XX-" + z[0]);
                    payStubMap.put("Start Date", z[1]);
                    payStubMap.put("End Date", z[3]);
                    payStubMap.put("Pay Date", z[4]);
                    payStubMap.put("Employee Number", z[5]);

                }

                if (lineNumber == 7) {

                    String s[] = line.split("G ");
                    String g[] = s[1].split(" ");
                    payStubMap.put("Rate", g[0].substring(1));
                    payStubMap.put("Hours", g[1]);
                    payStubMap.put("Current Pay", g[2].substring(1));

                }

                if (lineNumber == 8) {
                    String s[] = line.split(" ");
                    payStubMap.put("ficaMedTotal", s[1].substring(1));
                    payStubMap.put("ficaMedYTD", s[2].substring(1));

                }

                if (lineNumber == 9) {
                    String s[] = line.split("TY ");
                    String g[] = s[1].split(" ");
                    payStubMap.put("ficaSSTotal", g[0].substring(1));
                    payStubMap.put("ficaSSYTD", g[1].substring(1));

                }

                if (lineNumber == 10) {
                    String s[] = line.split("AX ");
                    String g[] = s[1].split(" ");
                    payStubMap.put("FederalTaxTotal", g[0].substring(1));
                    payStubMap.put("FederalTaxYTD", g[1].substring(1));

                }

                if (lineNumber == 11) {
                    String s[] = line.split("AX ");
                    String g[] = s[1].split(" ");
                    payStubMap.put("StateTaxTotal", g[0].substring(1));
                    payStubMap.put("StateTaxYTD", g[1].substring(1));

                }

                if (lineNumber == 13) {
                    String s[] = line.split(" ");
                    payStubMap.put("YTD Gross", s[0].substring(1));
                    payStubMap.put("YTD Deductions", s[1].substring(1));
                    payStubMap.put("YTD Net Pay", s[2].substring(1));
                    payStubMap.put("Total", s[3].substring(1));
                    payStubMap.put("Deductions", s[4].substring(1));
                    payStubMap.put("Net Pay", s[5].substring(1));
                }

                lineNumber++;

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return createPaystubObj(payStubMap);
    }

    public static Paystub createPaystubObj(HashMap<String, String> payStubMap) {
        // set employee info
        Entity employee = new Entity(payStubMap.get("Employee Name"),payStubMap.get("Employee Address"));
        Entity employer = new Entity(payStubMap.get("Company Name"),payStubMap.get("Company Address"));
        SSN ssn = new SSN(payStubMap.get("SSN"));

        // set income
        Income income = new Income(payStubMap.get("Rate"),payStubMap.get("Hours"), payStubMap.get("Current Pay"), payStubMap.get("Net Pay"), payStubMap.get("YTD Gross"), payStubMap.get("YTD Net Pay"));

        // set dates
        String reportStartDate = payStubMap.get("Start Date");
        String reportEndDate = payStubMap.get("End Date");
        String payDate = payStubMap.get("Pay Date");
        Dates dates = new Dates(reportStartDate, reportEndDate, payDate);

        // set deductions
        FinancialValue ficaMedicare = new FinancialValue(payStubMap.get("ficaMedTotal"), payStubMap.get("ficaMedYTD"));
        FinancialValue ficaSocialSecurity  = new FinancialValue(payStubMap.get("ficaSSTotal"), payStubMap.get("ficaSSYTD"));
        FinancialValue fedTax = new FinancialValue(payStubMap.get("FederalTaxTotal"), payStubMap.get("FederalTaxYTD"));
        FinancialValue stateTax =  new FinancialValue(payStubMap.get("StateTaxTotal"), payStubMap.get("StateTaxYTD"));
        FinancialValue overall =  new FinancialValue(payStubMap.get("Deductions"), payStubMap.get("YTD Deductions"));
        Deductions deductions = new Deductions(ficaMedicare, ficaSocialSecurity, fedTax, stateTax, overall);

        Paystub paystub = new Paystub(employee, employer,ssn, income, deductions, dates);

        return paystub;
    }

    public static Paystub createMockPaystub() {
        // set employee info
        Entity employee = new Entity("John Cena","2400 waterview, TX 75080");
        Entity employer = new Entity("Capital One","Plano TX");
        SSN ssn = new SSN("XXX-XX-1234");

        // set income
        Income income = new Income("19.5","40","800.00","6,600.00", "4,134.90", "600.70"); // changes rate from 20 to 19.5. YTD Net pay from 590 to 600. netpay from 5.6 to 6.6

        // set dates
        String reportStartDate = "02/07/2018";
        String reportEndDate = "02/13/2018";
        String payDate = "02/14/2018";
        Dates dates = new Dates(reportStartDate, reportEndDate, payDate);

        // set deductions
        FinancialValue ficaMedicare = new FinancialValue("11.60", "81.20"); // currently 13.60 instead of 11.60 to make fraud total, YTD changed form 81.20 to 83.20
        FinancialValue ficaSocialSecurity  = new FinancialValue("49.60", "347.20");
        FinancialValue fedTax = new FinancialValue("108.10", "756.70");
        FinancialValue stateTax =  new FinancialValue("40.00", "280.00");
        FinancialValue overall =  new FinancialValue("209.30", "1,465.10");
        Deductions deductions = new Deductions(ficaMedicare, ficaSocialSecurity, fedTax, stateTax, overall);

        Paystub paystub = new Paystub(employee, employer,ssn, income, deductions, dates);

        return paystub;
    }
}
