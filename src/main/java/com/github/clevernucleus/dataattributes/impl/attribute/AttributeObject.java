package com.github.clevernucleus.dataattributes.impl.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.github.clevernucleus.dataattributes.impl.json.AttributeFunctionJson;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public final class AttributeObject {
	private final Collection<AttributeFunctionJson> functions;
	private final Map<String, String> properties;
	
	public AttributeObject() {
		this.functions = new HashSet<AttributeFunctionJson>();
		this.properties = new HashMap<String, String>();
	}
	
	public void appendFunctions(Collection<AttributeFunctionJson> functionsIn) {
		this.functions.addAll(functionsIn);
	}
	
	public void appendProperties(Map<String, String> propertiesIn) {
		this.properties.putAll(propertiesIn);
	}
	
	public void impart(IMutableAttribute attribute) {
		attribute.setFunctions(this.functions);
		attribute.setProperties(this.properties);
	}
	
	public Collection<AttributeFunctionJson> functions() {
		return this.functions;
	}
	
	public void write(NbtCompound tagIn) {
		NbtList functions = new NbtList();
		NbtList properties = new NbtList();
		
		for(AttributeFunctionJson function : this.functions) {
			NbtCompound entry = new NbtCompound();
			function.write(entry);
			functions.add(entry);
		}
		
		for(String key : this.properties.keySet()) {
			NbtCompound entry = new NbtCompound();
			entry.putString("Key", key);
			entry.putString("Value", this.properties.get(key));
			properties.add(entry);
		}
		
		tagIn.put("Functions", functions);
		tagIn.put("Properties", properties);
	}
	
	public static AttributeObject read(NbtCompound tagIn) {
		AttributeObject attributeObject = new AttributeObject();
		
		if(!tagIn.contains("Functions")) return attributeObject;
		
		NbtList functions = tagIn.getList("Functions", NbtType.COMPOUND);
		
		for(int i = 0; i < functions.size(); i++) {
			NbtCompound entry = functions.getCompound(i);
			AttributeFunctionJson attributeFunctionJson = AttributeFunctionJson.read(entry);
			attributeObject.functions.add(attributeFunctionJson);
		}
		
		if(!tagIn.contains("Properties")) return attributeObject;
		
		NbtList properties = tagIn.getList("Properties", NbtType.COMPOUND);
		
		for(int i = 0; i < properties.size(); i++) {
			NbtCompound entry = properties.getCompound(i);
			attributeObject.properties.put(entry.getString("Key"), entry.getString("Value"));
		}
		
		return attributeObject;
	}
}
