package com.witchica.slabify.mixin;

import com.witchica.slabify.block.base.BaseSlabifyBlock;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tool.class)
public class ToolMixin {
    @ModifyVariable(method = "getMiningSpeed", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public BlockState injected(BlockState blockState) {
        if(blockState.getBlock() instanceof BaseSlabifyBlock slab) {
            return slab.getParent().defaultBlockState();
        } else {
            return blockState;
        }
    }
}
