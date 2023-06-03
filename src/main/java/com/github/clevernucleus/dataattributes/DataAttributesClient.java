package com.github.clevernucleus.dataattributes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class DataAttributesClient implements ClientModInitializer {
	private static CompletableFuture<PacketByteBuf> loginQueryReceived(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
		onPacketReceived(client, buf);
		return CompletableFuture.completedFuture(PacketByteBufs.empty());
	}
	
	private static void updateReceived(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		onPacketReceived(client, buf);
	}
	
	private static void onPacketReceived(MinecraftClient client, PacketByteBuf buf) {
		NbtCompound tag = buf.readNbt();
		
		client.execute(() -> {
			if(tag != null) {
				DataAttributes.MANAGER.fromNbt(tag);
				DataAttributes.MANAGER.apply();
			}
		});
	}

	@Override
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(DataAttributes.HANDSHAKE, DataAttributesClient::loginQueryReceived);
		ClientPlayNetworking.registerGlobalReceiver(DataAttributes.RELOAD, DataAttributesClient::updateReceived);
	}
}
