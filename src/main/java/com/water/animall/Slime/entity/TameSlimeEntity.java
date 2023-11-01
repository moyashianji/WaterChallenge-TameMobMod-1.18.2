package com.water.animall.Slime.entity;

import com.water.animall.Blaze.entity.TameBlazeEntity;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Ghast.entity.TameGhastEntity;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.animall.init.AnimallModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class TameSlimeEntity extends Mob implements Enemy {

    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AnimallModEntities.Slime.get(), 20, 4, 4));
    }

    public TameSlimeEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(AnimallModEntities.Slime.get(), world);
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 10000000);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }
    //胃かコピペ分

    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TameSlimeEntity.class, EntityDataSerializers.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public TameSlimeEntity(EntityType<? extends TameSlimeEntity> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
        this.moveControl = new TameSlimeEntity.TameSlimeEntityMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TameSlimeEntity.TameSlimeEntityFloatGoal(this));
        this.goalSelector.addGoal(2, new TameSlimeEntity.TameSlimeEntityAttackGoal(this));
        this.goalSelector.addGoal(3, new TameSlimeEntity.TameSlimeEntityRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new TameSlimeEntity.TameSlimeEntityKeepOnJumpingGoal(this));
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
        this.entityData.define(ID_SIZE, 1);
    }

    protected void setSize(int p_33594_, boolean p_33595_) {
        int i = Mth.clamp(p_33594_, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
        if (p_33595_) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    public void addAdditionalSaveData(CompoundTag p_33619_) {
        super.addAdditionalSaveData(p_33619_);
        p_33619_.putInt("Size", this.getSize() - 1);
        p_33619_.putBoolean("wasOnGround", this.wasOnGround);
    }

    public void readAdditionalSaveData(CompoundTag p_33607_) {
        this.setSize(p_33607_.getInt("Size") + 1, false);
        super.readAdditionalSaveData(p_33607_);
        this.wasOnGround = p_33607_.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return this.getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    protected boolean shouldDespawnInPeaceful() {
        return this.getSize() > 0;
    }
    private double maxFallDistance = 10.0; // 一定の高さまでの落下距離

    private double lastAirborneY = Double.NaN;
    public void tick() {

        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();
        Player player = this.level.getNearestPlayer(this, 200.0);
        if (player != null) {
            if (player.getVehicle() == this) {
                if (!level.isClientSide) {

                    if (isOnGround()) {
                        // モブが地面に着地した場合
                        if (!Double.isNaN(lastAirborneY)) {
                            double fallDistance = lastAirborneY - this.getY(); // 落下距離の計算
                            System.out.println(fallDistance);
                            if (fallDistance > 0) {
                                double maxFallDistance = Math.min(fallDistance, 150); // 最大値を200に制限
                                double targetY = lastAirborneY - (Math.min(maxFallDistance, 100) * 0.7); // 最大100までに制限
                                double newY = Math.max(targetY, getY());
                                this.setDeltaMovement(this.getDeltaMovement().x, newY - getY(), this.getDeltaMovement().z);
                            }
                            lastAirborneY = Double.NaN; // 空中にいない場合、lastAirborneY を NaN にリセット
                        }
                    } else {
                        if (Double.isNaN(lastAirborneY)) {
                            // モブが空中にいる場合かつ初めての空中状態
                            lastAirborneY = this.getY(); // 初回の高さの取得
                            System.out.println(lastAirborneY);
                        }
                    }

                }
            }
        }

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

          if (currentDistance > 4.0) {
                // プレイヤーの位置に向かって移動
                this.getNavigation().moveTo(player, 1.7);
            }
        }

        if (this.onGround && !this.wasOnGround) {
            int i = this.getSize();

            if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
            for(int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * ((float)Math.PI * 2F);
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float)i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float)i * 0.5F * f1;
                this.level.addParticle(this.getParticleType(), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.decreaseSquish();
    }

    protected void decreaseSquish() {
        this.targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (ID_SIZE.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    public EntityType<? extends TameSlimeEntity> getType() {
        return (EntityType<? extends TameSlimeEntity>)super.getType();
    }

    public void remove(Entity.RemovalReason p_149847_) {
        int i = this.getSize();
        if (!this.level.isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float)i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for(int l = 0; l < k; ++l) {
                float f1 = ((float)(l % 2) - 0.5F) * f;
                float f2 = ((float)(l / 2) - 0.5F) * f;
                TameSlimeEntity TameSlimeEntity = this.getType().create(this.level);
                if (this.isPersistenceRequired()) {
                    TameSlimeEntity.setPersistenceRequired();
                }

                TameSlimeEntity.setCustomName(component);
                TameSlimeEntity.setNoAi(flag);
                TameSlimeEntity.setInvulnerable(this.isInvulnerable());
                TameSlimeEntity.setSize(j, true);
                TameSlimeEntity.moveTo(this.getX() + (double)f1, this.getY() + 0.5D, this.getZ() + (double)f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addFreshEntity(TameSlimeEntity);
            }
        }

        super.remove(p_149847_);
    }

    public void push(Entity p_33636_) {
        super.push(p_33636_);
        if (p_33636_ instanceof IronGolem && this.isDealsDamage()) {
            this.dealDamage((LivingEntity)p_33636_);
        }

    }

    public void playerTouch(Player p_33611_) {
        if (this.isDealsDamage()) {
            this.dealDamage(p_33611_);
        }

    }

    protected void dealDamage(LivingEntity p_33638_) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.distanceToSqr(p_33638_) < 0.6D * (double)i * 0.6D * (double)i && this.hasLineOfSight(p_33638_) && p_33638_.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, p_33638_);
            }
        }

    }

    protected float getStandingEyeHeight(Pose p_33614_, EntityDimensions p_33615_) {
        return 0.625F * p_33615_.height;
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    protected ResourceLocation getDefaultLootTable() {
        return this.getSize() == 1 ? this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
    }

    public static boolean checkTameSlimeEntitySpawnRules(EntityType<TameSlimeEntity> p_33621_, LevelAccessor p_33622_, MobSpawnType p_33623_, BlockPos p_33624_, Random p_33625_) {
        if (p_33622_.getDifficulty() != Difficulty.PEACEFUL) {
            if (p_33622_.getBiome(p_33624_).is(Biomes.SWAMP) && p_33624_.getY() > 50 && p_33624_.getY() < 70 && p_33625_.nextFloat() < 0.5F && p_33625_.nextFloat() < p_33622_.getMoonBrightness() && p_33622_.getMaxLocalRawBrightness(p_33624_) <= p_33625_.nextInt(8)) {
                return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
            }

            if (!(p_33622_ instanceof WorldGenLevel)) {
                return false;
            }

            ChunkPos chunkpos = new ChunkPos(p_33624_);
            boolean flag = WorldgenRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((WorldGenLevel)p_33622_).getSeed(), 987234911L).nextInt(10) == 0;
            if (p_33625_.nextInt(10) == 0 && flag && p_33624_.getY() < 40) {
                return checkMobSpawnRules(p_33621_, p_33622_, p_33623_, p_33624_, p_33625_);
            }
        }

        return false;
    }

    protected float getSoundVolume() {
        return 0.4F * (float)this.getSize();
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return this.getSize() > 0;
    }

    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }



    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, @Nullable SpawnGroupData p_33604_, @Nullable CompoundTag p_33605_) {
        int i = this.random.nextInt(3);
        if (i < 2 && this.random.nextFloat() < 0.5F * p_33602_.getSpecialMultiplier()) {
            ++i;
        }

        int j = 1 << i;
        this.setSize(j, true);
        return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_, p_33605_);
    }

    float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(0.255F * (float)this.getSize());
    }

    /**
     * Called when the TameSlimeEntity spawns particles on landing, see onUpdate.
     * Return true to prevent the spawning of the default particles.
     */
    protected boolean spawnCustomParticles() { return false; }

    static class TameSlimeEntityAttackGoal extends Goal {
        private final TameSlimeEntity TameSlimeEntity;
        private int growTiredTimer;

        public TameSlimeEntityAttackGoal(TameSlimeEntity p_33648_) {
            this.TameSlimeEntity = p_33648_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.TameSlimeEntity.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return !this.TameSlimeEntity.canAttack(livingentity) ? false : this.TameSlimeEntity.getMoveControl() instanceof TameSlimeEntity.TameSlimeEntityMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.TameSlimeEntity.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.TameSlimeEntity.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.TameSlimeEntity.getTarget();
            if (livingentity != null) {
                this.TameSlimeEntity.lookAt(livingentity, 10.0F, 10.0F);
            }

            ((TameSlimeEntity.TameSlimeEntityMoveControl)this.TameSlimeEntity.getMoveControl()).setDirection(this.TameSlimeEntity.getYRot(), this.TameSlimeEntity.isDealsDamage());
        }
    }

    static class TameSlimeEntityFloatGoal extends Goal {
        private final TameSlimeEntity TameSlimeEntity;

        public TameSlimeEntityFloatGoal(TameSlimeEntity p_33655_) {
            this.TameSlimeEntity = p_33655_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            p_33655_.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (this.TameSlimeEntity.isInWater() || this.TameSlimeEntity.isInLava()) && this.TameSlimeEntity.getMoveControl() instanceof TameSlimeEntity.TameSlimeEntityMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.TameSlimeEntity.getRandom().nextFloat() < 0.8F) {
                this.TameSlimeEntity.getJumpControl().jump();
            }

            ((TameSlimeEntity.TameSlimeEntityMoveControl)this.TameSlimeEntity.getMoveControl()).setWantedMovement(1.2D);
        }
    }

    static class TameSlimeEntityKeepOnJumpingGoal extends Goal {
        private final TameSlimeEntity TameSlimeEntity;

        public TameSlimeEntityKeepOnJumpingGoal(TameSlimeEntity p_33660_) {
            this.TameSlimeEntity = p_33660_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.TameSlimeEntity.isPassenger();
        }

        public void tick() {
            ((TameSlimeEntity.TameSlimeEntityMoveControl)this.TameSlimeEntity.getMoveControl()).setWantedMovement(1.0D);
        }
    }

    static class TameSlimeEntityMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final TameSlimeEntity TameSlimeEntity;
        private boolean isAggressive;

        public TameSlimeEntityMoveControl(TameSlimeEntity p_33668_) {
            super(p_33668_);
            this.TameSlimeEntity = p_33668_;
            this.yRot = 180.0F * p_33668_.getYRot() / (float)Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.TameSlimeEntity.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.TameSlimeEntity.getJumpControl().jump();
                        if (this.TameSlimeEntity.doPlayJumpSound()) {
                            this.TameSlimeEntity.playSound(this.TameSlimeEntity.getJumpSound(), this.TameSlimeEntity.getSoundVolume(), this.TameSlimeEntity.getSoundPitch());
                        }
                    } else {
                        this.TameSlimeEntity.xxa = 0.0F;
                        this.TameSlimeEntity.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class TameSlimeEntityRandomDirectionGoal extends Goal {
        private final TameSlimeEntity TameSlimeEntity;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public TameSlimeEntityRandomDirectionGoal(TameSlimeEntity p_33679_) {
            this.TameSlimeEntity = p_33679_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.TameSlimeEntity.getTarget() == null && (this.TameSlimeEntity.onGround || this.TameSlimeEntity.isInWater() || this.TameSlimeEntity.isInLava() || this.TameSlimeEntity.hasEffect(MobEffects.LEVITATION)) && this.TameSlimeEntity.getMoveControl() instanceof TameSlimeEntity.TameSlimeEntityMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.TameSlimeEntity.getRandom().nextInt(60));
                this.chosenDegrees = (float)this.TameSlimeEntity.getRandom().nextInt(360);
            }

            ((TameSlimeEntity.TameSlimeEntityMoveControl)this.TameSlimeEntity.getMoveControl()).setDirection(this.chosenDegrees, false);
        }
    }

}
