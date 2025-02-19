package redstone.multimeter.mixin.common;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.WorldTickScheduler;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.interfaces.mixin.IServerWorld;
import redstone.multimeter.interfaces.mixin.IWorldTickScheduler;
import redstone.multimeter.server.MultimeterServer;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld {
	
	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;
	@Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;
	
	private OrderedTick<?> scheduledTickRSMM;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void setTickConsumers(CallbackInfo ci) {
		((IWorldTickScheduler)blockTickScheduler).setTickConsumerRSMM(scheduledTick -> {
			this.scheduledTickRSMM = scheduledTick;
		});
		((IWorldTickScheduler)fluidTickScheduler).setTickConsumerRSMM(scheduledTick -> {
			this.scheduledTickRSMM = scheduledTick;
		});
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=world border"
			)
	)
	private void startTickTaskWorldBorder(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WORLD_BORDER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=weather"
			)
	)
	private void swapTickTaskWeather(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.WEATHER);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickPending"
			)
	)
	private void swapTickTaskScheduledTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.SCHEDULED_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=blockTicks"
			)
	)
	private void startTickTaskBlockTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.BLOCK_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=fluidTicks"
			)
	)
	private void swapTickTaskFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.FLUID_TICKS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V"
			)
	)
	private void endTickTaskFluidTicks(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=raid"
			)
	)
	private void swapTickTaskRaids(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.RAIDS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=chunkSource"
			)
	)
	private void swapTickTaskChunkSource(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.CHUNK_SOURCE);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=blockEvents"
			)
	)
	private void swapTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.BLOCK_EVENTS);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/server/world/ServerWorld;processSyncedBlockEvents()V"
			)
	)
	private void endTickTaskBlockEvents(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=entities"
			)
	)
	private void startTickTaskEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.ENTITIES);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"
			)
	)
	private void endTickTaskEntities(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickTime",
			at = @At(
					value = "INVOKE",
					shift = Shift.AFTER,
					target = "Lnet/minecraft/world/level/ServerWorldProperties;setTime(J)V"
			)
	)
	private void onTickTime(CallbackInfo ci) {
		getMultimeterServer().onOverworldTickTime();
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.CUSTOM_MOB_SPAWNING);
	}
	
	@Inject(
			method = "tickSpawners",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskCustomMobSpawning(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		startTickTaskRSMM(TickTask.WAKE_SLEEPING_PLAYERS);
	}
	
	@Inject(
			method = "wakeSleepingPlayers",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskWakeSleepingPlayers(CallbackInfo ci) {
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.TICK_CHUNK);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
					args = "ldc=thunder"
			)
	)
	private void startTickTaskThunder(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		startTickTaskRSMM(TickTask.THUNDER);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=iceandsnow"
			)
	)
	private void swapTickTaskPrecipitation(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.PRECIPITATION);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "INVOKE_STRING",
					target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
					args = "ldc=tickBlocks"
			)
	)
	private void swapTickTaskRandomTicks(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		swapTickTaskRSMM(TickTask.RANDOM_TICKS);
	}
	
	@Inject(
			method = "tickChunk",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskRandomTicksAndTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		endTickTaskRSMM();
		endTickTaskRSMM();
	}
	
	@Inject(
			method = "tickFluid",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onTickFluid(BlockPos pos, Fluid fluid, CallbackInfo ci) {
		logCurrentScheduledTickRSMM();
	}
	
	@Inject(
			method = "tickBlock",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;scheduledTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
			)
	)
	private void onTickBlock(BlockPos pos, Block block, CallbackInfo ci) {
		logCurrentScheduledTickRSMM();
	}
	
	@Inject(
			method = "tickEntity",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/Entity;tick()V"
			)
	)
	private void onTickEntity(Entity entity, CallbackInfo ci) {
		getMultimeter().logEntityTick((World)(Object)this, entity);
	}
	
	@Inject(
			method = "processBlockEvent",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;onSyncedBlockEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"
			)
	)
	private void onProcessBlockEvent(BlockEvent blockEvent, CallbackInfoReturnable<Boolean> cir) {
		getMultimeter().logBlockEvent((World)(Object)this, blockEvent);
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return ((IMinecraftServer)server).getMultimeterServer();
	}
	
	private void logCurrentScheduledTickRSMM() {
		if (scheduledTickRSMM != null) {
			getMultimeter().logScheduledTick((World)(Object)this, scheduledTickRSMM);
			scheduledTickRSMM = null;
		}
	}
}
