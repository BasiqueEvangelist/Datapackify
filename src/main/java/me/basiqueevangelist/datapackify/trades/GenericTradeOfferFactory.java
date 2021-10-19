package me.basiqueevangelist.datapackify.trades;

import com.google.gson.JsonObject;
import me.basiqueevangelist.datapackify.JsonUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import java.util.*;

public record GenericTradeOfferFactory(TradeOffer offer) implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
        return offer;
    }

    public static GenericTradeOfferFactory parse(JsonObject obj) {
        ItemStack firstBuy = JsonUtils.makeItemStack(JsonUtils.get(obj, "firstBuy"));
        ItemStack secondBuy = ItemStack.EMPTY;

        if (obj.has("secondBuy")) {
            secondBuy = JsonUtils.makeItemStack(JsonUtils.get(obj, "secondBuy"));
        }

        var sell = JsonUtils.makeItemStack(JsonUtils.get(obj, "sell"));
        int uses = JsonHelper.getInt(obj, "uses", 0);
        int maxUses = JsonHelper.getInt(obj, "uses");
        int xp = JsonHelper.getInt(obj, "xp");
        float priceMultiplier = JsonHelper.getFloat(obj, "price_multiplier");
        int demand = JsonHelper.getInt(obj, "demand", 0);
        return new GenericTradeOfferFactory(new TradeOffer(firstBuy, secondBuy, sell, uses, maxUses, xp, priceMultiplier, demand));
    }
}