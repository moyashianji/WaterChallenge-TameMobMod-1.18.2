package com.water.animall.Blaze.entity;

import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Ghast.entity.TameGhastEntity;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Slime.entity.TameSlimeEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.animall.init.AnimallModEntities;
import com.water.tamemobitem.init.TamemobModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.EnumSet;

public class TameBlazeEntity extends Monster {

    @SubscribeEvent
    public static void addLivingEntityToBiomes(BiomeLoadingEvent event) {
        event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(AnimallModEntities.BLAZE.get(), 20, 4, 4));
    }

    public TameBlazeEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(AnimallModEntities.BLAZE.get(), world);
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void init() {
        SpawnPlacements.register(AnimallModEntities.BLAZE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos,
                 random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
    }

    //いかが追加分

    private float allowedHeightOffset = 0.5F;
    private int nextHeightOffsetChangeTick;
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Blaze.class, EntityDataSerializers.BYTE);

    public TameBlazeEntity(EntityType<? extends TameBlazeEntity> p_32219_, Level p_32220_) {
        super(p_32219_, p_32220_);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 10;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(4, new TameBlazeEntity.TameBlazeEntityAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
     //   this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
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

    public static AttributeSupplier.Builder createAttributes() {

        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,10000000).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.FOLLOW_RANGE, 48.0D);

    }



    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32235_) {
        return SoundEvents.BLAZE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    public float getBrightness() {
        return 1.0F;
    }

    public void aiStep() {

        //追加bん
        Player player = level.getNearestPlayer(this, 200.0);
        if (player != null) {

            if (hasEnderPearlNearby()) {
                // ゾンビの周りにエンダーパールがある場合の処理
                System.out.println("hello");
            }


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



        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.aiStep();
    }


    // ゾンビの周りにエンダーパールがあるかを確認するメソッド
    private boolean hasEnderPearlNearby() {
        double radius = 0.5; // 半径2マス

        int radiusInt = (int) Math.ceil(radius);
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(radius))) {
            if (itemEntity.getItem().getItem() == Items.ENDER_PEARL) {
                itemEntity.discard();

                if (!this.level.isClientSide()) {
                    ItemEntity entityToSpawn = new ItemEntity(this.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(Items.ENDER_EYE.getDefaultInstance().getItem()));
                    entityToSpawn.setPickUpDelay(0);
                    entityToSpawn.setUnlimitedLifetime();
                    this.level.addFreshEntity(entityToSpawn);
                }
                return true;
            }
        }
        return false;
    }
    public boolean isSensitiveToWater() {
        return true;
    }

    protected void customServerAiStep() {
        --this.nextHeightOffsetChangeTick;
        if (this.nextHeightOffsetChangeTick <= 0) {
            this.nextHeightOffsetChangeTick = 100;
            this.allowedHeightOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;
        }

        LivingEntity livingentity = this.getTarget();
        if (livingentity != null && livingentity.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(livingentity)) {
            Vec3 vec3 = this.getDeltaMovement();

         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double)0.3F - vec3.y) * (double)0.3F, 0.0D));
            this.hasImpulse = true;
        }

        super.customServerAiStep();
    }

    public boolean causeFallDamage(float p_149683_, float p_149684_, DamageSource p_149685_) {
        return false;
    }

    public boolean isOnFire() {
        return this.isCharged();
    }

    private boolean isCharged() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    void setCharged(boolean p_32241_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_32241_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    static class TameBlazeEntityAttackGoal extends Goal {
        private final TameBlazeEntity TameBlazeEntity;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public TameBlazeEntityAttackGoal(TameBlazeEntity p_32247_) {
            this.TameBlazeEntity = p_32247_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.TameBlazeEntity.getTarget();
            return livingentity != null && livingentity.isAlive() && this.TameBlazeEntity.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
                this.TameBlazeEntity.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {


            --this.attackTime;
            LivingEntity livingentity = this.TameBlazeEntity.getTarget();

            if (livingentity != null && !(livingentity instanceof Player)) {
                boolean flag = this.TameBlazeEntity.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.TameBlazeEntity.distanceToSqr(livingentity);
                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.TameBlazeEntity.doHurtTarget(livingentity);
                    }


                   this.TameBlazeEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.TameBlazeEntity.getX();
                    double d2 = livingentity.getY(0.5D) - this.TameBlazeEntity.getY(0.5D);
                    double d3 = livingentity.getZ() - this.TameBlazeEntity.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.TameBlazeEntity.setCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                                           this.TameBlazeEntity.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;
                            if (!this.TameBlazeEntity.isSilent()) {
                                this.TameBlazeEntity.level.levelEvent((Player)null, 1018, this.TameBlazeEntity.blockPosition(), 0);
                            }

                            for(int i = 0; i < 1; ++i) {
                                SmallFireball smallfireball = new SmallFireball(this.TameBlazeEntity.level, this.TameBlazeEntity, d1 + this.TameBlazeEntity.getRandom().nextGaussian() * d4, d2, d3 + this.TameBlazeEntity.getRandom().nextGaussian() * d4);
                                smallfireball.setPos(smallfireball.getX(), this.TameBlazeEntity.getY(0.5D) + 0.5D, smallfireball.getZ());
                                this.TameBlazeEntity.level.addFreshEntity(smallfireball);
                            }
                        }
                    }

                    this.TameBlazeEntity.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                         this.TameBlazeEntity.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.TameBlazeEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }

}
