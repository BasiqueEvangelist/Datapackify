package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellSuspiciousStewFactory.class)
public interface SellSuspiciousStewFactoryAccessor {
    @Accessor("multiplier")
    @Mutable
    void datapackify$setMultiplier(float mul);
}
