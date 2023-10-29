package com.water.animall.Piglin;

import com.water.item.RideItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PiglinEntity {


    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Piglin) {
            Piglin zombie = (Piglin) event.getTarget();
            Player player = event.getPlayer();

            // ゾンビのHPを設定
            zombie.setHealth(100000);
            System.out.println("target");
            // ゾンビがプレイヤーを攻撃しないようにする
            // ゾンビが近くにいる敵モブを攻撃する
            for (LivingEntity entity : player.level.getEntitiesOfClass(LivingEntity.class, zombie.getBoundingBox().inflate(10.0))) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    zombie.canAttack(entity);
                    System.out.println("tteststsaa");

                    break;
                }
            }
        }
    }
    public static void register() {
        MinecraftForge.EVENT_BUS.register(PiglinEntity.class);
    }

}
