package com.capital6.detectly;

import java.util.ArrayList;
import java.util.List;

public class OverallStats {

    boolean fraudDetected;
    public List<String> fraudMessages;

    public OverallStats(boolean fraudDetected, List<String> fraudMessages) {
        this.fraudDetected = fraudDetected;
        this.fraudMessages = fraudMessages;
    }

    public OverallStats() {
        this.fraudDetected = false;
        fraudMessages = new ArrayList<>();
    }

    public OverallStats(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
        fraudMessages = new ArrayList<>();
    }

    public boolean isFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }
}
