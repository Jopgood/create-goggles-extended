package com.createge.api;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.ItemHelper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for handling recipe outputs in Create mod machines.
 */
public final class RecipeOutputHelper {
    private RecipeOutputHelper() {} // Prevent instantiation
    
    /**
     * Gets all fluid outputs for a basin's current recipe.
     * @param basin The basin block entity
     * @return A list of fluid outputs
     */
    public static List<FluidStack> getFluidOutputs(BasinBlockEntity basin) {
        if (basin == null || basin.getWorld() == null || !(basin.getWorld() instanceof ServerWorld)) {
            return Collections.emptyList();
        }
        
        ProcessingRecipe<?> recipe = findCurrentRecipe(basin);
        return recipe != null ? recipe.getFluidResults() : Collections.emptyList();
    }
    
    /**
     * Gets all item outputs for a basin's current recipe.
     * @param basin The basin block entity
     * @return A map of item outputs and their counts
     */
    public static Map<Item, Integer> getItemOutputs(BasinBlockEntity basin) {
        if (basin == null || basin.getWorld() == null || !(basin.getWorld() instanceof ServerWorld)) {
            return Collections.emptyMap();
        }
        
        ProcessingRecipe<?> recipe = findCurrentRecipe(basin);
        if (recipe == null) {
            return Collections.emptyMap();
        }
        
        DynamicRegistryManager registryManager = ((ServerWorld) basin.getWorld()).getRegistryManager();
        Map<Item, Integer> outputMap = new HashMap<>();
        
        for (ProcessingOutput output : recipe.getRollableResults()) {
            ItemStack stack = output.getStack();
            ItemStack actualOutput = stack.copy();
            Item item = actualOutput.getItem();
            int count = actualOutput.getCount();
            
            outputMap.put(item, outputMap.getOrDefault(item, 0) + count);
        }
        
        return outputMap;
    }
    
    /**
     * Finds the current recipe for a basin.
     * @param basin The basin block entity
     * @return The current processing recipe, or null if none found
     */
    private static ProcessingRecipe<?> findCurrentRecipe(BasinBlockEntity basin) {
        World world = basin.getWorld();
        if (!(world instanceof ServerWorld)) {
            return null;
        }
        
        RecipeManager recipeManager = world.getRecipeManager();
        Optional<? extends Recipe<?>> optionalRecipe = 
            recipeManager.getFirstMatch(AllRecipeTypes.MIXING.getType(), basin.inputInventory, (ServerWorld) world);
        
        if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof ProcessingRecipe<?>) {
            return (ProcessingRecipe<?>) optionalRecipe.get();
        }
        
        return null;
    }
    
    /**
     * Estimates the maximum number of operations possible with the current ingredients.
     * @param basin The basin block entity
     * @return The maximum number of operations possible
     */
    public static int getMaxOperationsPossible(BasinBlockEntity basin) {
        if (basin == null || basin.getWorld() == null) {
            return 0;
        }
        
        ProcessingRecipe<?> recipe = findCurrentRecipe(basin);
        if (recipe == null) {
            return 0;
        }
        
        // This is a simplified implementation
        // A full implementation would need to check fluid and item ingredients
        return 1;
    }
}