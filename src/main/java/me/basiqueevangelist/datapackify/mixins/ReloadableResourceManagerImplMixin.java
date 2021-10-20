package me.basiqueevangelist.datapackify.mixins;

import me.basiqueevangelist.datapackify.resources.BuiltInResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
    @Shadow public abstract void addPack(ResourcePack pack);

    @Shadow @Final private ResourceType type;

    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void addCustomResourcePack(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        if (type == ResourceType.SERVER_DATA)
            addPack(new BuiltInResourcePack());
    }
}
