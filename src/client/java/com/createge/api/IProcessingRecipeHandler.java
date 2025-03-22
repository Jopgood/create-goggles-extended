package com.createge.api;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.recipe.RecipeType;

public interface IProcessingRecipeHandler {
    ProcessingRecipe<?> getCurrentRecipe();
    float getSpeedMultiplier();
    float getRecipeSpeed();
    RecipeType<?> getRecipeType();

    /**
     * Returns the total number of item and fluid outputs for the current recipe.
     */
    default int getProcessingOutputCount() {
        ProcessingRecipe<?> recipe = getCurrentRecipe();
        if (recipe == null) return 0;

        int itemOutputs = recipe.getRollableResults().size();
        int fluidOutputs = recipe.getFluidResults().size();

        return itemOutputs + fluidOutputs;
    }
}
