package com.hcdc.attendance;

import java.time.LocalDate;

public class QuoteOfTheDay {
    private static final String[] QUOTES = new String[] {
        "The only way to do great work is to love what you do. — Steve Jobs",
        "Success is not final, failure is not fatal: It is the courage to continue that counts. — Winston Churchill",
        "Don't watch the clock; do what it does. Keep going. — Sam Levenson",
        "Quality is not an act, it is a habit. — Aristotle",
        "The future depends on what you do today. — Mahatma Gandhi",
        "Believe you can and you're halfway there. — Theodore Roosevelt",
        "Act as if what you do makes a difference. It does. — William James",
        "Learning never exhausts the mind. — Leonardo da Vinci",
        "Perseverance is not a long race; it is many short races one after another. — Walter Elliot",
        "Small steps in the right direction can turn out to be the biggest step of your life. — Unknown"
    };

    public static String getQuote() {
        int day = LocalDate.now().getDayOfYear();
        int idx = Math.abs(day) % QUOTES.length;
        return QUOTES[idx];
    }
}
