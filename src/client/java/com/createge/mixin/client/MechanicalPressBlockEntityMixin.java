package com.createge.mixin.client;

import com.createge.api.IProcessingRecipeHandler;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import org.spongepowered.asm.mixin.Mixin;


@Mixin(MechanicalPressBlockEntity.class)
public class MechanicalPressBlockEntityMixin implements IProcessingRecipeHandler {

    @Override
    public ProcessingRecipe<?> getCurrentRecipe() {
        MechanicalPressBlockEntity press = (MechanicalPressBlockEntity)(Object)this;
        Recipe<?> recipe = ((BasinOperatingBlockEntityAccessor) press).getCurrentRecipe();
        return recipe instanceof ProcessingRecipe ? (ProcessingRecipe<?>) recipe : null;
    }

    @Override
    public float getSpeedMultiplier() {
        return Math.abs(((MechanicalPressBlockEntity)(Object)this).getSpeed());
    }

    @Override
    public RecipeType<?> getRecipeType() {
        return AllRecipeTypes.PRESSING.getType();
    }

    @Override
    public float getRecipeSpeed() {
        ProcessingRecipe<?> recipe = getCurrentRecipe();
        if (recipe != null) {
            int duration = recipe.getProcessingDuration();
            return duration != 0 ? duration / 100f : 1f;
        }
        return 1f;
    }

}