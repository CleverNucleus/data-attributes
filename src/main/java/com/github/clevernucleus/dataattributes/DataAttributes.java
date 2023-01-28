package com.github.clevernucleus.dataattributes;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import com.github.clevernucleus.dataattributes.api.DataAttributesAPI;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.api.util.Maths;
import com.github.clevernucleus.dataattributes.impl.AttributeManager;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

public class DataAttributes implements ModInitializer {
	public static final Identifier HANDSHAKE = new Identifier(DataAttributesAPI.MODID, "handshake");
	public static final Identifier RELOAD = new Identifier(DataAttributesAPI.MODID, "reload");
	public static final AttributeManager MANAGER = new AttributeManager();
	protected static String version = "";
	protected static byte[] semVer;
	private static final byte VER_SIZE = 3;
	
	private static void loginQueryStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
		PacketByteBuf buf = PacketByteBufs.create();
		final byte[] bytes = DataAttributes.MANAGER.getCurrentData();
		buf.writeByteArray(bytes);
		sender.sendPacket(HANDSHAKE, buf);
	}
	
	private static void loginQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, LoginSynchronizer synchronizer, PacketSender responseSender) {
		// Does doing nothing here make us compatible with Velocity plugin?
	}
	
	private static void refreshAttributes(final Entity entity) {
		if(!(entity instanceof LivingEntity)) return;
		((MutableAttributeContainer)((LivingEntity)entity).getAttributes()).refresh();
	}
	
	private static void healthModified(final EntityAttribute attribute, final @Nullable LivingEntity livingEntity, final EntityAttributeModifier modifier, final double prevValue, final boolean isWasAdded) {
		if(livingEntity == null) return;
		if(livingEntity.world.isClient) return;
		if(attribute != EntityAttributes.GENERIC_MAX_HEALTH) return;
		
		float c0 = livingEntity.getHealth();
		float c1 = c0 * livingEntity.getMaxHealth() / (float)prevValue;
		
		livingEntity.setHealth(c1);
	}
	
	@Override
	public void onInitialize() {
		version = FabricLoader.getInstance().getModContainer(DataAttributesAPI.MODID).get().getMetadata().getVersion().getFriendlyString();
		String[] versionArray = Arrays.copyOf(version.split("\\."), VER_SIZE);
		semVer = new byte[Math.max(versionArray.length, VER_SIZE)];
		
		for(int i = 0; i < semVer.length; i++) {
			semVer[i] = (byte)Maths.parse(versionArray[i]);
		}
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER);
		ServerLoginConnectionEvents.QUERY_START.register(DataAttributes::loginQueryStart);
		ServerLoginNetworking.registerGlobalReceiver(HANDSHAKE, DataAttributes::loginQueryResponse);
		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((oldEntity, newEntity, from, to) -> refreshAttributes(newEntity));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, from, to) -> refreshAttributes(player));
		EntityAttributeModifiedEvents.MODIFIED.register(DataAttributes::healthModified);
	}
}
