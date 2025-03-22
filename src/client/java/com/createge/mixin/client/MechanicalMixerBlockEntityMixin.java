package com.createge.mixin.client;

import com.createge.api.IProcessingRecipeHandler;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MechanicalMixerBlockEntity.class)
public class MechanicalMixerBlockEntityMixin implements IProcessingRecipeHandler {
    @Unique
    private ProcessingRecipe<?> currentRecipe;

    @Override
    public ProcessingRecipe<?> getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public float getSpeedMultiplier() {
        return Math.abs(((MechanicalMixerBlockEntity)(Object)this).getSpeed());
    }

    @Override
    public RecipeType<?> getRecipeType() {
        return AllRecipeTypes.MIXING.getType();
    }

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), remap = false)
    private void storeCurrentRecipe(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        List<Recipe<?>> recipes = cir.getReturnValue();
        if (!recipes.isEmpty() && recipes.get(0) instanceof ProcessingRecipe<?> recipe
                && recipe.getType() == getRecipeType()) {
            this.currentRecipe = recipe;
        }
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
