
package com.water.animall.Creeper.entity;

import com.water.animall.Creeper.goal.TameSwellGoal;
import com.water.animall.init.AnimallModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.protocol.Packet;

import javax.annotation.Nullable;
import java.util.Collection;


@Mod.EventBusSubscriber
public class SssssEntity extends Monster implements PowerableMob {
	private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
	private int oldSwell;
	private int swell;
	private int maxSwell = 30;
	private int explosionRadius = 3;
	private int droppedSkulls;

	//追加分
	private int moveTicks = 0;
	private int totalMoveTicks = 100; // 5秒分のタイマー (20 ticks * 5)

	@SubscribeEvent
	public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
		event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AnimallModEntities.SSSSS.get(), 20, 4, 4));
	}

	public SssssEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(AnimallModEntities.SSSSS.get(), world);
	}

	public SssssEntity(EntityType<SssssEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new TameSwellGoal(this));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));

	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}


	public static void init() {
		SpawnPlacements.register(AnimallModEntities.SSSSS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos,
				 random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH,100000000);

	}

	public int getMaxFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
	}

	public boolean causeFallDamage(float p_149687_, float p_149688_, DamageSource p_149689_) {
		boolean flag = super.causeFallDamage(p_149687_, p_149688_, p_149689_);
		this.swell += (int) (p_149687_ * 1.5F);
		if (this.swell > this.maxSwell - 5) {
			this.swell = this.maxSwell - 5;
		}

		return flag;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SWELL_DIR, -1);
		this.entityData.define(DATA_IS_POWERED, false);
		this.entityData.define(DATA_IS_IGNITED, false);
	}

	public void addAdditionalSaveData(CompoundTag p_32304_) {
		super.addAdditionalSaveData(p_32304_);
		if (this.entityData.get(DATA_IS_POWERED)) {
			p_32304_.putBoolean("powered", true);
		}

		p_32304_.putShort("Fuse", (short) this.maxSwell);
		p_32304_.putByte("ExplosionRadius", (byte) this.explosionRadius);
		p_32304_.putBoolean("ignited", this.isIgnited());
	}

	public void readAdditionalSaveData(CompoundTag p_32296_) {
		super.readAdditionalSaveData(p_32296_);
		this.entityData.set(DATA_IS_POWERED, p_32296_.getBoolean("powered"));
		if (p_32296_.contains("Fuse", 99)) {
			this.maxSwell = p_32296_.getShort("Fuse");
		}

		if (p_32296_.contains("ExplosionRadius", 99)) {
			this.explosionRadius = p_32296_.getByte("ExplosionRadius");
		}

		if (p_32296_.getBoolean("ignited")) {
			this.ignite();
		}

	}

	public void tick() {
		if (this.isAlive()) {
			this.oldSwell = this.swell;
			if (this.isIgnited()) {
				this.setSwellDir(1);
			}

			int i = this.getSwellDir();
			if (i > 0 && this.swell == 0) {

			}

			this.swell += i;
			if (this.swell < 0) {
				this.swell = 0;
			}

			if (this.swell >= this.maxSwell) {
				this.swell = this.maxSwell;
			}
		}

		super.tick();
	}

	public void setTarget(@Nullable LivingEntity p_149691_) {
		if (!(p_149691_ instanceof Goat)) {
			super.setTarget(p_149691_);
		}
	}

	protected SoundEvent getHurtSound(DamageSource p_32309_) {
		return SoundEvents.CREEPER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

	protected void dropCustomDeathLoot(DamageSource p_32292_, int p_32293_, boolean p_32294_) {
		super.dropCustomDeathLoot(p_32292_, p_32293_, p_32294_);
		Entity entity = p_32292_.getEntity();
		if (entity != this && entity instanceof Creeper) {
			Creeper creeper = (Creeper) entity;
			if (creeper.canDropMobsSkull()) {
				creeper.increaseDroppedSkulls();
				this.spawnAtLocation(Items.CREEPER_HEAD);
			}
		}

	}

	public boolean doHurtTarget(Entity p_32281_) {
		return true;
	}

	public boolean isPowered() {
		return this.entityData.get(DATA_IS_POWERED);
	}

	public int getSwellDir() {
		return this.entityData.get(DATA_SWELL_DIR);
	}

	public void setSwellDir(int p_32284_) {
		this.entityData.set(DATA_SWELL_DIR, p_32284_);
	}

	public void thunderHit(ServerLevel p_32286_, LightningBolt p_32287_) {
		super.thunderHit(p_32286_, p_32287_);
		this.entityData.set(DATA_IS_POWERED, true);
	}

	//追加分
	public static boolean flag = false;

	public InteractionResult mobInteract(Player p_32301_, InteractionHand p_32302_) {
		ItemStack itemstack = p_32301_.getItemInHand(p_32302_);

//追加分
		if (itemstack.isEmpty()) {

			Vec3 lookVector = this.getLookAngle();
			// ゾンビを5秒間の移動を開始
			startCustomMobMovement(lookVector, 0.4);
			flag = true;
		}

		if (itemstack.is(Items.FLINT_AND_STEEL)) {
			this.level.playSound(p_32301_, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			if (!this.level.isClientSide) {
				this.ignite();
				itemstack.hurtAndBreak(1, p_32301_, (p_32290_) -> {
					p_32290_.broadcastBreakEvent(p_32302_);
				});
			}

			return InteractionResult.sidedSuccess(this.level.isClientSide);
		} else {
			return super.mobInteract(p_32301_, p_32302_);
		}
	}
//追加分
	// ゾンビのようにプレイヤーの右クリックに反応する
	// 移動を開始するタイマーをセット
	private void startCustomMobMovement(Vec3 direction, double speed) {
		moveTicks = 0;
		totalMoveTicks = 100;

		// 移動速度と方向を設定
		setDeltaMovement(direction.x * speed, getDeltaMovement().y, direction.z * speed);
	}

	private static final double FOLLOW_DISTANCE = 3.0; // 一マスの距離

	// 移動のタイマーを処理
	@Override
	public void aiStep() {
		super.aiStep();
		//追加bん
		Player player = level.getNearestPlayer(this, 100.0);

		if (player != null) {
			if (flag == false) {
				Vec3 playerPos = player.position();
				double distance = player.distanceToSqr(this);

				double distancee = 10.0; // プレイヤーとの距離の閾値
				double teleportDistance = 5.0; // テレポートする距離

				if (this.distanceToSqr(player) > distancee * distancee) {

						this.setPos(player.position().x, player.position().y, player.position().z);

				} else if (distance > 3.0) {
					// プレイヤーの位置に向かって移動
					this.getNavigation().moveTo(player, 1.0);
				}
			}
		}
		if (flag == true) {
			if (moveTicks < totalMoveTicks) {
				// タイマーが経過していなければ、モブを移動させる
				Vec3 moveDirection = new Vec3(getDeltaMovement().x, 0.0, getDeltaMovement().z).normalize();
				move(MoverType.SELF, new Vec3(moveDirection.x * 0.05, 0.0, moveDirection.z * 0.05));
				moveTicks++;
				if (moveTicks == 99) {
					explodeCreeper();
				}
				if (moveTicks == 40) {
					this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
					this.gameEvent(GameEvent.PRIME_FUSE);
				}
			}
		} else {
			System.out.println("FALSE");

		}

	}
	private boolean isBlockPassable(BlockPos pos) {
		return level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
	}

	// 指定された座標の障害物が高さ1ブロック以下かどうかを確認するメソッド
	private boolean hasLowObstacle(BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		return pos.getY() - 1 <= pos.getY(); // 高さが1ブロック以下かどうかを確認
	}

	// ジャンプ処理
	private void jump() {
		if (!jumping) {
			setDeltaMovement(getDeltaMovement().add(0.0, 0.5, 0.0)); // 上方向にジャンプ
			jumping = true;
		}
	}
	private double followDistance = 5.0; // 追従する距離
	private double teleportDistance = 10.0; // テレポートする距離



	public void teleportTo(double x, double y, double z) {
		// カスタムモブを指定の座標にテレポート
		this.absMoveTo(x, y, z);
	}

	//爆発作動用メソッド
	public void explodeCreeper() {
		if (!this.level.isClientSide) {
			Explosion.BlockInteraction explosion$blockinteraction = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
			float f = this.isPowered() ? 2.0F : 1.0F;
			this.dead = true;
			this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, explosion$blockinteraction);
			this.discard();
			this.spawnLingeringCloud();
			flag = false;
		}
	}

	private void spawnLingeringCloud() {
		Collection<MobEffectInstance> collection = this.getActiveEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
			areaeffectcloud.setRadius(2.5F);
			areaeffectcloud.setRadiusOnUse(-0.5F);
			areaeffectcloud.setWaitTime(10);
			areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
			areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());

			for(MobEffectInstance mobeffectinstance : collection) {
				areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
			}

			this.level.addFreshEntity(areaeffectcloud);
		}
	}

	public boolean isIgnited() {
		return this.entityData.get(DATA_IS_IGNITED);
	}

	public void ignite() {
		this.entityData.set(DATA_IS_IGNITED, true);
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(SssssEntity.class);
	}
}

