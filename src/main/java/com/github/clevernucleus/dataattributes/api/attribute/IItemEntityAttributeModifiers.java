package com.github.clevernucleus.dataattributes.api.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;


public interface IItemEntityAttributeModifiers {
	
	
	default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack itemStack, EquipmentSlot slot) {
		return HashMultimap.create();
	}
}
