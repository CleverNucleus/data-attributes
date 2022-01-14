package com.github.clevernucleus.dataattributes.mutable;

import java.util.Map;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;

public interface MutableEntityAttribute extends IEntityAttribute {
	Map<IEntityAttribute, Double> parentsMutable();
	
	Map<IEntityAttribute, Double> childrenMutable();
	
	double sumStack(double positives, double negatives);
	
	double stack(double current, double input);
	
	boolean contains(MutableEntityAttribute a, MutableEntityAttribute b);
	
	void addParent(MutableEntityAttribute attributeIn, final double multiplier);
	
	void addChild(MutableEntityAttribute attributeIn, final double multiplier);
	
	void transferAttribute(String translationKey, double minValue, double maxValue, double fallbackValue, StackingBehaviour stackingBehaviour);
	
	void transferProperties(Map<String, String> properties);
	
	void clear();
}
