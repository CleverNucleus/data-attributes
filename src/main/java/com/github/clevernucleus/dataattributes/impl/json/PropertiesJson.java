package com.github.clevernucleus.dataattributes.impl.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import net.minecraft.util.Identifier;

public final class PropertiesJson {
	@Expose private HashMap<String, HashMap<String, String>> values;
	
	private PropertiesJson() {}
	
	public void merge(Map<Identifier, Map<String, String>> propertiesIn) {
		for(String key : this.values.keySet()) {
			Identifier identifier = new Identifier(key);
			Map<String, String> properties = propertiesIn.getOrDefault(identifier, new HashMap<String, String>());
			this.values.get(key).forEach(properties::put);
			propertiesIn.put(identifier, properties);
		}
	}
}
