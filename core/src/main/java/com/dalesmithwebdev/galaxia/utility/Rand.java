package com.dalesmithwebdev.galaxia.utility;

import java.util.Random;

public class Rand {
    private static Rand instance = null;
    private Random random = null;

    private Rand() {
        this.random = new Random();
    }

    public static Rand getInstance() {
        if(Rand.instance == null) {
            Rand.instance = new Rand();
        }
        return Rand.instance;
    }

    public static int nextInt() {
        return Rand.getInstance().random.nextInt();
    }

    public static float nextFloat() {
        return Rand.getInstance().random.nextFloat();
    }

    public static boolean nextBoolean() {
        return Rand.getInstance().random.nextBoolean();
    }

    public static double nextDouble() {
        return Rand.getInstance().random.nextDouble();
    }

    public static int nextInt(int bound) {
        return Rand.getInstance().random.nextInt(bound);
    }

    public static int nextInt(int min, int max) {
        return (int) ((Rand.getInstance().random.nextDouble() * (max - min)) + min);
    }
}
