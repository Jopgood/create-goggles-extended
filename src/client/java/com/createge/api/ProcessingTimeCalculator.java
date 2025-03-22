package com.createge.api;

/**
 * Utility class to calculate processing times for Create mod machines.
 */
public final class ProcessingTimeCalculator {
    private ProcessingTimeCalculator() {} // Prevent instantiation

    private static final double RPM_SLOW_THRESHOLD = 16.0;
    private static final double RPM_NORMAL_THRESHOLD = 32.0;
    private static final double RPM_FAST_THRESHOLD = 64.0;
    private static final double RPM_SUPER_THRESHOLD = 128.0;
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Converts processing time in ticks to recipes per minute.
     */
    public static double ticksToRecipesPerMinute(double ticks) {
        if (ticks <= 0) return 0;
        return (TICKS_PER_SECOND * SECONDS_PER_MINUTE) / ticks;
    }

    /**
     * Calculates the time in ticks for a mechanical press to complete one cycle.
     */
    public static double calculateMechanicalPressTime(double rpm) {
        // Pressing time calculations based on Create mod behavior
        if (rpm <= 0) return Double.MAX_VALUE;

        // Create mod uses a non-linear curve for press speed
        if (rpm < RPM_SLOW_THRESHOLD) {
            return 60; // Very slow
        } else if (rpm < RPM_NORMAL_THRESHOLD) {
            return 40; // Normal
        } else if (rpm < RPM_FAST_THRESHOLD) {
            return 30; // Fast
        } else if (rpm < RPM_SUPER_THRESHOLD) {
            return 20; // Very fast
        } else {
            return 15; // Super fast
        }
    }

    /**
     * Calculates the mixer processing frequency (recipes processed per tick).
     */
    public static double calculateMixerFrequency(double rpm, double recipeSpeed) {
        if (rpm <= 0 || recipeSpeed <= 0) return 0;
        
        // Simplified model based on Create's processing behavior
        double speedFactor = Math.min(rpm / 32.0, 2.0); // Cap at 2x for high RPM
        return speedFactor / (recipeSpeed * 20); // 20 ticks baseline
    }
}
