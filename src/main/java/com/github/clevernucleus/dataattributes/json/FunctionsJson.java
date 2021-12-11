package com.github.clevernucleus.dataattributes.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public final class FunctionsJson {
	@Expose private HashMap<String, HashMap<String, Double>> values;
	
	private FunctionsJson() {}
	
	public void merge(Map<String, Map<String, Double>> functionsIn) {
		for(String key : this.values.keySet()) {
			Map<String, Double> functions = functionsIn.getOrDefault(key, new HashMap<String, Double>());
			this.values.get(key).forEach(functions::put);
			functionsIn.put(key, functions);
		}
	}
}
