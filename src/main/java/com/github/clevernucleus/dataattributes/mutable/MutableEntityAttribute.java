package com.github.clevernucleus.dataattributes.mutable;

import java.util.Map;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;
import com.github.clevernucleus.dataattributes.json.AttributeFunctionJson;

public interface MutableEntityAttribute extends IEntityAttribute {
	void override(String translationKey, double minValue, double maxValue, double fallbackValue, double incrementValue, StackingBehaviour stackingBehaviour);
	
	void properties(Map<String, String> properties);
	
	void addParent(MutableEntityAttribute attributeIn, final AttributeFunctionJson function);
	
	void addChild(MutableEntityAttribute attributeIn, final AttributeFunctionJson function);
	
	void clear();
	
	double sum(final double k, final double k2, final double v, final double v2);
	
	boolean contains(MutableEntityAttribute a, MutableEntityAttribute b);
	
	Map<IEntityAttribute, AttributeFunctionJson> parentsMutable();
	
	Map<IEntityAttribute, AttributeFunctionJson> childrenMutable();
}
