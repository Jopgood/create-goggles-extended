package com.createge.mixin.client;

import com.createge.api.IProcessingRecipeHandler;
import com.createge.api.ProcessingSpeedTooltipHandler;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.MixingRecipe;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = BasinBlockEntity.class, priority = 1001)
public abstract class BasinBlockEntityMixin implements IHaveGoggleInformation {

	@Inject(method = "addToGoggleTooltip", at = @At("RETURN"), cancellable = true, remap = false)
	private void addProcessingSpeedTooltip(List<Text> tooltip, boolean isPlayerSneaking,
							   CallbackInfoReturnable<Boolean> cir) {
		BasinBlockEntity basin = (BasinBlockEntity) (Object) this;

		// Get the filter item
		ItemStack filterStack = basin.getFilter().getFilter();
		if (filterStack.isEmpty()) {
			System.out.println("[CreateGE] No filter set on Basin.");
			return;  // No filter = no tooltip modification
		}

		// Get the world and check if it's a server world
		World world = basin.getWorld();
		if (world instanceof ServerWorld serverWorld) {
			// Access RecipeManager and DynamicRegistryManager on the server side
			RecipeManager recipeManager = serverWorld.getRecipeManager();
			DynamicRegistryManager registryManager = serverWorld.getRegistryManager();

			// Find a matching MixingRecipe
			Optional<? extends Recipe<?>> optionalRecipe = recipeManager.getFirstMatch(AllRecipeTypes.MIXING.getType(), basin.inputInventory, serverWorld);
			if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof MixingRecipe mixingRecipe) {
				// Pass registryManager to getOutput()
				ItemStack outputStack = mixingRecipe.getOutput(registryManager);

				// Log the recipe and output item
				System.out.println("[CreateGE] Found Mixing Recipe:");
				System.out.println("[CreateGE] Filter Item: " + filterStack.getName().getString());
				System.out.println("[CreateGE] Recipe Output: " + outputStack.getName().getString());
			} else {
				System.out.println("[CreateGE] No valid Mixing Recipe found for: " + filterStack.getName().getString());
			}
		} else {
			System.out.println("[CreateGE] Unable to access RecipeManager (not in ServerWorld).");
		}

		// Retrieve processing speed details
		BlockEntity processor = ProcessingSpeedTooltipHandler.findProcessingBlockEntity(basin, 2);
		if (processor instanceof IProcessingRecipeHandler handler) {
			float rpm = handler.getSpeedMultiplier();
			float recipeSpeed = handler.getRecipeSpeed();
			int outputCount = handler.getProcessingOutputCount();

			ProcessingSpeedTooltipHandler.addProcessingSpeedTooltip(
					tooltip,
					rpm,
					recipeSpeed,
					outputCount,
					processor,
					filterStack.getName().getString());
		}

		cir.setReturnValue(true);
	}
}