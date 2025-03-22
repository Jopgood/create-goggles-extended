package com.createge.api;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Helper class to find recipe output counts based on filter strings
 */
public class RecipeOutputHelper {

    /**
     * Gets the total output count for a recipe based on the filter in a basin
     *
     * @param basin The basin block entity
     * @param handler The processing recipe handler
     * @return The number of items produced, or 1 if no recipe found
     */
    public static int getRecipeOutputCount(BasinBlockEntity basin, IProcessingRecipeHandler handler) {
        System.out.println("[RecipeOutputHelper] Starting recipe output count calculation...");

        // Get the filter from the basin
        FilteringBehaviour filtering = basin.getFilter();
        if (filtering == null || filtering.getFilter().isEmpty()) {
            System.out.println("[RecipeOutputHelper] No filter found in basin, returning default (1)");
            return 1;
        }

        // Get the filter string and clean it
        String filterString = filtering.getFilter().getName().toString();
        System.out.println("[RecipeOutputHelper] Filter: " + filterString + " (normalized: " + filterString + ")");
        System.out.println("[RecipeOutputHelper] Processing block type: " + handler.getClass().getSimpleName());

        // Try to get the current recipe directly from the handler
        ProcessingRecipe<?> currentRecipe = handler.getCurrentRecipe();
        if (currentRecipe != null) {
            System.out.println("[RecipeOutputHelper] Found current recipe: " + currentRecipe.getId());

            // Check if any of the recipe outputs match our filter
            List<ProcessingOutput> outputs = currentRecipe.getRollableResults();
            System.out.println("[RecipeOutputHelper] Recipe has " + outputs.size() + " potential outputs");

            for (ProcessingOutput output : outputs) {
                ItemStack stack = output.getStack();
                String itemName = stack.getItem().getName().toString();

                System.out.println("[RecipeOutputHelper] Checking output: " + itemName +
                        " (normalized: " + itemName + "), count: " + stack.getCount() +
                        ", chance: " + output.getChance());

                if (matchesFilter(filterString, itemName)) {
                    System.out.println("[RecipeOutputHelper] ✓ MATCH FOUND between filter and output!");
                    int totalItems = calculateTotalItems(outputs);
                    System.out.println("[RecipeOutputHelper] Total output count: " + totalItems);
                    return totalItems;
                } else {
                    System.out.println("[RecipeOutputHelper] ✗ No match between filter and output");
                }
            }

            System.out.println("[RecipeOutputHelper] None of the outputs matched the filter");
        } else {
            System.out.println("[RecipeOutputHelper] No current recipe found in handler");
        }

        // If we couldn't find a match with the current recipe, return 1
        System.out.println("[RecipeOutputHelper] Returning default output count (1)");
        return 1;
    }

    /**
     * Calculate total number of items from all output stacks
     */
    private static int calculateTotalItems(List<ProcessingOutput> outputs) {
        System.out.println("[RecipeOutputHelper] Calculating total items from " + outputs.size() + " outputs");
        int total = 0;

        for (ProcessingOutput output : outputs) {
            ItemStack stack = output.getStack();
            float chance = output.getChance();

            if (!stack.isEmpty()) {
                if (chance >= 0.9f) {
                    total += stack.getCount();
                    System.out.println("[RecipeOutputHelper]   - Including " + stack.getItem() +
                            " x" + stack.getCount() + " (chance: " + chance + ")");
                } else {
                    System.out.println("[RecipeOutputHelper]   - Excluding " + stack.getItem() +
                            " x" + stack.getCount() + " (chance too low: " + chance + ")");
                }
            }
        }

        int result = Math.max(1, total);
        System.out.println("[RecipeOutputHelper] Final total: " + result + " items");
        return result;
    }

    /**
     * Check if a filter string matches a result name
     */
    private static boolean matchesFilter(String filter, String result) {
        // Direct match
        if (filter.equals(result)) {
            System.out.println("[RecipeOutputHelper] Direct match: " + filter + " == " + result);
            return true;
        }

        // Remove any numbers from filter
        String cleanFilter = filter.replaceAll("\\d+", "").trim();

        if (cleanFilter.equals(filter)) {
            System.out.println("[RecipeOutputHelper] No numbers to clean from filter");
        } else {
            System.out.println("[RecipeOutputHelper] Cleaned filter: " + cleanFilter);
        }

        // Check for partial matches both ways
        boolean resultContainsFilter = result.contains(cleanFilter);
        boolean filterContainsResult = cleanFilter.contains(result);

        if (resultContainsFilter) {
            System.out.println("[RecipeOutputHelper] Partial match: result \"" + result +
                    "\" contains filter \"" + cleanFilter + "\"");
        }

        if (filterContainsResult) {
            System.out.println("[RecipeOutputHelper] Partial match: filter \"" + cleanFilter +
                    "\" contains result \"" + result + "\"");
        }

        return resultContainsFilter || filterContainsResult;
    }

    /**
     * Normalize an item name for comparison
     */
    private static String normalizeItemName(String name) {
        System.out.println("[RecipeOutputHelper] Normalizing: " + name);

        // Remove count prefix if present (e.g., "2 minecraft:iron_ingot")
        String withoutCount = name.replaceAll("^\\d+\\s+", "");
        if (!withoutCount.equals(name)) {
            System.out.println("[RecipeOutputHelper]   - Removed count prefix: " + withoutCount);
        }

        // Remove namespace if present
        String withoutNamespace = withoutCount.replaceAll("^[\\w]+:", "");
        if (!withoutNamespace.equals(withoutCount)) {
            System.out.println("[RecipeOutputHelper]   - Removed namespace: " + withoutNamespace);
        }

        // Convert to lowercase and remove extra spaces
        String normalized = withoutNamespace.toLowerCase().trim();
        if (!normalized.equals(withoutNamespace)) {
            System.out.println("[RecipeOutputHelper]   - Lowercase & trimmed: " + normalized);
        }

        return normalized;
    }
}