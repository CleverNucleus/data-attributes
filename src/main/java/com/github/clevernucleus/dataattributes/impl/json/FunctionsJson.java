package com.github.clevernucleus.dataattributes.impl.json;

import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.Multimap;
import com.google.gson.annotations.Expose;

import net.minecraft.util.Identifier;

public final class FunctionsJson {
	@Expose private HashMap<String, Collection<AttributeFunctionJson>> values;
	
	private FunctionsJson() {}
	
	public void merge(Multimap<Identifier, AttributeFunctionJson> functionsIn) {
		for(String key : this.values.keySet()) {
			Identifier identifier = new Identifier(key);
			this.values.get(key).forEach(function -> functionsIn.put(identifier, function));
		}
	}
}
