package me.basiqueevangelist.datapackify.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TradeOffers.class)
public interface TradeOffersAccessor {
    @Accessor("PROFESSION_TO_LEVELED_TRADE")
    @Mutable
    static void setTrades(Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map) {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }

    @Accessor("WANDERING_TRADER_TRADES")
    @Mutable
    static void setWanderingTrades(Int2ObjectMap<TradeOffers.Factory[]> map) {
        throw new UnsupportedOperationException("Mixin failed to apply");
    }
}
