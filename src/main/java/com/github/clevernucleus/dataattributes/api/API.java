package com.github.clevernucleus.dataattributes.api;

import java.util.function.Supplier;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 
 * The core API access - provides access to the modid and safe static attribute instantiation.
 * @author CleverNucleus
 *
 */
public final class API {
	
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
	 * Adds or subtracts the current and adding values, with diminishing returns tending towards the limit.
	 * @param current Current value (add to or subtract from).
	 * @param adding Modifying value (+/-).
	 * @param limit The limiting value (output respects this, unless limit == 0).
	 * @return The diminishing returns.
	 */
	public static double add(final double current, final double adding, final double limit) {
		if(limit == 0.0D) return current;
		if(adding > 0.0D) return limit * ((current + adding) / (limit + adding));
		if(adding < 0.0D) return adding + current - (current * adding / limit);
		
		return current;
	}
}
