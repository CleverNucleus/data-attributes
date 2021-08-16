package com.github.clevernucleus.dataattributes.impl.attribute;

import java.util.UUID;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttributeModifier;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public final class MutableEntityAttributeModifier implements IEntityAttributeModifier {
	private final EntityAttributeModifier modifier;
	private double value;
	
	public MutableEntityAttributeModifier(final EntityAttributeModifier modifier) {
		this.modifier = modifier;
		this.value = modifier.getValue();
	}
	
	public EntityAttributeModifier getModifier() {
		return new EntityAttributeModifier(this.getId(), this.getName(), this.getValue(), this.getOperation());
	}
	
	@Override
	public UUID getId() {
		return this.modifier.getId();
	}
	
	@Override
	public String getName() {
		return this.modifier.getName();
	}
	
	@Override
	public EntityAttributeModifier.Operation getOperation() {
		return this.modifier.getOperation();
	}
	
	@Override
	public double getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(final double value) {
		this.value = value;
	}
}
