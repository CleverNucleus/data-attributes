package com.github.clevernucleus.dataattributes.api;

import java.util.function.Supplier;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 
 * The core API access - provides access to the modid and safe static attribute instantiation.
 * @author CleverNucleus
 *
 */
public final class DataAttributesAPI {
	
	/**
	 * The modid for Data Attributes.
	 */
	public static final String MODID = "dataattributes";
	
	/**
	 * @param attributeKey Attribute registry key.
	 * @return A supplier getting the registered attribute assigned to the input key. 
	 * Uses a supplier because attributes added using json are null until datapacks are loaded/synced to the client,
	 * so static initialisation would not work. Using this you can safely access an attribute through a static reference.
	 */
	public static Supplier<EntityAttribute> getAttribute(final Identifier attributeKey) {
		return () -> Registry.ATTRIBUTE.get(attributeKey);
	}
	
	/**
	 * Checks to see if the livingEntity has the attributes required of the itemStack's attribute modifiers.
	 * @param livingEntity
	 * @param itemStack
	 * @param slot
	 * @return False if the livingEntity is null or the livingEntity's attribute container does not contain an attribute present on 
	 * the itemStack. Returns true otherwise or if the itemStack is empty (i.e. has no modifiers).
	 */
	public static boolean checkHasAttributes(final LivingEntity livingEntity, final ItemStack itemStack, final EquipmentSlot slot) {
		if(livingEntity == null) return false;
		if(itemStack.isEmpty()) return true;
		
		AttributeContainer container = livingEntity.getAttributes();
		var modifiers = itemStack.getAttributeModifiers(slot);
		var attributes = modifiers.keySet();
		
		for(EntityAttribute attribute : attributes) {
			if(!container.hasAttribute(attribute)) return false;
		}
		
		return true;
	}
}
