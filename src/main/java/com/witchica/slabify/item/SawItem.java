package com.witchica.slabify.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SawItem extends Item {
    public SawItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        int maxDamage = itemStack.getMaxDamage();
        int damage = maxDamage - itemStack.getDamageValue();

        list.add(Component.literal("" + damage + " / " + maxDamage).withStyle(ChatFormatting.LIGHT_PURPLE));

        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
