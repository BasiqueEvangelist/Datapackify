package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
public interface SellPotionHoldingItemFactoryAccessor {
    @Accessor("sell")
    void datapackify$setSell(ItemStack is);
}
