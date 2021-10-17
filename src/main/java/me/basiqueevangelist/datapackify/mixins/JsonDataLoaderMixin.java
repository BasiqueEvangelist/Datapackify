package me.basiqueevangelist.datapackify.mixins;

import com.google.gson.JsonElement;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Mixin(JsonDataLoader.class)
public class JsonDataLoaderMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Datapackify/JsonDataLoaderMixin");

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/Map;", at = @At("RETURN"))
    private void removeEmptyObjects(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<Map<Identifier, JsonElement>> cir) {
        cir.getReturnValue().entrySet().removeIf(x -> {
            if (x.getValue().isJsonObject() && x.getValue().getAsJsonObject().size() == 0) {
                LOGGER.debug("Dropping {} as it is an empty JSON object.", x.getKey());
                return true;
            }
            return false;
        });
    }

    @Redirect(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"))
    private Iterator<Identifier> filterOutEmptyFiles(Collection<Identifier> instance, ResourceManager resourceManager, Profiler profiler) {
        return instance.stream().filter(x -> {
            try {
                Resource res = resourceManager.getResource(x);
                if (res.getInputStream().read() == -1) {
                    LOGGER.debug("Dropping {} as it is empty.", x);
                    return false;
                }
                return true;
            } catch (IOException ioe) {
                return true;
            }
        }).iterator();
    }
}
