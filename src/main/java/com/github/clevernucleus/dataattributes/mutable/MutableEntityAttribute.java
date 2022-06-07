package com.github.clevernucleus.dataattributes.mutable;

import java.util.Map;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;

public interface MutableEntityAttribute extends IEntityAttribute {
	void override(String translationKey, double minValue, double maxValue, double fallbackValue, double incrementValue, StackingBehaviour stackingBehaviour);
	
	void properties(Map<String, String> properties);
	
	void addParent(MutableEntityAttribute attributeIn, final double multiplier);
	
	void addChild(MutableEntityAttribute attributeIn, final double multiplier);
	
	void clear();
	
	double sum(final double k, final double v);
	
	boolean contains(MutableEntityAttribute a, MutableEntityAttribute b);
	
	Map<IEntityAttribute, Double> parentsMutable();
	
	Map<IEntityAttribute, Double> childrenMutable();
}
