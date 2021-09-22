package com.github.clevernucleus.dataattributes.impl.attribute;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityTypeObject {
	private Map<Identifier, Double> attributes;
	
	public EntityTypeObject(Map<Identifier, Double> attributes) {
		this.attributes = new HashMap<Identifier, Double>();
		
		if(attributes != null) {
			this.attributes.putAll(attributes);
		}
	}
	
	public void buildContainer(DefaultAttributeContainer.Builder builderIn, DefaultAttributeContainer containerIn) {
		((IMutableContainer)containerIn).build(builderIn);
		
		for(Identifier identifier : this.attributes.keySet()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			double value = this.attributes.get(identifier);
			double clamp = attribute.clamp(value);
			
			builderIn.add(attribute, clamp);
		}
	}
	
	public void write(NbtCompound tagIn) {
		NbtList list = new NbtList();
		
		for(Identifier identifier : this.attributes.keySet()) {
			NbtCompound entry = new NbtCompound();
			entry.putString("Key", identifier.toString());
			entry.putDouble("Value", this.attributes.get(identifier));
			list.add(entry);
		}
		
		tagIn.put("Attributes", list);
	}
	
	public static EntityTypeObject read(NbtCompound tagIn) {
		if(!tagIn.contains("Attributes")) return new EntityTypeObject(new HashMap<Identifier, Double>());
		
		Map<Identifier, Double> attributes = new HashMap<Identifier, Double>();
		NbtList list = tagIn.getList("Attributes", NbtType.COMPOUND);
		
		for(int i = 0; i < list.size(); i++) {
			NbtCompound entry = list.getCompound(i);
			Identifier identifier = new Identifier(entry.getString("Key"));
			attributes.put(identifier, entry.getDouble("Value"));
		}
		
		EntityTypeObject entityTypeObject = new EntityTypeObject(attributes);
		
		return entityTypeObject;
	}
}
