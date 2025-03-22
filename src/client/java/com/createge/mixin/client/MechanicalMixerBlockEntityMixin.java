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
        System.out.println("[CreateGE] Current recipe: " + (this.currentRecipe != null ? "Found" : "Not found"));
        return this.currentRecipe;
    }

    @Override
    public float getSpeedMultiplier() {
        float speed = Math.abs(((MechanicalMixerBlockEntity)(Object)this).getSpeed());
        System.out.println("[CreateGE] Mixer speed: " + speed);
        return speed;
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
            System.out.println("[CreateGE] Stored recipe from getMatchingRecipes");
        }
    }

    @Override
    public float getRecipeSpeed() {
        ProcessingRecipe<?> recipe = getCurrentRecipe();
        if (recipe != null) {
            int duration = recipe.getProcessingDuration();
            float result = duration != 0 ? duration / 100f : 1f;
            System.out.println("[CreateGE] Recipe duration: " + duration + ", Speed: " + result);
            return result;
        }
        System.out.println("[CreateGE] No recipe found for speed calculation");
        return 1f;
    }
}