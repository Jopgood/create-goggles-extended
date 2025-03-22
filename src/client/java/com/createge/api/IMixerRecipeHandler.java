package com.createge.api;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

public interface IMixerRecipeHandler {
    ProcessingRecipe<?> getCurrentRecipe();
}