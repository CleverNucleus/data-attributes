package com.github.clevernucleus.dataattributes.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.clevernucleus.dataattributes.impl.AttributeManager.Tuple;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;
import com.github.clevernucleus.dataattributes.mutable.MutableDefaultAttributeContainer;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class AttributeContainerHandler {
	private Map<Integer, Tuple<DefaultAttributeContainer>> implicitContainers;
	private Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> explicitContainers;

	protected AttributeContainerHandler() {
		this.implicitContainers = ImmutableMap.of();
		this.explicitContainers = ImmutableMap.of();
	}

	protected AttributeContainer getContainer(final EntityType<? extends LivingEntity> entityType, final LivingEntity livingEntity) {
		DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
		((MutableDefaultAttributeContainer)DefaultAttributeRegistry.get(entityType)).copy(builder);
		
		for(int i = 0; i < this.implicitContainers.size(); i++) {
			Tuple<DefaultAttributeContainer> tuple = this.implicitContainers.get(i);
			Class<? extends LivingEntity> type = tuple.livingEntity();

			if(type.isInstance(livingEntity)) {
				((MutableDefaultAttributeContainer)tuple.value()).copy(builder);
			}
		}

		if(this.explicitContainers.containsKey(entityType)) {
			((MutableDefaultAttributeContainer)this.explicitContainers.get(entityType)).copy(builder);
		}

		AttributeContainer container = new AttributeContainer(builder.build());
		((MutableAttributeContainer)container).setLivingEntity(livingEntity);

		return container;
	}

	@SuppressWarnings("unchecked")
	protected void buildContainers(final Map<Identifier, EntityTypeData> entityTypeDataIn, Map<Identifier, Tuple<Integer>> entityTypeInstances) {
		Collection<Identifier> entityTypes = Registry.ENTITY_TYPE.getIds().stream().filter(id -> DefaultAttributeRegistry.hasDefinitionFor(Registry.ENTITY_TYPE.get(id))).collect(Collectors.toSet());
		ImmutableMap.Builder<Integer, Tuple<DefaultAttributeContainer>> implicitContainers = ImmutableMap.builder();
		ImmutableMap.Builder<EntityType<? extends LivingEntity>, DefaultAttributeContainer> explicitContainers = ImmutableMap.builder();
		Map<Integer, Tuple<Identifier>> orderedEntityTypes = new HashMap<>();

		for(Identifier identifier : entityTypeDataIn.keySet()) {
			if(entityTypeInstances.containsKey(identifier)) {
				Tuple<Integer> tuple = entityTypeInstances.get(identifier);
				orderedEntityTypes.put(tuple.value(), new Tuple<Identifier>(tuple.livingEntity(), identifier));
			}
			if(!entityTypes.contains(identifier)) continue;
			EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>)Registry.ENTITY_TYPE.get(identifier);
			DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
			EntityTypeData entityTypeData = entityTypeDataIn.get(identifier);
			entityTypeData.build(builder, DefaultAttributeRegistry.get(entityType));
			explicitContainers.put(entityType, builder.build());
		}

		final int size = orderedEntityTypes.size();
		final int max = orderedEntityTypes.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);

		for(Map.Entry<Integer, Tuple<Identifier>> entry : orderedEntityTypes.entrySet()) {
			Tuple<Identifier> tuple = entry.getValue();
			Identifier identifier = tuple.value();
			final int hierarchy = entry.getKey();
			final int index = Math.round((float)size * (float)hierarchy / (float)max) - 1;
			DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
			EntityTypeData entityTypeData = entityTypeDataIn.get(identifier);
			entityTypeData.build(builder, null);
			implicitContainers.put(index, new Tuple<DefaultAttributeContainer>(tuple.livingEntity(), builder.build()));
		}

		this.implicitContainers = implicitContainers.build();
		this.explicitContainers = explicitContainers.build();
	}
}
