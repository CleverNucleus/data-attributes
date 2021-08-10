package com.github.clevernucleus.dataattributes.api.attribute;

/**
 * A functional interface for addition using lambdas and limits.
 * 
 * @author CleverNucleus
 *
 */
@FunctionalInterface
public interface AdditionFunction {
	
	/**
	 * @param current the current/primary value.
	 * @param adding the adding/secondary value.
	 * @param limit the limiting value if the implementation needs it (such as for diminishing returns).
	 * @return the implementation's sum of the inputs.
	 */
	double add(final double current, final double adding, final double limit);
}
