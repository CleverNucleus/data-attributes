package com.github.clevernucleus.dataattributes.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.clevernucleus.dataattributes.api.DataAttributesAPI;
import com.github.clevernucleus.dataattributes.json.AttributeOverrideJson;
import com.github.clevernucleus.dataattributes.json.EntityTypesJson;
import com.github.clevernucleus.dataattributes.json.FunctionsJson;
import com.github.clevernucleus.dataattributes.json.PropertiesJson;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public final class AttributeDataManager implements SimpleSynchronousResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();
	private static final int PATH_SUFFIX_LENGTH = ".json".length();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DIRECTORY = "attributes";
	private static final Identifier ID = new Identifier(DataAttributesAPI.MODID, DIRECTORY);
	private boolean isLoaded = false;
	public final Map<Identifier, AttributeWrapper> attributes;
	public final Map<Identifier, EntityTypeAttributes> entityTypes;
	public final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> containers;
	
	public AttributeDataManager() {
		this.attributes = new HashMap<Identifier, AttributeWrapper>();
		this.entityTypes = new HashMap<Identifier, EntityTypeAttributes>();
		this.containers = new HashMap<EntityType<? extends LivingEntity>, DefaultAttributeContainer>();
	}
	
	private static Map<Identifier, Double> formatFunctions(Map<String, Double> functionsIn) {
		Map<Identifier, Double> functions = new HashMap<Identifier, Double>();
		
		for(String key : functionsIn.keySet()) {
			double value = functionsIn.get(key);
			
			functions.put(new Identifier(key), value);
		}
		
		return functions;
	}
	
	private static EntityAttribute getOrCreate(final Identifier identifier, EntityAttribute attributeIn) {
		EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
		
		if(attribute == null) {
			attribute = MutableRegistryImpl.register(Registry.ATTRIBUTE, identifier, attributeIn);
		}
		
		return attribute;
	}
	
	private void loadAttributeOverrides(ResourceManager manager) {
		Map<Identifier, AttributeOverrideJson> local = new HashMap<Identifier, AttributeOverrideJson>();
		String location = DIRECTORY + "/overrides";
		int length = location.length() + 1;
		
		for(Identifier resource : manager.findResources(location, file -> file.endsWith(".json"))) {
			String path = resource.getPath();
			Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));
			
			try (
				Resource resourceStream = manager.getResource(resource);
				InputStream inputStream = resourceStream.getInputStream();
				Reader readerStream = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			) {
				AttributeOverrideJson json = JsonHelper.deserialize(GSON, readerStream, AttributeOverrideJson.class);
				
				if(json != null) {
					AttributeOverrideJson object = local.put(identifier, json);
					
					if(object != null) throw new IllegalStateException("Duplicate data file ignored with ID " + identifier);
				} else {
					LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier, resource);
				}
				
				resourceStream.close();
			} catch(IOException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier, resource, exception);
			}
		}
		
		local.forEach((key, value) -> this.attributes.put(key, new AttributeWrapper(value)));
	}
	
	private void loadAttributeFunctions(ResourceManager manager) {
		Map<Identifier, FunctionsJson> local = new HashMap<Identifier, FunctionsJson>();
		int length = DIRECTORY.length() + 1;
		
		for(Identifier resource : manager.findResources(DIRECTORY, file -> file.endsWith("functions.json"))) {
			String path = resource.getPath();
			Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));
			
			try (
				Resource resourceStream = manager.getResource(resource);
				InputStream inputStream = resourceStream.getInputStream();
				Reader readerStream = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			) {
				FunctionsJson json = JsonHelper.deserialize(GSON, readerStream, FunctionsJson.class);
				
				if(json != null) {
					FunctionsJson object = local.put(identifier, json);
					
					if(object != null) throw new IllegalStateException("Duplicate data file ignored with ID " + identifier);
				} else {
					LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier, resource);
				}
				
				resourceStream.close();
			} catch(IOException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier, resource, exception);
			}
		}
		
		Map<String, Map<String, Double>> functions = new HashMap<String, Map<String, Double>>();
		local.values().forEach(json -> json.merge(functions));
		
		for(String key : functions.keySet()) {
			Identifier identifier = new Identifier(key);
			AttributeWrapper attributeWrapper = this.attributes.getOrDefault(identifier, new AttributeWrapper());
			attributeWrapper.putFunctions(formatFunctions(functions.get(key)));
			this.attributes.put(identifier, attributeWrapper);
		}
	}
	
	private void loadAttributeProperties(ResourceManager manager) {
		Map<Identifier, PropertiesJson> local = new HashMap<Identifier, PropertiesJson>();
		int length = DIRECTORY.length() + 1;
		
		for(Identifier resource : manager.findResources(DIRECTORY, file -> file.endsWith("properties.json"))) {
			String path = resource.getPath();
			Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));
			
			try (
				Resource resourceStream = manager.getResource(resource);
				InputStream inputStream = resourceStream.getInputStream();
				Reader readerStream = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			) {
				PropertiesJson json = JsonHelper.deserialize(GSON, readerStream, PropertiesJson.class);
				
				if(json != null) {
					PropertiesJson object = local.put(identifier, json);
					
					if(object != null) throw new IllegalStateException("Duplicate data file ignored with ID " + identifier);
				} else {
					LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier, resource);
				}
				
				resourceStream.close();
			} catch(IOException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier, resource, exception);
			}
		}
		
		Map<String, Map<String, String>> properties = new HashMap<String, Map<String, String>>();
		local.values().forEach(json -> json.merge(properties));
		
		for(String key : properties.keySet()) {
			Identifier identifier = new Identifier(key);
			AttributeWrapper attributeWrapper = this.attributes.getOrDefault(identifier, new AttributeWrapper());
			attributeWrapper.putProperties(properties.get(key));
			this.attributes.put(identifier, attributeWrapper);
		}
	}
	
	private void loadAttributeContainers(ResourceManager manager) {
		Map<Identifier, EntityTypesJson> local = new HashMap<Identifier, EntityTypesJson>();
		int length = DIRECTORY.length() + 1;
		
		for(Identifier resource : manager.findResources(DIRECTORY, file -> file.endsWith("entity_types.json"))) {
			String path = resource.getPath();
			Identifier identifier = new Identifier(resource.getNamespace(), path.substring(length, path.length() - PATH_SUFFIX_LENGTH));
			
			try (
				Resource resourceStream = manager.getResource(resource);
				InputStream inputStream = resourceStream.getInputStream();
				Reader readerStream = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			) {
				EntityTypesJson json = JsonHelper.deserialize(GSON, readerStream, EntityTypesJson.class);
				
				if(json != null) {
					EntityTypesJson object = local.put(identifier, json);
					
					if(object != null) throw new IllegalStateException("Duplicate data file ignored with ID " + identifier);
				} else {
					LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier, resource);
				}
				
				resourceStream.close();
			} catch(IOException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier, resource, exception);
			}
		}
		
		Map<String, Map<String, Double>> entityTypes = new HashMap<String, Map<String, Double>>();
		local.values().forEach(json -> json.merge(entityTypes));
		
		for(String key : entityTypes.keySet()) {
			EntityTypeAttributes entityTypeAttributes = new EntityTypeAttributes(entityTypes.get(key));
			Identifier identifier = new Identifier(key);
			this.entityTypes.put(identifier, entityTypeAttributes);
		}
	}
	
	public void clear() {
		this.attributes.clear();
		this.entityTypes.clear();
		this.containers.clear();
	}
	
	public void refresh() {
		for(Identifier identifier : Registry.ATTRIBUTE.getIds()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			((MutableEntityAttribute)attribute).clear();
		}
		
		for(Identifier identifier : this.attributes.keySet()) {
			AttributeWrapper attributeWrapper = this.attributes.get(identifier);
			attributeWrapper.override(identifier, AttributeDataManager::getOrCreate);
		}
		
		for(Identifier identifier : this.attributes.keySet()) {
			EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
			
			if(entityAttribute == null) continue;
			
			AttributeWrapper attributeWrapper = this.attributes.get(identifier);
			attributeWrapper.transfer(entityAttribute);
		}
		
		Collection<Identifier> entityTypes = Registry.ENTITY_TYPE.getIds().stream().filter(id -> DefaultAttributeRegistry.hasDefinitionFor(Registry.ENTITY_TYPE.get(id))).collect(Collectors.toSet());
		
		for(Identifier identifier : this.entityTypes.keySet()) {
			if(!entityTypes.contains(identifier)) continue;
			
			EntityTypeAttributes entityTypeAttributes = this.entityTypes.get(identifier);
			
			@SuppressWarnings("unchecked")
			EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>)Registry.ENTITY_TYPE.get(identifier);
			DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
			entityTypeAttributes.build(builder, DefaultAttributeRegistry.get(entityType));
			this.containers.put(entityType, builder.build());
		}
	}
	
	@Override
	public void reload(ResourceManager manager) {
		if(this.isLoaded) return;
		this.clear();
		this.loadAttributeOverrides(manager);
		this.loadAttributeFunctions(manager);
		this.loadAttributeProperties(manager);
		this.loadAttributeContainers(manager);
		this.isLoaded = true;
	}
	
	@Override
	public Identifier getFabricId() {
		return ID;
	}
}
