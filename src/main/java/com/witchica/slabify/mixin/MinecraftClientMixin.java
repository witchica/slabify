package com.witchica.slabify.mixin;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.client.SlabifyClient;
import net.fabricmc.fabric.mixin.resource.loader.MinecraftServerMixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public class MinecraftClientMixin {
	@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
	@Group(name = "clientFreezeHooks", min = 1, max = 1)
	@Inject(method = "<init>", at = @At(value = "INVOKE", remap = false,
			target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
	private void afterFabricHooks(CallbackInfo ci) {
		Slabify.INSTANCE.onPostInitialize();
		SlabifyClient.INSTANCE.onClientPostInitialize();
	}
}