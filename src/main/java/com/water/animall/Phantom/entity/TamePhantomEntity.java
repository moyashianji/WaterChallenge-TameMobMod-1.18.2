package com.water.animall.Phantom.entity;

import com.water.animall.Blaze.entity.TameBlazeEntity;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Ghast.entity.TameGhastEntity;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Slime.entity.TameSlimeEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.animall.init.AnimallModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class TamePhantomEntity extends FlyingMob implements Enemy {

    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AnimallModEntities.Slime.get(), 20, 4, 4));
    }

    public TamePhantomEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(AnimallModEntities.PHANTOM.get(), world);
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void init() {
        SpawnPlacements.register(AnimallModEntities.SSSSS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos,
                 random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 100000000);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    //胃かコピペ

    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TamePhantomEntity.class, EntityDataSerializers.INT);
    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    TamePhantomEntity.AttackPhase attackPhase = TamePhantomEntity.AttackPhase.CIRCLE;

    public TamePhantomEntity(EntityType<? extends TamePhantomEntity> p_33101_, Level p_33102_) {
        super(p_33101_, p_33102_);
        this.xpReward = 5;
        this.moveControl = new TamePhantomEntity.TamePhantomEntityMoveControl(this);
        this.lookControl = new TamePhantomEntity.TamePhantomEntityLookControl(this);
    }

    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    protected BodyRotationControl createBodyControl() {
        return new TamePhantomEntity.TamePhantomEntityBodyRotationControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TamePhantomEntity.TamePhantomEntityAttackStrategyGoal());
        this.goalSelector.addGoal(2, new TamePhantomEntity.TamePhantomEntitySweepAttackGoal());
        this.goalSelector.addGoal(3, new TamePhantomEntity.TamePhantomEntityCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new TamePhantomEntity.TamePhantomEntityAttackPlayerTargetGoal());
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

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public void setTamePhantomEntitySize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    private void updateTamePhantomEntitySizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getTamePhantomEntitySize()));
    }

    public int getTamePhantomEntitySize() {
        return this.entityData.get(ID_SIZE);
    }

    protected float getStandingEyeHeight(Pose p_33136_, EntityDimensions p_33137_) {
        return p_33137_.height * 0.35F;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updateTamePhantomEntitySizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float f = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            float f1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getTamePhantomEntitySize();
            float f2 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)f2, this.getY() + (double)f4, this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)f2, this.getY() + (double)f4, this.getZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }

    }

    public void aiStep() {

        Player player = this.level.getNearestPlayer(this, 200.0);
        if (player != null) {

            // ゾンビがプレイヤーの背後を追従する
            double distanceX = player.getX() - this.getX();
            double distanceZ = player.getZ() - this.getZ();
            double angle = Math.atan2(distanceZ, distanceX);

            // ゾンビをプレイヤーの半径3マス以内に入らないように移動させる
            double minDistance = 3.0;
            double currentDistance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);


            double distancee = 20.0; // プレイヤーとの距離の閾値
            double teleportDistance = 5.0; // テレポートする距離

            if (this.distanceToSqr(player) > distancee * distancee) {

                this.setPos(player.position().x + 2, player.position().y, player.position().z + 2);
            }
            if (currentDistance < minDistance) {
                double newX = player.getX() - minDistance * Math.cos(angle);
                double newZ = player.getZ() - minDistance * Math.sin(angle);
                this.setPos(newX, this.getY(), newZ);
            } else if (currentDistance > 4.0) {
                // プレイヤーの位置に向かって移動
                this.getNavigation().moveTo(player, 1.7);
            }
        }

        if (this.isAlive() && this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }

        super.aiStep();
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33126_, DifficultyInstance p_33127_, MobSpawnType p_33128_, @Nullable SpawnGroupData p_33129_, @Nullable CompoundTag p_33130_) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setTamePhantomEntitySize(0);
        return super.finalizeSpawn(p_33126_, p_33127_, p_33128_, p_33129_, p_33130_);
    }

    public void readAdditionalSaveData(CompoundTag p_33132_) {
        super.readAdditionalSaveData(p_33132_);
        if (p_33132_.contains("AX")) {
            this.anchorPoint = new BlockPos(p_33132_.getInt("AX"), p_33132_.getInt("AY"), p_33132_.getInt("AZ"));
        }

        this.setTamePhantomEntitySize(p_33132_.getInt("Size"));
    }

    public void addAdditionalSaveData(CompoundTag p_33141_) {
        super.addAdditionalSaveData(p_33141_);
        p_33141_.putInt("AX", this.anchorPoint.getX());
        p_33141_.putInt("AY", this.anchorPoint.getY());
        p_33141_.putInt("AZ", this.anchorPoint.getZ());
        p_33141_.putInt("Size", this.getTamePhantomEntitySize());
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return true;
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33152_) {
        return SoundEvents.PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    public boolean canAttackType(EntityType<?> p_33111_) {
        return true;
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        int i = this.getTamePhantomEntitySize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + 0.2F * (float)i) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP;
    }

    class TamePhantomEntityAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = reducedTickDelay(20);

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = TamePhantomEntity.this.level.getNearbyPlayers(this.attackTargeting, TamePhantomEntity.this, TamePhantomEntity.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for(Player player : list) {
                        if (TamePhantomEntity.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            TamePhantomEntity.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = TamePhantomEntity.this.getTarget();
            return livingentity != null ? TamePhantomEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }
    }

    class TamePhantomEntityAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        public boolean canUse() {
            LivingEntity livingentity = TamePhantomEntity.this.getTarget();
            return livingentity != null ? TamePhantomEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            TamePhantomEntity.this.attackPhase = TamePhantomEntity.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            TamePhantomEntity.this.anchorPoint = TamePhantomEntity.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, TamePhantomEntity.this.anchorPoint).above(10 + TamePhantomEntity.this.random.nextInt(20));
        }

        public void tick() {
            if (TamePhantomEntity.this.attackPhase == TamePhantomEntity.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    TamePhantomEntity.this.attackPhase = TamePhantomEntity.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + TamePhantomEntity.this.random.nextInt(4)) * 20);
                    TamePhantomEntity.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + TamePhantomEntity.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            TamePhantomEntity.this.anchorPoint = TamePhantomEntity.this.getTarget().blockPosition().above(20 + TamePhantomEntity.this.random.nextInt(20));
            if (TamePhantomEntity.this.anchorPoint.getY() < TamePhantomEntity.this.level.getSeaLevel()) {
                TamePhantomEntity.this.anchorPoint = new BlockPos(TamePhantomEntity.this.anchorPoint.getX(), TamePhantomEntity.this.level.getSeaLevel() + 1, TamePhantomEntity.this.anchorPoint.getZ());
            }

        }
    }

    class TamePhantomEntityBodyRotationControl extends BodyRotationControl {
        public TamePhantomEntityBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        public void clientTick() {
            TamePhantomEntity.this.yHeadRot = TamePhantomEntity.this.yBodyRot;
            TamePhantomEntity.this.yBodyRot = TamePhantomEntity.this.getYRot();
        }
    }

    class TamePhantomEntityCircleAroundAnchorGoal extends TamePhantomEntity.TamePhantomEntityMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return TamePhantomEntity.this.getTarget() == null || TamePhantomEntity.this.attackPhase == TamePhantomEntity.AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + TamePhantomEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + TamePhantomEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = TamePhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (TamePhantomEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + TamePhantomEntity.this.random.nextFloat() * 9.0F;
            }

            if (TamePhantomEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (TamePhantomEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = TamePhantomEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (TamePhantomEntity.this.moveTargetPoint.y < TamePhantomEntity.this.getY() && !TamePhantomEntity.this.level.isEmptyBlock(TamePhantomEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (TamePhantomEntity.this.moveTargetPoint.y > TamePhantomEntity.this.getY() && !TamePhantomEntity.this.level.isEmptyBlock(TamePhantomEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(TamePhantomEntity.this.anchorPoint)) {
                TamePhantomEntity.this.anchorPoint = TamePhantomEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            TamePhantomEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(TamePhantomEntity.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    class TamePhantomEntityLookControl extends LookControl {
        public TamePhantomEntityLookControl(Mob p_33235_) {
            super(p_33235_);
        }

        public void tick() {
        }
    }

    class TamePhantomEntityMoveControl extends MoveControl {
        private float speed = 0.1F;

        public TamePhantomEntityMoveControl(Mob p_33241_) {
            super(p_33241_);
        }

        public void tick() {
            if (TamePhantomEntity.this.horizontalCollision) {
                TamePhantomEntity.this.setYRot(TamePhantomEntity.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = TamePhantomEntity.this.moveTargetPoint.x - TamePhantomEntity.this.getX();
            double d1 = TamePhantomEntity.this.moveTargetPoint.y - TamePhantomEntity.this.getY();
            double d2 = TamePhantomEntity.this.moveTargetPoint.z - TamePhantomEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = TamePhantomEntity.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(TamePhantomEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                TamePhantomEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                TamePhantomEntity.this.yBodyRot = TamePhantomEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, TamePhantomEntity.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)(180F / (float)Math.PI)));
                TamePhantomEntity.this.setXRot(f4);
                float f5 = TamePhantomEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = TamePhantomEntity.this.getDeltaMovement();
                TamePhantomEntity.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
            }

        }
    }

    abstract class TamePhantomEntityMoveTargetGoal extends Goal {
        public TamePhantomEntityMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return TamePhantomEntity.this.moveTargetPoint.distanceToSqr(TamePhantomEntity.this.getX(), TamePhantomEntity.this.getY(), TamePhantomEntity.this.getZ()) < 4.0D;
        }
    }

    class TamePhantomEntitySweepAttackGoal extends TamePhantomEntity.TamePhantomEntityMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        public boolean canUse() {
            return TamePhantomEntity.this.getTarget() != null && TamePhantomEntity.this.attackPhase == TamePhantomEntity.AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = TamePhantomEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player) {
                    Player player = (Player)livingentity;
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (TamePhantomEntity.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = TamePhantomEntity.this.tickCount + 20;
                        List<Cat> list = TamePhantomEntity.this.level.getEntitiesOfClass(Cat.class, TamePhantomEntity.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void start() {
        }

        public void stop() {
            TamePhantomEntity.this.setTarget((LivingEntity)null);
            TamePhantomEntity.this.attackPhase = TamePhantomEntity.AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = TamePhantomEntity.this.getTarget();
            if (livingentity != null) {
                TamePhantomEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (TamePhantomEntity.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    TamePhantomEntity.this.doHurtTarget(livingentity);
                    TamePhantomEntity.this.attackPhase = TamePhantomEntity.AttackPhase.CIRCLE;
                    if (!TamePhantomEntity.this.isSilent()) {
                        TamePhantomEntity.this.level.levelEvent(1039, TamePhantomEntity.this.blockPosition(), 0);
                    }
                } else if (TamePhantomEntity.this.horizontalCollision || TamePhantomEntity.this.hurtTime > 0) {
                    TamePhantomEntity.this.attackPhase = TamePhantomEntity.AttackPhase.CIRCLE;
                }

            }
        }
    }
}