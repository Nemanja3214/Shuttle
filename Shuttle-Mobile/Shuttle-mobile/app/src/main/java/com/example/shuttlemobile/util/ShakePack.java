package com.example.shuttlemobile.util;

/**
 * Data structure containing all info for a shake event.
 * Usage:
 * Create instance once, the recommended threshold is 11-12.
 * In the onSensorChanged method of your listener, for TYPE_ACCELEROMETER, call
 * update(sensor.values). After that, call isShaking() to check whether there's shaking.
 */
public class ShakePack {
    private float lastAcc = 9f;
    private float currAcc = 9f;
    private float acc = 10f;
    private float threshold;

    public ShakePack(float threshold) {
        this.threshold = threshold;
    }

    public void update(float[] acceleration) {
        final float x = acceleration[0];
        final float y = acceleration[1];
        final float z = acceleration[2];
        lastAcc = currAcc;
        currAcc = (float) Math.sqrt(x * x + y * y + z * z);
        final float delta = currAcc - lastAcc;
        acc = acc * 0.9f + delta;
    }

    public float getAcc() {
        return Math.abs(acc);
    }

    public boolean isShaking() {
        return Math.abs(acc) > threshold;
    }
}
