package com.github.clevernucleus.dataattributes.impl.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import net.minecraft.util.Identifier;

public final class PropertiesJson {
	@Expose private HashMap<String, HashMap<String, Float>> values;
	
	private PropertiesJson() {}
	
	public void merge(Map<Identifier, Map<String, Float>> propertiesIn) {
		for(String key : this.values.keySet()) {
			Identifier identifier = new Identifier(key);
			Map<String, Float> properties = propertiesIn.getOrDefault(identifier, new HashMap<String, Float>());
			this.values.get(key).forEach(properties::put);
			propertiesIn.put(identifier, properties);
		}
	}
}
