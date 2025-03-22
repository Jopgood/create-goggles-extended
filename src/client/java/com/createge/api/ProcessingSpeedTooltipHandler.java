package com.createge.api;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import java.util.List;

public final class ProcessingSpeedTooltipHandler {
    private ProcessingSpeedTooltipHandler() {} // Prevent instantiation
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;

    /**
     * Adds processing speed information to the tooltip.
     * @param tooltip The tooltip list to append to
     * @param rpm Current rotation speed
     * @param processor The processing block entity
     */
    public static void addProcessingSpeedTooltip(List<Text> tooltip,
                                                 double rpm,
                                                 double recipeSpeed,
                                                 double outputCount,
                                                 BlockEntity processor,
                                                 String recipe) {
        if (tooltip == null || processor == null) {
            return;
        }

        double recipesPerMinute;

        if (processor instanceof MechanicalPressBlockEntity) {
            double processingTicks = ProcessingTimeCalculator.calculateMechanicalPressTime(rpm);
            recipesPerMinute = ProcessingTimeCalculator.ticksToRecipesPerMinute(processingTicks);
            System.out.println("[CreateGE] Press calculation: " + processingTicks + " ticks, " + recipesPerMinute + " per minute");
        } else if (processor instanceof MechanicalMixerBlockEntity) {
            double recipesPerTick = ProcessingTimeCalculator.calculateMixerFrequency(rpm, recipeSpeed);
            recipesPerMinute = Math.max(1, outputCount) * (recipesPerTick * (TICKS_PER_SECOND * SECONDS_PER_MINUTE));
            System.out.println("[CreateGE] Mixer calculation: " + recipesPerTick + " per tick, " + recipesPerMinute + " per minute");
        } else {
            System.out.println("[CreateGE] Unknown processor type");
            recipesPerMinute = 0;
        }

        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Recipe: ")
                .append(Text.literal(recipe))
                .formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Processing Speed: ")
                .append(Text.literal(String.format("%.2f", recipesPerMinute))
                        .formatted(Formatting.AQUA))
                .append(Text.literal(" recipes/min")));
    }

    public static float getRecipeMultiplier(ProcessingRecipe<?> recipe) {
        if (recipe == null) return 1f;
        try {
            int processingDuration = recipe.getProcessingDuration();
            return processingDuration != 0 ? 100f / processingDuration : 1f;
        } catch (Exception e) {
            // Log error or handle appropriately
            System.out.println("[CreateGE] Error calculating recipe multiplier: " + e.getMessage());
            return 1f;
        }
    }

    public static BlockEntity findProcessingBlockEntity(BlockEntity source, int yOffset) {
        if (source == null || source.getWorld() == null) return null;
        BlockEntity entity = source.getWorld().getBlockEntity(source.getPos().up(yOffset));
        System.out.println("[CreateGE] Finding processing entity: " + (entity != null ? entity.getClass().getSimpleName() : "null"));
        return entity;
    }
}