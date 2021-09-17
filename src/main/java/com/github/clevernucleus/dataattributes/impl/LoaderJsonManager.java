package com.github.clevernucleus.dataattributes.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.clevernucleus.dataattributes.DataAttributes;
import com.github.clevernucleus.dataattributes.api.API;
import com.github.clevernucleus.dataattributes.impl.attribute.AttributeObject;
import com.github.clevernucleus.dataattributes.impl.attribute.EntityTypeObject;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableAttribute;
import com.github.clevernucleus.dataattributes.impl.json.AttributeFunctionJson;
import com.github.clevernucleus.dataattributes.impl.json.AttributeJson;
import com.github.clevernucleus.dataattributes.impl.json.EntityTypesJson;
import com.github.clevernucleus.dataattributes.impl.json.FunctionsJson;
import com.github.clevernucleus.dataattributes.impl.json.PropertiesJson;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public final class LoaderJsonManager implements SimpleSynchronousResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();
	private static final int PATH_SUFFIX_LENGTH = ".json".length();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DIRECTORY = "attributes";
	private static final Identifier ID = new Identifier(API.MODID, DIRECTORY);
	
	public final Map<Identifier, AttributeJson> overrides;
	public final Map<Identifier, AttributeObject> attributes;
	public final Map<Identifier, EntityTypeObject> entityTypes;
	public final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> containers;
	
	public LoaderJsonManager() {
		this.overrides = new HashMap<Identifier, AttributeJson>();
		this.attributes = new HashMap<Identifier, AttributeObject>();
		this.entityTypes = new HashMap<Identifier, EntityTypeObject>();
		this.containers = new HashMap<EntityType<? extends LivingEntity>, DefaultAttributeContainer>();
	}
	
	private static EntityAttribute getOrCreate(final Identifier identifier, EntityAttribute attributeIn) {
		EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
		
		if(attribute == null) {
			attribute = Registry.register(Registry.ATTRIBUTE, identifier, attributeIn);
		}
		
		return attribute;
	}
	
	private static boolean checkForRecursion(final Identifier identifier, Collection<Identifier> recursives, Multimap<Identifier, AttributeFunctionJson> remove) {
		if(recursives.contains(identifier)) return true;
		
		recursives.add(identifier);
		
		AttributeObject attributeObject = DataAttributes.MANAGER.attributes.get(identifier);
		
		if(attributeObject == null) return false;
		
		for(AttributeFunctionJson function : attributeObject.functions()) {
			Identifier subIdentifier = function.attribute();
			
			if(checkForRecursion(subIdentifier, recursives, remove)) {
				remove.put(identifier, function);
			}
		}
		
		return false;
	}
	
	public void clear() {
		this.overrides.clear();
		this.attributes.clear();
		this.entityTypes.clear();
		this.containers.clear();
	}
	
	public void refresh() {
		for(Identifier identifier : Registry.ATTRIBUTE.getIds()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			((IMutableAttribute)attribute).reset();
		}
		
		for(Identifier identifier : this.overrides.keySet()) {
			AttributeJson json = this.overrides.get(identifier);
			EntityAttribute attribute = getOrCreate(identifier, json.newAttribute());
			json.impart((IMutableAttribute)attribute);
		}
		
		for(Identifier identifier : this.attributes.keySet()) {
			EntityAttribute attribute = Registry.ATTRIBUTE.get(identifier);
			
			if(attribute == null) continue;
			
			AttributeObject object = this.attributes.get(identifier);
			object.impart((IMutableAttribute)attribute);
		}
		
		Collection<Identifier> entityTypes = Registry.ENTITY_TYPE.getIds().stream().filter(id -> DefaultAttributeRegistry.hasDefinitionFor(Registry.ENTITY_TYPE.get(id))).collect(Collectors.toSet());
		
		for(Identifier identifier : this.entityTypes.keySet()) {
			if(!entityTypes.contains(identifier)) continue;
			
			EntityTypeObject entityTypeObject = this.entityTypes.get(identifier);
			
			@SuppressWarnings("unchecked")
			EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>)Registry.ENTITY_TYPE.get(identifier);
			DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
			
			entityTypeObject.buildContainer(builder, DefaultAttributeRegistry.get(entityType));
			
			this.containers.put(entityType, builder.build());
		}
	}
	
	private void loadOverrides(ResourceManager manager) {
		Map<Identifier, AttributeJson> local = new HashMap<Identifier, AttributeJson>();
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
				AttributeJson json = JsonHelper.deserialize(GSON, readerStream, AttributeJson.class);
				
				if(json != null) {
					AttributeJson object = local.put(identifier, json);
					
					if(object != null) throw new IllegalStateException("Duplicate data file ignored with ID " + identifier);
				} else {
					LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier, resource);
				}
				
				resourceStream.close();
			} catch(IOException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier, resource, exception);
			}
		}
		
		this.overrides.putAll(local);
	}
	
	private void loadFunctions(ResourceManager manager) {
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
		
		Multimap<Identifier, AttributeFunctionJson> functions = HashMultimap.create();
		
		for(FunctionsJson functionsJson : local.values()) {
			functionsJson.merge(functions);
		}
		
		for(Identifier identifier : functions.keySet()) {
			AttributeObject attributeObject = new AttributeObject();
			attributeObject.appendFunctions(functions.get(identifier));
			this.attributes.put(identifier, attributeObject);
		}
		
		for(Identifier attribute : this.attributes.keySet()) {
			Collection<Identifier> recursives = new HashSet<Identifier>();
			Multimap<Identifier, AttributeFunctionJson> remove = HashMultimap.create();
			
			checkForRecursion(attribute, recursives, remove);
			
			for(Identifier identifier : remove.keySet()) {
				for(AttributeFunctionJson function : remove.get(identifier)) {
					this.attributes.get(identifier).functions().remove(function);
				}
			}
		}
	}
	
	private void loadProperties(ResourceManager manager) {
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
		
		Map<Identifier, Map<String, String>> properties = new HashMap<Identifier, Map<String, String>>();
		
		for(PropertiesJson propertiesJson : local.values()) {
			propertiesJson.merge(properties);
		}
		
		for(Identifier identifier : properties.keySet()) {
			AttributeObject attributeObject = this.attributes.getOrDefault(identifier, new AttributeObject());
			attributeObject.appendProperties(properties.get(identifier));
			this.attributes.put(identifier, attributeObject);
		}
	}
	
	private void loadContainers(ResourceManager manager) {
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
		
		Map<Identifier, Map<Identifier, Double>> entityTypes = new HashMap<Identifier, Map<Identifier, Double>>();
		
		for(EntityTypesJson entityTypesJson : local.values()) {
			entityTypesJson.merge(entityTypes);
		}
		
		for(Identifier identifier : entityTypes.keySet()) {
			EntityTypeObject entityTypeObject = new EntityTypeObject(entityTypes.get(identifier));
			this.entityTypes.put(identifier, entityTypeObject);
		}
	}
	
	@Override
	public void reload(ResourceManager manager) {
		this.clear();
		this.loadOverrides(manager);
		this.loadFunctions(manager);
		this.loadProperties(manager);
		this.loadContainers(manager);
	}
	
	@Override
	public Identifier getFabricId() {
		return ID;
	}
}
