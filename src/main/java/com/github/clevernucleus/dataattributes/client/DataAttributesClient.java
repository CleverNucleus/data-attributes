package com.github.clevernucleus.dataattributes.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.github.clevernucleus.dataattributes.DataAttributes;
import com.github.clevernucleus.dataattributes.impl.attribute.AttributeObject;
import com.github.clevernucleus.dataattributes.impl.attribute.EntityTypeObject;
import com.github.clevernucleus.dataattributes.impl.json.AttributeJson;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class DataAttributesClient implements ClientModInitializer {
	private static CompletableFuture<PacketByteBuf> loginQueryReceived(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
		NbtCompound tag = buf.readNbt();
		
		client.execute(() -> {
			DataAttributes.MANAGER.clear();
			
			if(tag.contains("Overrides")) {
				NbtList overrides = tag.getList("Overrides", NbtType.COMPOUND);
				
				for(int i = 0; i < overrides.size(); i++) {
					NbtCompound entry = overrides.getCompound(i);
					Identifier identifier = new Identifier(entry.getString("Identifier"));
					AttributeJson attributeJson = AttributeJson.read(entry);
					DataAttributes.MANAGER.overrides.put(identifier, attributeJson);
				}
			}
			
			if(tag.contains("Attributes")) {
				NbtList attributes = tag.getList("Attributes", NbtType.COMPOUND);
				
				for(int i = 0; i < attributes.size(); i++) {
					NbtCompound entry = attributes.getCompound(i);
					Identifier identifier = new Identifier(entry.getString("Identifier"));
					AttributeObject attributeObject = AttributeObject.read(entry);
					DataAttributes.MANAGER.attributes.put(identifier, attributeObject);
				}
			}
			
			if(tag.contains("EntityTypes")) {
				NbtList entityTypes = tag.getList("EntityTypes", NbtType.COMPOUND);
				
				for(int i = 0; i < entityTypes.size(); i++) {
					NbtCompound entry = entityTypes.getCompound(i);
					Identifier identifier = new Identifier(entry.getString("Identifier"));
					EntityTypeObject attributeObject = EntityTypeObject.read(entry);
					DataAttributes.MANAGER.entityTypes.put(identifier, attributeObject);
				}
			}
			
			DataAttributes.MANAGER.refresh();
		});
		
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeString(DataAttributes.VERSION);
		
		return CompletableFuture.completedFuture(bufOut);
	}
	
	@Override
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(DataAttributes.SYNC, DataAttributesClient::loginQueryReceived);
	}
}
