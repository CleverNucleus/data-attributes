package com.github.clevernucleus.dataattributes.api.attribute;

/**
 * @since 1.3.0
 * @author CleverNucleus
 */
public interface IAttributeFunction {
	
	/**
	 * @return The FunctionBehaviour associated with this attribute function.
	 */
	FunctionBehaviour behaviour();
	
	/**
	 * @return The value associated with this attribute function.
	 */
	double value();
}
