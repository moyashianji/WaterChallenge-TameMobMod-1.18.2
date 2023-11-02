package com.water.animall.Ghast.entity;

import com.water.animall.Blaze.entity.TameBlazeEntity;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Slime.entity.TameSlimeEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.animall.init.AnimallModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
public class TameGhastEntity extends FlyingMob implements Enemy {


    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AnimallModEntities.GHAST.get(), 20, 4, 4));
    }

    public TameGhastEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(AnimallModEntities.GHAST.get(), world);
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void init() {
        SpawnPlacements.register(AnimallModEntities.GHAST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos,
                 random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
    }

    //胃かこぴぺ
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(TameGhastEntity.class, EntityDataSerializers.BOOLEAN);
    private int explosionPower = 1;

    public TameGhastEntity(EntityType<? extends TameGhastEntity> p_32725_, Level p_32726_) {
        super(p_32725_, p_32726_);
        this.xpReward = 5;
        this.setInvulnerable(true);

        this.moveControl = new TameGhastEntity.TameGhastEntityMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(5, new TameGhastEntity.RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(7, new TameGhastEntity.TameGhastEntityLookGoal(this));
        this.goalSelector.addGoal(7, new TameGhastEntity.TameGhastEntityShootFireballGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, false,
                (entity) -> !(entity instanceof SssssEntity)
                        && !(entity instanceof TameCow)
                        && !(entity instanceof TameSlimeEntity)
                        && !(entity instanceof TameEnderMan)
                        && !(entity instanceof TameGhastEntity)
                        &&!(entity instanceof TameIronGolem)
                        && !(entity instanceof TamePhantomEntity)
                        &&!(entity instanceof TameSpiderEntity)
                        && !(entity instanceof TameBlazeEntity)));

    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean p_32759_) {
        this.entityData.set(DATA_IS_CHARGING, p_32759_);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public boolean hurt(DamageSource p_32730_, float p_32731_) {
        if (this.isInvulnerableTo(p_32730_)) {
            return false;
        } else if (p_32730_.getDirectEntity() instanceof LargeFireball && !(p_32730_.getEntity() instanceof Player)) {
            super.hurt(p_32730_, 1000.0F);
            return true;
        } else {
            return super.hurt(p_32730_, p_32731_);
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1000000000.0D).add(Attributes.FOLLOW_RANGE, 100.0D);
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32750_) {
        return SoundEvents.GHAST_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    protected float getSoundVolume() {
        return 5.0F;
    }

    public static boolean checkTameGhastEntitySpawnRules(EntityType<TameGhastEntity> p_32735_, LevelAccessor p_32736_, MobSpawnType p_32737_, BlockPos p_32738_, Random p_32739_) {
        return p_32736_.getDifficulty() != Difficulty.PEACEFUL && p_32739_.nextInt(20) == 0 && checkMobSpawnRules(p_32735_, p_32736_, p_32737_, p_32738_, p_32739_);
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public void addAdditionalSaveData(CompoundTag p_32744_) {
        super.addAdditionalSaveData(p_32744_);
        p_32744_.putByte("ExplosionPower", (byte) this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundTag p_32733_) {
        super.readAdditionalSaveData(p_32733_);
        if (p_32733_.contains("ExplosionPower", 99)) {
            this.explosionPower = p_32733_.getByte("ExplosionPower");
        }

    }

    protected float getStandingEyeHeight(Pose p_32741_, EntityDimensions p_32742_) {
        return 2.6F;
    }

    static class TameGhastEntityLookGoal extends Goal {
        private final TameGhastEntity TameGhastEntity;

        public TameGhastEntityLookGoal(TameGhastEntity p_32762_) {
            this.TameGhastEntity = p_32762_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.TameGhastEntity.getTarget() == null) {
                Vec3 vec3 = this.TameGhastEntity.getDeltaMovement();
                this.TameGhastEntity.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                this.TameGhastEntity.yBodyRot = this.TameGhastEntity.getYRot();
            } else {
                LivingEntity livingentity = this.TameGhastEntity.getTarget();
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.TameGhastEntity) < 4096.0D) {
                    double d1 = livingentity.getX() - this.TameGhastEntity.getX();
                    double d2 = livingentity.getZ() - this.TameGhastEntity.getZ();
                    this.TameGhastEntity.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.TameGhastEntity.yBodyRot = this.TameGhastEntity.getYRot();
                }
            }

        }
    }

    static class TameGhastEntityMoveControl extends MoveControl {
        private final TameGhastEntity TameGhastEntity;
        private int floatDuration;

        public TameGhastEntityMoveControl(TameGhastEntity p_32768_) {
            super(p_32768_);
            this.TameGhastEntity = p_32768_;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {

                //追加bん
                Player player = this.TameGhastEntity.level.getNearestPlayer(this.TameGhastEntity, 200.0);
                if (player != null) {

                    // ゾンビがプレイヤーの背後を追従する
                    double distanceX = player.getX() - this.TameGhastEntity.getX();
                    double distanceZ = player.getZ() - this.TameGhastEntity.getZ();
                    double angle = Math.atan2(distanceZ, distanceX);

                    // ゾンビをプレイヤーの半径3マス以内に入らないように移動させる
                    double minDistance = 3.0;
                    double currentDistance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);


                    double distancee = 20.0; // プレイヤーとの距離の閾値
                    double teleportDistance = 5.0; // テレポートする距離

                    if (this.TameGhastEntity.distanceToSqr(player) > distancee * distancee) {

                        this.TameGhastEntity.setPos(player.position().x + 2, player.position().y, player.position().z + 2);
                    }
                    if (currentDistance < minDistance) {
                        double newX = player.getX() - minDistance * Math.cos(angle);
                        double newZ = player.getZ() - minDistance * Math.sin(angle);
                        this.TameGhastEntity.setPos(newX, this.TameGhastEntity.getY(), newZ);
                    } else if (currentDistance > 4.0) {
                        // プレイヤーの位置に向かって移動
                        this.TameGhastEntity.getNavigation().moveTo(player, 1.7);
                    }
                }

                if (this.floatDuration-- <= 0) {
                    if(player != null) {

                        // ゾンビがプレイヤーの背後を追従する
                        double distanceX = player.getX() - this.TameGhastEntity.getX();
                        double distanceZ = player.getZ() - this.TameGhastEntity.getZ();
                        double angle = Math.atan2(distanceZ, distanceX);
                        // ゾンビをプレイヤーの半径3マス以内に入らないように移動させる
                        double minDistance = 1.0;
                        double currentDistance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);


                        double distancee = 20.0; // プレイヤーとの距離の閾値
                        double teleportDistance = 5.0; // テレポートする距離

                        if (currentDistance < minDistance) {
                            double newX = player.getX() - minDistance * Math.cos(angle);
                            double newZ = player.getZ() - minDistance * Math.sin(angle);
                            this.TameGhastEntity.setPos(newX, this.TameGhastEntity.getY(), newZ);
                        }

                        this.floatDuration += this.TameGhastEntity.getRandom().nextInt(5) + 2;
                        Vec3 vec3 = new Vec3(player.getX() - this.TameGhastEntity.getX(), this.wantedY - this.TameGhastEntity.getY(), player.getZ() - this.TameGhastEntity.getZ());
                        double d0 = vec3.length();
                        vec3 = vec3.normalize();
                        if (this.canReach(vec3, Mth.ceil(d0))) {
                            this.TameGhastEntity.setDeltaMovement(this.TameGhastEntity.getDeltaMovement().add(vec3.scale(0.1D)));
                        } else {
                            this.operation = MoveControl.Operation.WAIT;
                        }
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_32771_, int p_32772_) {
            AABB aabb = this.TameGhastEntity.getBoundingBox();

            for (int i = 1; i < p_32772_; ++i) {
                aabb = aabb.move(p_32771_);
                if (!this.TameGhastEntity.level.noCollision(this.TameGhastEntity, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class TameGhastEntityShootFireballGoal extends Goal {
        private final TameGhastEntity TameGhastEntity;
        public int chargeTime;

        public TameGhastEntityShootFireballGoal(TameGhastEntity p_32776_) {
            this.TameGhastEntity = p_32776_;
        }

        public boolean canUse() {
            return this.TameGhastEntity.getTarget() != null;
        }

        public void start() {
            this.chargeTime = 0;
        }

        public void stop() {
            this.TameGhastEntity.setCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.TameGhastEntity.getTarget();
            if (livingentity != null &&!(livingentity instanceof Player)) {
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.TameGhastEntity) < 4096.0D && this.TameGhastEntity.hasLineOfSight(livingentity)) {
                    Level level = this.TameGhastEntity.level;
                    ++this.chargeTime;
                    if (this.chargeTime == 10 && !this.TameGhastEntity.isSilent()) {
                        level.levelEvent((Player) null, 1015, this.TameGhastEntity.blockPosition(), 0);
                    }

                    if (this.chargeTime == 20) {
                        double d1 = 4.0D;
                        Vec3 vec3 = this.TameGhastEntity.getViewVector(1.0F);
                        double d2 = livingentity.getX() - (this.TameGhastEntity.getX() + vec3.x * 4.0D);
                        double d3 = livingentity.getY(0.5D) - (0.5D + this.TameGhastEntity.getY(0.5D));
                        double d4 = livingentity.getZ() - (this.TameGhastEntity.getZ() + vec3.z * 4.0D);
                        if (!this.TameGhastEntity.isSilent()) {
                            level.levelEvent((Player) null, 1016, this.TameGhastEntity.blockPosition(), 0);
                        }

                        LargeFireball largefireball = new LargeFireball(level, this.TameGhastEntity, d2, d3, d4, this.TameGhastEntity.getExplosionPower());
                        largefireball.setPos(this.TameGhastEntity.getX() + vec3.x * 4.0D, this.TameGhastEntity.getY(0.5D) + 0.5D, largefireball.getZ() + vec3.z * 4.0D);
                        level.addFreshEntity(largefireball);
                        this.chargeTime = -40;
                    }
                } else if (this.chargeTime > 0) {
                    --this.chargeTime;
                }

                this.TameGhastEntity.setCharging(this.chargeTime > 10);
            }
        }
    }

    static class RandomFloatAroundGoal extends Goal {
        private final TameGhastEntity TameGhastEntity;

        public RandomFloatAroundGoal(TameGhastEntity p_32783_) {
            this.TameGhastEntity = p_32783_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movecontrol = this.TameGhastEntity.getMoveControl();
            if (!movecontrol.hasWanted()) {
                return true;
            } else {
                double d0 = movecontrol.getWantedX() - this.TameGhastEntity.getX();
                double d1 = movecontrol.getWantedY() - this.TameGhastEntity.getY();
                double d2 = movecontrol.getWantedZ() - this.TameGhastEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            Random random = this.TameGhastEntity.getRandom();
            double d0 = this.TameGhastEntity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.TameGhastEntity.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.TameGhastEntity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.TameGhastEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }
}