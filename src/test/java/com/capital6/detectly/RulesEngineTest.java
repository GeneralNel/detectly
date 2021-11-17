package com.capital6.detectly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class RulesEngineTest {

    @Autowired
    private KieContainer kieContainer;

    KieSession kSession;

    @BeforeEach
    public void setup() {
        kSession = kieContainer.newKieSession();
    }

    @Test
    public void
    testValidSSN() {
        Paystub paystub = new Paystub("Tom", "123-45-6789");
        kSession.insert(paystub);

        kSession.fireAllRules();

        assertTrue(paystub.getSsn().isValid());
    }

    @Test
    public void
    testInvalidSSN() {
        // incorrect ssn length
        Paystub paystub = new Paystub("Tom", "123-45-67891");
        kSession.insert(paystub);

        kSession.fireAllRules();

        assertFalse(paystub.getSsn().isValid());
        assertEquals(paystub.getSsn().getMsg(), "Incorrect number of characters in SSN");

        paystub.getSsn().setContent("XXX-XX-XXXX");
        kSession.insert(paystub);

        kSession.fireAllRules();

        assertFalse(paystub.getSsn().isValid());
        assertEquals(paystub.getSsn().getMsg(), "Characters in SSN are hidden");
    }

}
