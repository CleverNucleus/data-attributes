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
	 * @return The attribute's minimum value;
	 */
	double minValue();
	
	/**
	 * @return The attribute's maximum value;
	 */
	double maxValue();
	
	/**
	 * @return The attribute's stacking behaviour.
	 */
	StackingBehaviour stackingBehaviour();
	
	/**
	 * @return An immutable map of the function-parents attached to this attribute.
	 * @since 1.3.0
	 */
	Map<IEntityAttribute, IAttributeFunction> parents();
	
	/**
	 * @return An immutable map of the function-children attached to this attribute.
	 * @since 1.3.0
	 */
	Map<IEntityAttribute, IAttributeFunction> children();
	
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
