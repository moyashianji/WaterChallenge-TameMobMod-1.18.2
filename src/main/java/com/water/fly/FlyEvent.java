package com.water.fly;

import com.water.animall.Phantom.entity.TamePhantomEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.jar.Attributes;

public class FlyEvent {


    private static int noGravityTicks = 0;

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getPlayer();
        if (event.getTarget() instanceof TamePhantomEntity) {
            // noGravityTicks を設定して20秒間だけsetNoGravityをtrueにする
            noGravityTicks = 700; // 20 ticks/秒で計算
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            // noGravityTicks をカウントダウン
            if (noGravityTicks > 0) {
                noGravityTicks--;
            } else {
                // 20秒経過後、setNoGravityを元に戻す
                Player player = event.player;
                player.getAbilities().mayfly = false;

                player.setNoGravity(false);
                player.onUpdateAbilities();
            }
        }
    }
    public static void register() {
        MinecraftForge.EVENT_BUS.register(FlyEvent.class);
    }
}
