package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

@Mixin(Item.class)
abstract class ItemMixin implements ItemHelper {

	@Override
	public SoundEvent getEquipSound(final ItemStack itemStack) {
		return ((Item)(Object)this).getEquipSound();
	}
}
