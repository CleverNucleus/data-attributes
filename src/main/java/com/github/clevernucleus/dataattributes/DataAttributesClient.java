package com.github.clevernucleus.dataattributes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class DataAttributesClient implements ClientModInitializer {
	private static CompletableFuture<PacketByteBuf> loginQueryReceived(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
		NbtCompound tag = buf.readNbt();
		
		client.execute(() -> {
			if(tag != null) {
				DataAttributes.MANAGER.fromNbt(tag);
				DataAttributes.MANAGER.apply();
			}
		});
		
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeByteArray(DataAttributes.majorVersion);
		
		return CompletableFuture.completedFuture(bufOut);
	}
	
	private static void updateReceived(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		NbtCompound tag = buf.readNbt();
		final int updateFlag = buf.readInt();
		
		client.execute(() -> {
			if(tag != null) {
				DataAttributes.MANAGER.fromNbt(tag);
				DataAttributes.MANAGER.apply();
			}
			
			ClientWorld world = client.world;
			
			if(world != null) {
				ClientWorld.Properties properties = world.getLevelProperties();
				((MutableIntFlag)properties).setUpdateFlag(updateFlag);
			}
		});
	}
	
	@Override
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(DataAttributes.HANDSHAKE, DataAttributesClient::loginQueryReceived);
		ClientPlayNetworking.registerGlobalReceiver(DataAttributes.RELOAD, DataAttributesClient::updateReceived);
	}
}
