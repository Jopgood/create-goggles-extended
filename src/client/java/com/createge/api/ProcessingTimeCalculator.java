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
     * This matches how Create's mixer actually processes recipes.
     */
    public static double calculateMixerFrequency(double rpm, double recipeSpeed) {
        if (rpm <= 0) return 0;
        if (recipeSpeed <= 0) recipeSpeed = 1;
        
        // Based on Create's actual processing formula
        double baseTicksPerRecipe;
        if (rpm < RPM_SLOW_THRESHOLD) {
            baseTicksPerRecipe = 50; // Slower
        } else if (rpm < RPM_NORMAL_THRESHOLD) {
            baseTicksPerRecipe = 30; // Normal
        } else if (rpm < RPM_FAST_THRESHOLD) {
            baseTicksPerRecipe = 20; // Fast
        } else if (rpm < RPM_SUPER_THRESHOLD) {
            baseTicksPerRecipe = 15; // Very fast
        } else {
            baseTicksPerRecipe = 10; // Super fast
        }
        
        // Adjust for recipe-specific speed
        double actualTicksPerRecipe = baseTicksPerRecipe * recipeSpeed;
        
        // Convert to recipes per tick
        double recipesPerTick = 1.0 / actualTicksPerRecipe;
        
        System.out.println("[CreateGE] Mixer calculation:");
        System.out.println("[CreateGE] RPM: " + rpm);
        System.out.println("[CreateGE] Recipe Speed: " + recipeSpeed);
        System.out.println("[CreateGE] Base Ticks: " + baseTicksPerRecipe);
        System.out.println("[CreateGE] Actual Ticks: " + actualTicksPerRecipe);
        System.out.println("[CreateGE] Recipes/Tick: " + recipesPerTick);
        
        return recipesPerTick;
    }
}