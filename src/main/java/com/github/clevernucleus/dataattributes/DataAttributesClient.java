package com.github.clevernucleus.dataattributes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.github.clevernucleus.dataattributes.api.event.client.ClientSyncedEvent;
import com.github.clevernucleus.dataattributes.impl.AttributeWrapper;
import com.github.clevernucleus.dataattributes.impl.EntityTypeAttributes;
import com.github.clevernucleus.dataattributes.impl.MutableRegistryImpl;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class DataAttributesClient implements ClientModInitializer {
	private static CompletableFuture<PacketByteBuf> loginQueryReceived(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
		NbtCompound tag = buf.readNbt();
		
		client.execute(() -> {
			DataAttributes.MANAGER.clear();
			
			if(tag.contains("Attributes")) {
				NbtList attributes = tag.getList("Attributes", NbtType.COMPOUND);
				
				for(int i = 0; i < attributes.size(); i++) {
					NbtCompound entry = attributes.getCompound(i);
					Identifier identifier = new Identifier(entry.getString("Identifier"));
					AttributeWrapper attributeWrapper = new AttributeWrapper();
					attributeWrapper.readFromNbt(entry);
					
					DataAttributes.MANAGER.attributes.put(identifier, attributeWrapper);
				}
			}
			
			if(tag.contains("EntityTypes")) {
				NbtList attributes = tag.getList("EntityTypes", NbtType.COMPOUND);
				
				for(int i = 0; i < attributes.size(); i++) {
					NbtCompound entry = attributes.getCompound(i);
					Identifier identifier = new Identifier(entry.getString("Identifier"));
					EntityTypeAttributes entityTypeAttributes = new EntityTypeAttributes();
					entityTypeAttributes.readFromNbt(entry);
					
					DataAttributes.MANAGER.entityTypes.put(identifier, entityTypeAttributes);
				}
			}
			
			DataAttributes.MANAGER.refresh();
			ClientSyncedEvent.EVENT.invoker().onCompleted(client);
		});
		
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeString(DataAttributes.VERSION);
		
		return CompletableFuture.completedFuture(bufOut);
	}
	
	private static void removeAttributes(ClientPlayNetworkHandler handler, MinecraftClient client) {
		client.execute(() -> MutableRegistryImpl.unregister(Registry.ATTRIBUTE));
	}
	
	@Override
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(DataAttributes.HANDSHAKE, DataAttributesClient::loginQueryReceived);
		ClientPlayConnectionEvents.DISCONNECT.register(DataAttributesClient::removeAttributes);
	}
}
