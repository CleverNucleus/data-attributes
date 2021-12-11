package com.github.clevernucleus.dataattributes.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public final class PropertiesJson {
	@Expose private HashMap<String, HashMap<String, String>> values;
	
	private PropertiesJson() {}
	
	public void merge(Map<String, Map<String, String>> propertiesIn) {
		for(String key : this.values.keySet()) {
			Map<String, String> properties = propertiesIn.getOrDefault(key, new HashMap<String, String>());
			this.values.get(key).forEach(properties::put);
			propertiesIn.put(key, properties);
		}
	}
}
