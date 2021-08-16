package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.UUID;

import net.minecraft.entity.attribute.EntityAttributeModifier;

/**
 * Allows the value of a modifier to be changed before the modifier is applied.
 * 
 * @author CleverNucleus
 *
 */
public interface IEntityAttributeModifier {
	
	/**
	 * @return The modifier's uuid.
	 */
	UUID getId();
	
	/**
	 * @return The modifier's name.
	 */
	String getName();
	
	/**
	 * @return The modifier's operation.
	 */
	EntityAttributeModifier.Operation getOperation();
	
	/**
	 * @return The modifier's (mutable) value.
	 */
	double getValue();
	
	/**
	 * Set's the value of the modifier.
	 * @param value
	 */
	void setValue(final double value);
}
