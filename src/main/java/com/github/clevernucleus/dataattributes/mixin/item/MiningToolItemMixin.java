package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

@Mixin(MiningToolItem.class)
abstract class MiningToolItemMixin extends ItemMixin {
	
	@Override
	public float getAttackDamage(final ItemStack itemStack) {
		return ((MiningToolItem)(Object)this).getAttackDamage();
	}
}
