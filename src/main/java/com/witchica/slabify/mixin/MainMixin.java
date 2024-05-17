package com.witchica.slabify.mixin;

import com.witchica.slabify.Slabify;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 0)
public class MainMixin {

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Group(name = "serverFreezeHooks", min = 1, max = 1)
    @Inject(method = "main", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startServer(Ljava/io/File;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private static void main(CallbackInfo ci) {
        Slabify.INSTANCE.onPostInitialize();
    }
}
