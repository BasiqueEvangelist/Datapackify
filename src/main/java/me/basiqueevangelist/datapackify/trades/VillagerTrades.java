package me.basiqueevangelist.datapackify.trades;

import com.google.gson.JsonObject;
import me.basiqueevangelist.datapackify.Datapackify;
import me.basiqueevangelist.datapackify.JsonUtils;
import me.basiqueevangelist.datapackify.mixins.ProcessItemFactoryAccessor;
import me.basiqueevangelist.datapackify.mixins.SellEnchantedToolFactoryAccessor;
import me.basiqueevangelist.datapackify.mixins.SellPotionHoldingItemFactoryAccessor;
import me.basiqueevangelist.datapackify.mixins.SellSuspiciousStewFactoryAccessor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradeOffers.*;
import net.minecraft.village.VillagerType;
import java.util.*;

public final class VillagerTrades {
    private VillagerTrades() {

    }

    public static final Registry<IOfferFactoryType> REGISTRY = FabricRegistryBuilder.createDefaulted(
        IOfferFactoryType.class,
        new Identifier(Datapackify.NAMESPACE, "villager_trades"),
        new Identifier(Datapackify.NAMESPACE, "empty")
    ).buildAndRegister();

    public static void init() {
        register(Datapackify.NAMESPACE + "empty", (obj) -> (e, r) -> null);
        register("minecraft:buy_for_one_emerald", (obj) ->
            new BuyForOneEmeraldFactory(
                JsonHelper.getItem(obj, "buy"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        register("minecraft:sell_item", (obj) ->
            new SellItemFactory(
                JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05f)
            )
        );

        register("minecraft:sell_enchanted_tool", (obj) -> {
            var fac = new SellEnchantedToolFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "base_price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                JsonHelper.getFloat(obj, "multiplier", 0.05f)
            );
            ((SellEnchantedToolFactoryAccessor)fac).datapackify$setTool(JsonUtils.makeItemStack(JsonUtils.get(obj, "tool")));
            return fac;
        });
        register("minecraft:sell_map", (obj) ->
            new SellMapFactory(
                JsonHelper.getInt(obj, "price"),
                JsonUtils.getRegistryItem(Registry.STRUCTURE_FEATURE, JsonHelper.getString(obj, "structure")),
                getMapIconType(JsonHelper.getString(obj, "icon_type")),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        register("minecraft:sell_suspicious_stew", (obj) -> {
            var fac = new SellSuspiciousStewFactory(
                JsonUtils.getRegistryItem(Registry.STATUS_EFFECT, JsonHelper.getString(obj, "effect")),
                JsonHelper.getInt(obj, "duration"),
                JsonHelper.getInt(obj, "experience")
            );
            ((SellSuspiciousStewFactoryAccessor)fac).datapackify$setMultiplier(JsonHelper.getFloat(obj, "multiplier", 0.05f));
            return fac;
        });
        register("minecraft:process_item", (obj) -> {
            var fac = new ProcessItemFactory(
                Items.STONE,
                JsonHelper.getInt(obj, "second_count"),
                JsonHelper.getInt(obj, "price"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            );
            ((ProcessItemFactoryAccessor)fac).datapackify$setMultiplier(JsonHelper.getFloat(obj, "multiplier", 0.05f));
            ((ProcessItemFactoryAccessor)fac).datapackify$setSecondBuy(JsonUtils.makeItemStack(JsonUtils.get(obj, "second_buy")));
            ((ProcessItemFactoryAccessor)fac).datapackify$setSell( JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")));
            return fac;
        });
        register("minecraft:type_aware_buy_for_one_emerald", (obj) ->
            new TypeAwareBuyForOneEmeraldFactory(
                JsonHelper.getInt(obj, "count"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience"),
                typeAwareItemMap(JsonHelper.getObject(obj, "map"))
            )
        );
        register("minecraft:sell_potion_holding_item", (obj) -> {
            var fac = new SellPotionHoldingItemFactory(
                JsonHelper.getItem(obj, "second_buy"),
                JsonHelper.getInt(obj, "second_count"),
                Items.STONE,
                JsonHelper.getInt(obj, "sell_count"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            );
            ((SellPotionHoldingItemFactoryAccessor)fac).datapackify$setSell(JsonUtils.makeItemStack(JsonUtils.get(obj, "sell")));
            return fac;
        });
        register("minecraft:sell_dyed_armor", (obj) ->
            new SellDyedArmorFactory(
                JsonHelper.getItem(obj, "sell"),
                JsonHelper.getInt(obj, "price"),
                JsonHelper.getInt(obj, "max_uses"),
                JsonHelper.getInt(obj, "experience")
            )
        );
        register("minecraft:enchant_book", (obj) ->
            new EnchantBookFactory(
                JsonHelper.getInt(obj, "experience")
            )
        );
        register("datapackify:generic", GenericTradeOfferFactory::parse);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new VillagerTradeManager());
    }

    private static <T extends TradeOffers.Factory> IOfferFactoryType<T> register(String name, IOfferFactoryType<T> fac) {
        return Registry.register(REGISTRY, new Identifier(name), fac);
    }

    private static MapIcon.Type getMapIconType(String s) {
        return switch (s) {
            case "player" -> MapIcon.Type.PLAYER;
            case "frame" -> MapIcon.Type.FRAME;
            case "red_marker" -> MapIcon.Type.RED_MARKER;
            case "blue_marker" -> MapIcon.Type.BLUE_MARKER;
            case "target_x" -> MapIcon.Type.TARGET_X;
            case "target_point" -> MapIcon.Type.TARGET_POINT;
            case "player_off_map" -> MapIcon.Type.PLAYER_OFF_MAP;
            case "player_off_limits" -> MapIcon.Type.PLAYER_OFF_LIMITS;
            case "mansion" -> MapIcon.Type.MANSION;
            case "monument" -> MapIcon.Type.MONUMENT;
            case "banner_white" -> MapIcon.Type.BANNER_WHITE;
            case "banner_orange" -> MapIcon.Type.BANNER_ORANGE;
            case "banner_magenta" -> MapIcon.Type.BANNER_MAGENTA;
            case "banner_light_blue" -> MapIcon.Type.BANNER_LIGHT_BLUE;
            case "banner_yellow" -> MapIcon.Type.BANNER_YELLOW;
            case "banner_lime" -> MapIcon.Type.BANNER_LIME;
            case "banner_pink" -> MapIcon.Type.BANNER_PINK;
            case "banner_gray" -> MapIcon.Type.BANNER_GRAY;
            case "banner_light_gray" -> MapIcon.Type.BANNER_LIGHT_GRAY;
            case "banner_cyan" -> MapIcon.Type.BANNER_CYAN;
            case "banner_purple" -> MapIcon.Type.BANNER_PURPLE;
            case "banner_blue" -> MapIcon.Type.BANNER_BLUE;
            case "banner_brown" -> MapIcon.Type.BANNER_BROWN;
            case "banner_green" -> MapIcon.Type.BANNER_GREEN;
            case "banner_red" -> MapIcon.Type.BANNER_RED;
            case "banner_black" -> MapIcon.Type.BANNER_BLACK;
            case "red_x" -> MapIcon.Type.RED_X;
            default -> throw new IllegalArgumentException("Invalid map icon type $s");
        };
    }

    private static Map<VillagerType, Item> typeAwareItemMap(JsonObject obj) {
        Map<VillagerType, Item> map = new HashMap<>();
        for (var entry : obj.entrySet()) {
            var res = JsonUtils.getRegistryItem(Registry.VILLAGER_TYPE, entry.getKey());
            map.put(res, JsonHelper.asItem(entry.getValue(), "item"));
        }
        return map;
    }
}
