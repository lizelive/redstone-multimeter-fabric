package rsmm.fabric.client;

import net.minecraft.nbt.CompoundTag;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;

public class ClientMeterGroup extends MeterGroup {
	
	private final MultimeterClient client;
	private final ClientLogManager logManager;
	
	public ClientMeterGroup(MultimeterClient client, String name) {
		super(name);
		
		this.client = client;
		this.logManager = new ClientLogManager(this);
	}
	
	public MultimeterClient getMultimeterClient() {
		return client;
	}
	
	public ClientLogManager getLogManager() {
		return logManager;
	}
	
	public void updateMeters(CompoundTag meterChanges) {
		int meterCount = getMeterCount();
		
		for (int index = 0; index < meterCount; index++) {
			String key = String.valueOf(index);
			
			if (meterChanges.contains(key)) {
				Meter meter = getMeter(index);
				CompoundTag meterData = meterChanges.getCompound(key);
				
				meter.fromTag(meterData);
			}
		}
	}
}
