package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.ProcessItemFactory.class)
public interface ProcessItemFactoryAccessor {
    @Accessor("multiplier")
    @Mutable
    void datapackify$setMultiplier(float mul);

    @Accessor("sell")
    @Mutable
    void datapackify$setSell(ItemStack is);

    @Accessor("secondBuy")
    @Mutable
    void datapackify$setSecondBuy(ItemStack is);
}
