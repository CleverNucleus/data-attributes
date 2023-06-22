package com.github.clevernucleus.dataattributes.mixin.item;

import net.minecraft.item.Equipment;
import org.spongepowered.asm.mixin.Mixin;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Mixin(Item.class)
abstract class ItemMixin implements ItemHelper {
	
	@Override
	public SoundEvent getEquipSound(final ItemStack itemStack) {
		if(!(this instanceof Equipment)) return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;

		return ((Equipment)this).getEquipSound();
	}
}
