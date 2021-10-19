package me.basiqueevangelist.datapackify.trades;

import com.google.gson.JsonObject;
import net.minecraft.village.TradeOffers;

@FunctionalInterface
public interface IOfferFactoryType<T extends TradeOffers.Factory> {
    T deserialize(JsonObject factory);
}