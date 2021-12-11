package com.github.clevernucleus.dataattributes.api.util;

import java.util.UUID;

import net.minecraft.item.Item;

/**
 * Helper class that exposes {@link Item#ATTACK_DAMAGE_MODIFIER_ID} and {@link Item#ATTACK_SPEED_MODIFIER_ID}.
 * 
 * @author CleverNucleus
 *
 */
public final class ItemFields extends Item {
	private ItemFields() { super(new Item.Settings()); }
	
	public static UUID attackDamageModifierID() { return ATTACK_DAMAGE_MODIFIER_ID; }
	
	public static UUID attackSpeedModifierID() { return ATTACK_DAMAGE_MODIFIER_ID; }
}
