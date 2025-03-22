package com.createge.api;

import net.minecraft.util.math.MathHelper;

/**
 * Utility class to calculate processing times for Create mod machines.
 */
public final class ProcessingTimeCalculator {
    private ProcessingTimeCalculator() {} // Prevent instantiation

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
     * Calculates processing time for Mechanical Press
     * @param rpm Current RPM of the machine
     * @return Processing time in ticks
     */
    public static double calculateMechanicalPressTime(double rpm) {
        if (rpm == 0) return Double.POSITIVE_INFINITY;

        double absK = Math.abs(rpm);
        double fraction = absK / 512.0; // Use 512.0 for double division
        double minValue = Math.min(1, fraction);
        double maxValue = Math.max(0, minValue);
        double innerTerm = 1 + maxValue * 59;
        double absInnerTerm = Math.abs(innerTerm);
        return 240.0 / absInnerTerm; // Use 240.0 for double division
    }

    /**
     * Calculates recipe frequency (recipes/tick) for Mixer
     * @param rpm Current RPM of the machine
     * @param recipeSpeed The speed of the recipe
     * @return Recipes per tick
     */
    public static double calculateMixerFrequency(double rpm, double recipeSpeed) {
        double logValue = Math.log(512 / rpm) / Math.log(2); // log base 2 without int casting
        double processingTicks = MathHelper.clamp(
                Math.ceil(logValue) * Math.ceil(recipeSpeed * 15) + 1, 1.0, 512.0); // Ensure floating-point operations
        return 1.0 / processingTicks;
    }
}