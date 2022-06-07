package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

@Mixin(ArmorItem.class)
abstract class ArmorItemMixin implements ItemHelper {
	
	@Override
	public int getProtection(final ItemStack itemStack) {
		return ((ArmorItem)(Object)this).getProtection();
	}
	
	@Override
	public float getToughness(final ItemStack itemStack) {
		return ((ArmorItem)(Object)this).getToughness();
	}
}
