package me.basiqueevangelist.datapackify.mixins;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "craftRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;increment(I)V"))
    private static void craftRecipe(Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        slots.get(2).increment(recipe.getOutput().getCount() - 1);
    }

    @Inject(method = "canAcceptRecipeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEqualIgnoreDamage(Lnet/minecraft/item/ItemStack;)Z"), cancellable = true)
    private static void canAcceptRecipeOutput(Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (slots.get(2).getCount() + recipe.getOutput().getCount() > slots.get(2).getMaxCount())
            cir.setReturnValue(false);
    }
}