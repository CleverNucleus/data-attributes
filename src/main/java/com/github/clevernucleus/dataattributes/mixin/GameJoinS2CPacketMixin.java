package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

@Mixin(GameJoinS2CPacket.class)
abstract class GameJoinS2CPacketMixin implements MutableIntFlag {
	
	@Unique
	private int updateFlag;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void data_init(PacketByteBuf buf, CallbackInfo ci) {
		this.updateFlag = buf.readInt();
	}
	
	@Inject(method = "write", at = @At("TAIL"))
	private void data_write(PacketByteBuf buf, CallbackInfo ci) {
		buf.writeInt(this.updateFlag);
	}
	
	@Override
	public void setUpdateFlag(int flag) {
		this.updateFlag = flag;
	}
	
	@Override
	public int getUpdateFlag() {
		return this.updateFlag;
	}
}
