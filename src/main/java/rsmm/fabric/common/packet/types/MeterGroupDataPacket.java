package rsmm.fabric.common.packet.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.packet.AbstractRSMMPacket;
import rsmm.fabric.server.MultimeterServer;

public class MeterGroupDataPacket extends AbstractRSMMPacket {
	
	private String name;
	private CompoundTag meterGroupData;
	
	public MeterGroupDataPacket() {
		
	}
	
	public MeterGroupDataPacket(MeterGroup meterGroup) {
		this.name = meterGroup.getName();
		this.meterGroupData = meterGroup.toTag();
	}
	
	@Override
	public void encode(CompoundTag data) {
		data.putString("name", name);
		data.put("data", meterGroupData);
	}
	
	@Override
	public void decode(CompoundTag data) {
		name = data.getString("name");
		meterGroupData = data.getCompound("data");
	}
	
	@Override
	public void execute(MultimeterServer server, ServerPlayerEntity player) {
		server.getMultimeter().meterGroupDataReceived(name, meterGroupData, player);
	}
	
	@Override
	public void execute(MultimeterClient client) {
		client.meterGroupDataReceived(name, meterGroupData);
	}
}
