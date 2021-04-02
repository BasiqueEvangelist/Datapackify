package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public interface SellEnchantedToolFactoryAccessor {
    @Accessor("tool")
    void datapackify$setTool(ItemStack is);
}
