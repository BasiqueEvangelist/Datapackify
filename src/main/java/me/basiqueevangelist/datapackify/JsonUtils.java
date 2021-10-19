package me.basiqueevangelist.datapackify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public final class JsonUtils {
    private JsonUtils() {

    }

    public static JsonElement get(JsonObject obj, String name) {
        if (!obj.has(name))
            throw new IllegalArgumentException("Missing " + name + "in JSON object!");

        return obj.get(name);
    }

    public static <T> T getRegistryItem(Registry<T> reg, String id) {
        return reg.getOrEmpty(new Identifier(id)).orElseThrow(() ->
            new IllegalArgumentException("'" + id + "' is not a member of registry " + reg.getKey().getValue())
        );
    }

    public static ItemStack makeItemStack(JsonElement el) {
        if (el.isJsonPrimitive()) {
            return new ItemStack(JsonHelper.asItem(el, "<item stack>"));
        } else {
            return ShapedRecipe.outputFromJson(JsonHelper.asObject(el, "<item stack>"));
        }
    }
}