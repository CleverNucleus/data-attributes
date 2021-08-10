package com.github.clevernucleus.dataattributes.impl.attribute;

import java.util.Collection;
import java.util.Map;

import com.github.clevernucleus.dataattributes.impl.json.AttributeFunctionJson;

public interface IMutableAttribute {
	
	void setDefaultValue(final double defaultValue);
	
	void setMinValue(final double minValue);
	
	void setMaxValue(final double maxValue);
	
	void setTranslationKey(final String translationKey);
	
	void setFunctions(final Collection<AttributeFunctionJson> functions);
	
	void setProperties(final Map<String, Float> properties);
	
	void reset();
}
