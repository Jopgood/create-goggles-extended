package com.createge.mixin.client;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasinOperatingBlockEntity.class)
public interface BasinOperatingBlockEntityAccessor {
    @Accessor(remap = false)
    Recipe<?> getCurrentRecipe();

}