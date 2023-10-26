
package com.water.item;

import com.water.init.TestttModItems;
import com.water.procedures.RidemobugaaitemudeGongJisaretatokiProcedure;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
public class RideItem extends Item {
	public RideItem() {
		super(new Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64).rarity(Rarity.COMMON));
	}

	public static boolean Key = true;
	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);

		if(entity instanceof Slime) {
			RidemobugaaitemudeGongJisaretatokiProcedure.execute(entity, sourceentity);
		}
		return retval;
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.LeftClickBlock event) {
		Player player = event.getPlayer();
		if (event.getItemStack().getItem() == TestttModItems.RIDE.get()) {

			if (player.getVehicle() instanceof Slime) {

				if(player.getVehicle().isOnGround()) {
					((Slime) player.getVehicle()).setJumping(true);
					player.getVehicle().setDeltaMovement(new Vec3((player.getVehicle().getDeltaMovement().x() + player.getVehicle().getLookAngle().x * 0.5), (player.getVehicle().getDeltaMovement().y() + 0.7),
							(player.getVehicle().getDeltaMovement().z() + player.getVehicle().getLookAngle().z * 0.5)));
				}
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if(!player.isPassenger()) {
			return super.use(level, player, usedHand);
		}
		Entity playerMount = player.getVehicle();
		addMotion(player, playerMount);
		LivingEntity lp = (LivingEntity) playerMount;

		if (player.level.isClientSide) {
			if (player instanceof LocalPlayer && ((LocalPlayer) player).input.jumping) {
				addJamp(player,playerMount);
			}
		}
		return super.use(level, player, usedHand);
	}
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Player player = event.player;
			if (player.isPassenger() && player.getVehicle() instanceof Slime) {
				Slime zombie = (Slime) player.getVehicle();
				// Synchronize the motion of the zombie with the player.
				System.out.println("slime");

				player.getAbilities().invulnerable = (true);
				player.onUpdateAbilities();

				move((LivingEntity) player.getVehicle(), event.player.getDeltaMovement());
				setLookAngle(player.getVehicle(), player);
			}
			if(!(player.getVehicle() instanceof Slime)){
				if(!player.isCreative()) {
					player.getAbilities().invulnerable = (false);
					player.onUpdateAbilities();
				}
			}

		}
	}
	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		if (event.getEntityLiving() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) event.getEntityLiving();
			if (entity instanceof Slime) {
					if(!(event.getSource().getEntity() instanceof Player)) {
						event.setCanceled(true); // ゾンビの攻撃を無効化
						System.out.println("AttackDisable");
					}
			}
		}
	}
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if (event.getSource().getEntity() instanceof Slime) {


				event.setCanceled(true); // ゾンビのダメージを無効化
				System.out.println("SlimeHurtDisabled");
			}
		}
	//動く
	private void addMotion(Player player, Entity playerMount) {

		Vec3 lookAngle = player.getLookAngle();
		Vec3 lastMotion = playerMount.getDeltaMovement();

		Vec3 newMotion = new Vec3(lastMotion.x + (lookAngle.x/2), lastMotion.y, lastMotion.z + (lookAngle.z/2));

		playerMount.setDeltaMovement(newMotion);

	}
	//ジャンプ
	private void addJamp(Player player, Entity playerMount) {

		playerMount.setDeltaMovement(new Vec3((playerMount.getDeltaMovement().x() + playerMount.getLookAngle().x * 0.5), (playerMount.getDeltaMovement().y() + 20.3),
				(playerMount.getDeltaMovement().z() + player.getVehicle().getLookAngle().z * 0.5)));

	}
	//顔の向き
	private static void setLookAngle(Entity entity, Player player) {

		float yHeadRot = player.yHeadRot;
		float yHeadRot0 = player.yHeadRotO;
		float yRot = player.getYRot();
		float yRot0 = player.yRotO;
		float xRot = player.getXRot();
		float xRot0 = player.xRotO;

		entity.setYHeadRot(yHeadRot);
		entity.setYRot(yRot);
		entity.setXRot(xRot);
		entity.xRotO = xRot0;
		entity.yRotO = yRot0;

	}
	//フラグの無効化
	public static void move(LivingEntity entity, Vec3 vector) {
		if (entity instanceof Mob) {
			Mob mob = (Mob)entity;
			Goal.Flag[] var3 = Goal.Flag.values();
			int var4 = var3.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				Goal.Flag value = var3[var5];
				mob.goalSelector.disableControlFlag(value);
			}
		}

	}
	public static void register() {
		MinecraftForge.EVENT_BUS.register(RideItem.class);
	}
}


