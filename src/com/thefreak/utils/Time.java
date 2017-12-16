package com.thefreak.utils;

public class Time {
    public static final long SECOND = 1000000000L; //nanosecond in one second

    public static long get() {
        return System.nanoTime();
    }
}
