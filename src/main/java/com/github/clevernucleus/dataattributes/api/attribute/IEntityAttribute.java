package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * EntityAttribute's implement this through a mixin, and can be cast to this interface to get additional data added by Data Attributes.
 * 
 * @author CleverNucleus
 *
 */
public interface IEntityAttribute {
	
	/**
	 * @return The attribute's default value.
	 */
	double getDefaultValue();
	
	/**
	 * @return The attribute's minimum value;
	 */
	double getMinValue();
	
	/**
	 * @return The attribute's maximum value;
	 */
	double getMaxValue();
	
	/**
	 * Checks if instances of this attribute should synchronize values to clients.
	 * @return
	 */
	boolean isTracked();
	
	/**
	 * @param value A value, usually the attribute's current data value.
	 * @return For EntityAttribute, returns the input. For ClampedEntityAttribute, returns a clamped value between the min and max.
	 */
	double clamp(double value);
	
	/**
	 * Stacks two values based on this attribute's stacking behaviour.
	 * @param current
	 * @param adding
	 * @return
	 */
	double stack(double current, double adding);
	
	/**
	 * @return The attribute's translation key (references a lang json name).
	 */
	String getTranslationKey();
	
	/**
	 * @return An immutable map of the function-parents attached to this attribute.
	 */
	Map<IEntityAttribute, Double> parents();
	
	/**
	 * @return An immutable map of the function-children attached to this attribute.
	 */
	Map<IEntityAttribute, Double> children();
	
	/**
	 * @return An immutable collection of the properties' keys attached to this attribute.
	 */
	Collection<String> properties();
	
	/**
	 * @param property A property key.
	 * @return true if this attribute has the input property key; false if not, or if the input is null.
	 */
	boolean hasProperty(final String property);
	
	/**
	 * @param property A property key.
	 * @return This attribute's property value assigned to the input property's key. If it does not exist or is null, returns 0.0F.
	 */
	String getProperty(final String property);
}
