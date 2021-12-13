package com.github.clevernucleus.dataattributes.api.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * Implement this in your Item class.
 * 
 * @author CleverNucleus
 *
 */
public interface IItemEntityAttributeModifiers {
	
	/**
	 * This method provides a mutable attribute modifier multimap so that items can have dynamically changing modifiers based on nbt.
	 * @param itemStack
	 * @param slot
	 * @return
	 */
	default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack itemStack, EquipmentSlot slot) {
		return HashMultimap.create();
	}
}
