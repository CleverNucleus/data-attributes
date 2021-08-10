package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.Collection;

/**
 * 
 * EntityAttribute's implement this through a mixin, and can be cast to this interface to get additional data added by Data Attributes.
 * 
 * @author CleverNucleus
 *
 */
public interface IAttribute {
	
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
	 * @param value A value, usually the attribute's current data value.
	 * @return For EntityAttribute, returns the input. For ClampedEntityAttribute, returns a clamped value between the min and max.
	 */
	double clamp(double value);
	
	/**
	 * @return The attribute's translation key (references a lang json name).
	 */
	String getTranslationKey();
	
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
	float getProperty(final String property);
	
	/**
	 * @return An immutable collection of the attribute functions attached to this attribute.
	 */
	Collection<IAttributeFunction> functions();
}
