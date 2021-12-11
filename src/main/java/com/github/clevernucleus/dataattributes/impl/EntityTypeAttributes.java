package com.github.clevernucleus.dataattributes.impl;

import java.util.HashMap;
import java.util.Map;

import com.github.clevernucleus.dataattributes.mutable.MutableDefaultAttributeContainer;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityTypeAttributes {
	private final Map<Identifier, Double> attributes;
	
	public EntityTypeAttributes() {
		this.attributes = new HashMap<Identifier, Double>();
	}
	
	public EntityTypeAttributes(Map<String, Double> attributes) {
		this();
		attributes.forEach((key, value) -> this.attributes.put(new Identifier(key), value));
	}
	
	public void build(DefaultAttributeContainer.Builder builderIn, DefaultAttributeContainer containerIn) {
		((MutableDefaultAttributeContainer)containerIn).build(builderIn);
		
		for(Identifier identifier : this.attributes.keySet()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			double value = this.attributes.get(identifier);
			double clamp = attribute.clamp(value);
			
			builderIn.add(attribute, clamp);
		}
	}
	
	public void readFromNbt(NbtCompound tag) {
		NbtList attributesTag = tag.getList("Attributes", NbtType.COMPOUND);
		
		for(int i = 0; i < attributesTag.size(); i++) {
			NbtCompound entry = attributesTag.getCompound(i);
			String key = entry.getString("Key");
			Identifier identifier = new Identifier(key);
			double value = entry.getDouble("Value");
			
			this.attributes.put(identifier, value);
		}
	}
	
	public void writeToNbt(NbtCompound tag) {
		NbtList attributesTag = new NbtList();
		
		for(Identifier identifier : this.attributes.keySet()) {
			NbtCompound entry = new NbtCompound();
			entry.putString("Key", identifier.toString());
			entry.putDouble("Value", this.attributes.get(identifier));
			attributesTag.add(entry);
		}
		
		tag.put("Attributes", attributesTag);
	}
}
