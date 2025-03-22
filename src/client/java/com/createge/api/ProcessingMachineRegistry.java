package com.createge.api;

import net.minecraft.block.entity.BlockEntity;
import java.util.HashMap;
import java.util.Map;

public class ProcessingMachineRegistry {
    private static final Map<Class<? extends BlockEntity>, ProcessingMachineConfig> CONFIGS = new HashMap<>();

    public static void register(Class<? extends BlockEntity> machineClass, ProcessingMachineConfig config) {
        CONFIGS.put(machineClass, config);
    }

    public static ProcessingMachineConfig getConfig(Class<? extends BlockEntity> machineClass) {
        return CONFIGS.get(machineClass);
    }

    public record ProcessingMachineConfig(
            float baseRPM,
            double baseRecipesPerMinute,
            int processorOffset
    ) {}
}