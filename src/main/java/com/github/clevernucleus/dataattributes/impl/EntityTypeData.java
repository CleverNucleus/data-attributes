package com.github.clevernucleus.dataattributes.impl;

import java.util.HashMap;
import java.util.Map;

import com.github.clevernucleus.dataattributes.mutable.MutableDefaultAttributeContainer;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityTypeData {
	public final Map<Identifier, Double> data;
	
	public EntityTypeData() {
		this.data = new HashMap<Identifier, Double>();
	}
	
	public EntityTypeData(final Map<String, Double> data) {
		this();
		data.forEach((key, value) -> this.data.put(new Identifier(key), value));
	}
	
	public void build(DefaultAttributeContainer.Builder builder, DefaultAttributeContainer container) {
		((MutableDefaultAttributeContainer)container).copy(builder);
		
		for(Identifier key : this.data.keySet()) {
			EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(key);
			
			if(entityAttribute == null) continue;
			
			double value = this.data.get(key);
			double clamp = entityAttribute.clamp(value);
			builder.add(entityAttribute, clamp);
		}
	}
	
	public void readFromNbt(NbtCompound tag) {
		tag.getKeys().forEach(key -> this.data.put(new Identifier(key), tag.getDouble(key)));
	}
	
	public void writeToNbt(NbtCompound tag) {
		this.data.forEach((key, value) -> tag.putDouble(key.toString(), value));
	}
}
