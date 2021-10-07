package rsmm.fabric.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.common.MeterProperties;
import rsmm.fabric.common.WorldPos;
import rsmm.fabric.common.event.EventType;
import rsmm.fabric.common.network.packets.MeterUpdatesPacket;

public class ServerMeterGroup extends MeterGroup {
	
	private final Multimeter multimeter;
	private final Set<ServerPlayerEntity> subscribers;
	private final ServerLogManager logManager;
	
	private final List<Long> removedMeters;
	private final Map<Long, MeterProperties> meterUpdates;
	
	public ServerMeterGroup(Multimeter multimeter, String name) {
		super(name);
		
		this.multimeter = multimeter;
		this.subscribers = new HashSet<>();
		this.logManager = new ServerLogManager(this);
		
		this.removedMeters = new ArrayList<>();
		this.meterUpdates = new LinkedHashMap<>();
	}
	
	@Override
	public void clear() {
		super.clear();
		
		removedMeters.clear();
		meterUpdates.clear();
	}
	
	@Override
	protected boolean moveMeter(Meter meter, WorldPos newPos) {
		if (hasMeterAt(newPos)) {
			return false;
		}
		
		World world = multimeter.getMultimeterServer().getWorldOf(newPos);
		
		if (world == null) {
			return false;
		}
		
		return super.moveMeter(meter, newPos);
	}
	
	@Override
	protected void meterAdded(Meter meter) {
		meterUpdates.putIfAbsent(meter.getId(), meter.getProperties());
	}
	
	@Override
	protected void meterRemoved(Meter meter) {
		removedMeters.add(meter.getId());
		meterUpdates.remove(meter.getId());
	}
	
	@Override
	protected void meterUpdated(Meter meter) {
		meterUpdates.putIfAbsent(meter.getId(), meter.getProperties());
	}
	
	@Override
	public ServerLogManager getLogManager() {
		return logManager;
	}
	
	public Multimeter getMultimeter() {
		return multimeter;
	}
	
	public void addMeter(MeterProperties properties) {
		addMeter(new Meter(properties));
	}
	
	public void removeMeter(long id) {
		Meter meter = getMeter(id);
		
		if (meter != null) {
			removeMeter(meter);
		}
	}
	
	public void updateMeter(long id, MeterProperties newProperties) {
		Meter meter = getMeter(id);
		
		if (meter != null) {
			updateMeter(meter, newProperties);
		}
	}
	
	public boolean tryMoveMeter(long id, WorldPos newPos, boolean byPiston) {
		if (!hasMeter(id)) {
			return false;
		}
		
		Meter meter = getMeter(id);
		
		if (byPiston && !meter.isMovable()) {
			return false;
		}
		
		return moveMeter(meter, newPos);
	}
	
	public boolean hasSubscribers() {
		return !subscribers.isEmpty();
	}
	
	public Set<ServerPlayerEntity> getSubscribers() {
		return Collections.unmodifiableSet(subscribers);
	}
	
	public void addSubscriber(ServerPlayerEntity player) {
		subscribers.add(player);
	}
	
	public void removeSubscriber(ServerPlayerEntity player) {
		subscribers.remove(player);
	}
	
	public void flushUpdates() {
		if (removedMeters.isEmpty() && meterUpdates.isEmpty()) {
			return;
		}
		
		MeterUpdatesPacket packet = new MeterUpdatesPacket(removedMeters, meterUpdates);
		multimeter.getMultimeterServer().getPacketHandler().sendPacketToPlayers(packet, subscribers);
		
		removedMeters.clear();
		meterUpdates.clear();
	}
	
	public void tryLogEvent(WorldPos pos, EventType type, int metaData, BiPredicate<ServerMeterGroup, Meter> meterPredicate) {
		if (hasMeterAt(pos)) {
			Meter meter = getMeterAt(pos);
			
			if (meterPredicate.test(this, meter)) {
				logManager.logEvent(meter, type, metaData);
			}
		}
	}
}
