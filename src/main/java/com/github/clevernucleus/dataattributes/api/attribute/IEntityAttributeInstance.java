package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.UUID;

/**
 * 
 * Access to an entity attribute instance.
 * @author CleverNucleus
 *
 */
public interface IEntityAttributeInstance {
	
	/**
	 * Changes the value of the input modifier (if it exists) and updates the instance and all children.
	 * @param uuid The uuid of the modifier.
	 * @param value The value to change the modifier to.
	 */
	void updateModifier(final UUID uuid, final double value);
}
