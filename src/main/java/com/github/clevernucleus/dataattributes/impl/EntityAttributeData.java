package com.github.clevernucleus.dataattributes.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.clevernucleus.dataattributes.json.AttributeOverrideJson;
import com.github.clevernucleus.dataattributes.json.AttributeFunctionJson;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityAttributeData implements NbtIO {
	private AttributeOverrideJson attribute;
	private final Map<Identifier, AttributeFunctionJson> functions;
	private final Map<String, String> properties;
	
	public EntityAttributeData() {
		this.functions = new HashMap<Identifier, AttributeFunctionJson>();
		this.properties = new HashMap<String, String>();
	}
	
	public EntityAttributeData(final AttributeOverrideJson attribute) {
		this();
		this.attribute = attribute;
	}
	
	public void override(final Identifier identifier, BiFunction<Identifier, EntityAttribute, EntityAttribute> function) {
		if(this.attribute == null) return;
		EntityAttribute entityAttribute = function.apply(identifier, this.attribute.create());
		this.attribute.override((MutableEntityAttribute)entityAttribute);
	}
	
	public void copy(EntityAttribute entityAttributeIn) {
		MutableEntityAttribute mutableEntityAttribute = (MutableEntityAttribute)entityAttributeIn;
		mutableEntityAttribute.properties(this.properties);
		
		for(Identifier identifier : this.functions.keySet()) {
			EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
			
			if(entityAttribute == null) continue;
			
			AttributeFunctionJson function = this.functions.get(identifier);
			mutableEntityAttribute.addChild((MutableEntityAttribute)entityAttribute, function);
		}
	}
	
	public void putFunctions(Map<Identifier, AttributeFunctionJson> functions) {
		this.functions.putAll(functions);
	}
	
	public void putProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag) {
		if(tag.contains("Attribute")) {
			this.attribute = new AttributeOverrideJson();
			this.attribute.readFromNbt(tag.getCompound("Attribute"));
		}
		
		NbtCompound functions = tag.getCompound("Functions");
		functions.getKeys().forEach(key -> this.functions.put(new Identifier(key), AttributeFunctionJson.read(functions.getByteArray(key))));
		
		NbtCompound properties = tag.getCompound("Properties");
		properties.getKeys().forEach(key -> this.properties.put(key, properties.getString(key)));
	}
	
	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtCompound attribute = new NbtCompound();
		
		if(this.attribute != null) {
			this.attribute.writeToNbt(attribute);
			tag.put("Attribute", attribute);
		}
		
		NbtCompound functions = new NbtCompound();
		this.functions.forEach((key, value) -> functions.putByteArray(key.toString(), value.write()));
		tag.put("Functions", functions);
		
		NbtCompound properties = new NbtCompound();
		this.properties.forEach((key, value) -> properties.putString(key.toString(), value));
		tag.put("Properties", properties);
	}
}
