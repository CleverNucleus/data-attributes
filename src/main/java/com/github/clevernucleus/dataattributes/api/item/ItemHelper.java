package com.github.clevernucleus.dataattributes.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

/**
 * Helper interface to enable stack-specific operations. For example, using nbt data for stack-specific attributes. Can be 
 * used in tandem with FabricItem.
 * 
 * @author CleverNucleus
 *
 */
public interface ItemHelper {
	
	/**
	 * Fired on the constructor for itemstacks. All items are automatically an instance of ItemHelper, so checks should be 
	 * made when using this to avoid running unnecessary logic on all itemstack creation events. Example usage includes 
	 * attaching nbt data when an itemstack is first created.
	 * @param itemStack
	 * @param count
	 */
	default void onStackCreated(final ItemStack itemStack, final int count) {}
	
	/**
	 * ItemStack dependent version of SwordItem#getAttackDamage and MiningToolItem#getAttackDamage. Default 
	 * implementation returns the aforementioned.
	 * @param itemStack
	 * @return
	 */
	default float getAttackDamage(final ItemStack itemStack) { return 0.0F; }
	
	/**
	 * ItemStack dependent version of ArmorItem#getProtection. Default implementation returns aforementioned.
	 * @param itemStack
	 * @return
	 */
	default int getProtection(final ItemStack itemStack) { return 0; }
	
	/**
	 * ItemStack dependent version of ArmorItem#getToughness. Default implementation returns aforementioned.
	 * @param itemStack
	 * @return
	 */
	default float getToughness(final ItemStack itemStack) { return 0.0F; }
	
	/**
	 * ItemStack dependent version of Item#getEquipSound. Default implementation returns aforementioned.
	 * @param itemStack
	 * @return
	 */
	default SoundEvent getEquipSound(final ItemStack itemStack) { return null; }
}
