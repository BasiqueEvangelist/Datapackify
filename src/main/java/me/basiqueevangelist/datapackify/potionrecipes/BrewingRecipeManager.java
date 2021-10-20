package me.basiqueevangelist.datapackify.potionrecipes;

import com.google.gson.*;
import me.basiqueevangelist.datapackify.Datapackify;
import me.basiqueevangelist.datapackify.JsonUtils;
import me.basiqueevangelist.datapackify.mixins.BrewingRecipeRegistryAccessors;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class BrewingRecipeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger("Datapackify/BrewingRecipeManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static List<BrewingRecipeRegistry.Recipe<Item>> PRESET_ITEM_RECIPES = null;
    private static List<BrewingRecipeRegistry.Recipe<Potion>> PRESET_POTION_RECIPES = null;
    private static List<Ingredient> PRESET_POTION_TYPES = null;

    public BrewingRecipeManager() {
        super(GSON, "brewing_recipes");
    }

    public void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        if (PRESET_ITEM_RECIPES == null) {
            PRESET_ITEM_RECIPES = BrewingRecipeRegistryAccessors.getItemRecipes();
        }
        if (PRESET_POTION_RECIPES == null) {
            PRESET_POTION_RECIPES = BrewingRecipeRegistryAccessors.getPotionRecipes();
        }
        if (PRESET_POTION_TYPES == null) {
            PRESET_POTION_TYPES = BrewingRecipeRegistryAccessors.getPotionTypes();
        }

        List<BrewingRecipeRegistry.Recipe<Item>> newItemRecipes = new ArrayList<>();
        List<BrewingRecipeRegistry.Recipe<Potion>> newPotionRecipes = new ArrayList<>();

        for (var entry : loader.entrySet()) {
            try {
                JsonObject obj = JsonHelper.asObject(entry.getValue(), "<brewing recipe>");
                String type = JsonHelper.getString(obj, "type");
                switch (type) {
                    case "item" -> newItemRecipes.add(
                        new BrewingRecipeRegistry.Recipe<>(
                            JsonHelper.getItem(obj, "input"),
                            Ingredient.fromJson(JsonUtils.get(obj, "ingredient")),
                            JsonHelper.getItem(obj, "output")
                        )
                    );
                    case "potion" -> newPotionRecipes.add(
                        new BrewingRecipeRegistry.Recipe<>(
                            JsonUtils.getRegistryItem(Registry.POTION, JsonHelper.getString(obj, "input")),
                            Ingredient.fromJson(JsonUtils.get(obj, "ingredient")),
                            JsonUtils.getRegistryItem(Registry.POTION, JsonHelper.getString(obj, "output"))
                        )
                    );
                    default -> throw new IllegalArgumentException("Invalid brewing recipe type $type");
                }
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Encountered error while parsing {}: {}", entry.getKey(), e);
            }
        }
        LOGGER.info("Loaded " + newItemRecipes.size() + " item recipes and " + newPotionRecipes.size() + " potion recipes");
        newItemRecipes.addAll(PRESET_ITEM_RECIPES);
        newPotionRecipes.addAll(PRESET_POTION_RECIPES);

        List<Ingredient> potionTypes =
            new ArrayList<>(Collections.singletonList(Ingredient.fromTag(TagFactory.ITEM.create(new Identifier("minecraft:potions")))));
        potionTypes.addAll(PRESET_POTION_TYPES);

        BrewingRecipeRegistryAccessors.setItemRecipes(newItemRecipes);
        BrewingRecipeRegistryAccessors.setPotionRecipes(newPotionRecipes);
        BrewingRecipeRegistryAccessors.setPotionTypes(potionTypes);
    }

    public Identifier getFabricId() {
        return new Identifier(Datapackify.NAMESPACE, "brewing_recipes");
    }
}
