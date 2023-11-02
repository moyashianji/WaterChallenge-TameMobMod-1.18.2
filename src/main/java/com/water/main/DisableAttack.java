package com.water.main;

import com.water.animall.Blaze.entity.TameBlazeEntity;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Ghast.entity.TameGhastEntity;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Slime.entity.TameSlimeEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.item.RideItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DisableAttack {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof TameIronGolem) {


            // プレイヤーがダメージを受けた場合
            if (event.getSource() == DamageSource.FALL
                    || event.getSource() == DamageSource.IN_FIRE
                    || event.getSource() == DamageSource.ON_FIRE
                    || event.getSource() == DamageSource.LAVA) { // マグマからのダメージの場合（適切なダメージソースを指定）
                event.setCanceled(true); // ダメージを無効にする
                System.out.println("falling");
            }else{
            }
        }
    }
    @SubscribeEvent
    public static void onLivingDamagse(LivingDamageEvent event) {
        if (event.getEntity() instanceof TameBlazeEntity) {
            System.out.println("gggg");

            // プレイヤーがダメージを受けた場合
            if (event.getSource().getEntity() instanceof TameIronGolem) { // マグマからのダメージの場合（適切なダメージソースを指定）
                event.setCanceled(true); // ダメージを無効にする

            }else{
            }

        }
    }

    @SubscribeEvent
    public static void onMobDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof TameBlazeEntity) {
            if(event.getSource().getEntity() instanceof TameIronGolem) {
                // ゾンビが攻撃を受けた場合、ダメージをキャンセル
                System.out.println("golemm");
                event.setCanceled(true);
            }
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(DisableAttack.class);
    }
}
