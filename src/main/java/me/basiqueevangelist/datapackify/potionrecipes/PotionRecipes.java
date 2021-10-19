package me.basiqueevangelist.datapackify.potionrecipes;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public final class PotionRecipes {
    private PotionRecipes() {

    }

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrewingRecipeManager());
    }
}