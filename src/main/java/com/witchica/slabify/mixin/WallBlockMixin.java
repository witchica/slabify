package com.witchica.slabify.mixin;

import com.witchica.slabify.block.SlabifyWallBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public class WallBlockMixin {
    @Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
    public void connectsTo(BlockState blockState, boolean bl, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if(blockState.getBlock() instanceof SlabifyWallBlock) {
            cir.setReturnValue(true);
        }
    }
}
