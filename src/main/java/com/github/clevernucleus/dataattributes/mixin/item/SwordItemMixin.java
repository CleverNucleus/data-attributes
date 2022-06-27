package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

@Mixin(SwordItem.class)
abstract class SwordItemMixin extends ItemMixin {
	
	@Override
	public float getAttackDamage(final ItemStack itemStack) {
		return ((SwordItem)(Object)this).getAttackDamage();
	}
}
