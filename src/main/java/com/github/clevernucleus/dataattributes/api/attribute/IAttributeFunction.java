package com.github.clevernucleus.dataattributes.api.attribute;

import net.minecraft.util.Identifier;

/**
 * An interface to provide a layer of abstraction to reference an attribute's attribute functions.
 * 
 * @author CleverNucleus
 *
 */
public interface IAttributeFunction {
	
	/**
	 * @return The affected attribute's registry key.
	 */
	Identifier attribute();
	
	/**
	 * @return The affected attribute's behaviour (if it uses flat or diminishing addition).
	 */
	AttributeBehaviour behaviour();
	
	/**
	 * @return A multiplier applied to the resulting change in value (e.g. for every one point changed in the parent attribute).
	 */
	double multiplier();
}
