package com.example.learn;

import java.util.*;

public class Reverse {

    public static void main (String[] args) {
        String name = "Mushfiq";
        System.out.println(reverseName(name));
    }

    public static String reverseName(String name) {
        String reversedName = "";
        for (int i = 0; i < name.length(); i++) {
            reversedName = name.charAt(i) + reversedName;
        }
        return reversedName;
    }
}
