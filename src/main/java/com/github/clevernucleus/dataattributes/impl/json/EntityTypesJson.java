package com.github.clevernucleus.dataattributes.impl.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

import net.minecraft.util.Identifier;

public final class EntityTypesJson {
	@Expose private HashMap<String, HashMap<String, Double>> values;
	
	private EntityTypesJson() {}
	
	public void merge(Map<Identifier, Map<Identifier, Double>> entityTypesIn) {
		for(String key : this.values.keySet()) {
			Identifier identifier = new Identifier(key);
			Map<Identifier, Double> entityTypes = entityTypesIn.getOrDefault(identifier, new HashMap<Identifier, Double>());
			this.values.get(key).forEach((attribute, value) -> entityTypes.put(new Identifier(attribute), value));
			entityTypesIn.put(identifier, entityTypes);
		}
	}
}
