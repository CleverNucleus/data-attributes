package com.github.clevernucleus.dataattributes.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.clevernucleus.dataattributes.json.AttributeOverrideJson;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class AttributeWrapper {
	private AttributeOverrideJson attribute;
	private final Map<Identifier, Double> functions;
	private final Map<String, String> properties;
	
	public AttributeWrapper() {
		this.functions = new HashMap<Identifier, Double>();
		this.properties = new HashMap<String, String>();
	}
	
	public AttributeWrapper(final AttributeOverrideJson attribute) {
		this();
		this.attribute = attribute;
	}
	
	public void override(final Identifier identifier, BiFunction<Identifier, EntityAttribute, EntityAttribute> function) {
		if(this.attribute == null) return;
		
		EntityAttribute entityAttribute = function.apply(identifier, this.attribute.create());
		this.attribute.transfer((MutableEntityAttribute)entityAttribute);
	}
	
	public void transfer(EntityAttribute entityAttribute) {
		MutableEntityAttribute dataAttribute = (MutableEntityAttribute)entityAttribute;
		dataAttribute.transferProperties(this.properties);
		
		for(Identifier identifier : this.functions.keySet()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			double multiplier = this.functions.get(identifier);
			
			dataAttribute.addChild((MutableEntityAttribute)attribute, multiplier);
		}
	}
	
	public void putFunctions(Map<Identifier, Double> functions) {
		this.functions.putAll(functions);
	}
	
	public void putProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}
	
	public void readFromNbt(NbtCompound tag) {
		if(tag.contains("Attribute")) {
			NbtCompound attributeTag = tag.getCompound("Attribute");
			this.attribute = new AttributeOverrideJson();
			this.attribute.readFromNbt(attributeTag);
		}
		
		NbtList functionsTag = tag.getList("Functions", NbtType.COMPOUND);
		
		for(int i = 0; i < functionsTag.size(); i++) {
			NbtCompound entry = functionsTag.getCompound(i);
			String key = entry.getString("Key");
			double value = entry.getDouble("Value");
			this.functions.put(new Identifier(key), value);
		}
		
		NbtList propertiesTag = tag.getList("Properties", NbtType.COMPOUND);
		
		for(int i = 0; i < propertiesTag.size(); i++) {
			NbtCompound entry = propertiesTag.getCompound(i);
			String key = entry.getString("Key");
			String value = entry.getString("Value");
			this.properties.put(key, value);
		}
	}
	
	public void writeToNbt(NbtCompound tag) {
		if(this.attribute != null) {
			NbtCompound attributeTag = new NbtCompound();
			this.attribute.writeToNbt(attributeTag);
			tag.put("Attribute", attributeTag);
		}
		
		NbtList functionsTag = new NbtList();
		
		for(Identifier key : this.functions.keySet()) {
			NbtCompound entry = new NbtCompound();
			entry.putString("Key", key.toString());
			entry.putDouble("Value", this.functions.get(key));
			functionsTag.add(entry);
		}
		
		NbtList propertiesTag = new NbtList();
		
		for(String key : this.properties.keySet()) {
			NbtCompound entry = new NbtCompound();
			entry.putString("Key", key);
			entry.putString("Value", this.properties.get(key));
			propertiesTag.add(entry);
		}
		
		tag.put("Functions", functionsTag);
		tag.put("Properties", propertiesTag);
	}
}
