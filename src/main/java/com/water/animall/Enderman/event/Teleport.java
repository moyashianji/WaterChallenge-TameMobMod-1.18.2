package com.water.animall.Enderman.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.sound.sampled.Clip;

public class Teleport {
    private static boolean teleporting = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (teleporting) {

                // 3秒経過後に実行
                if (event.player.tickCount % 60 == 0) {
                    teleporting = false;
                    teleportPlayerToBlock(event.player);
                    System.out.println("teleportt");
                }
            } else {
                // Xキーが押されたらテレポートを開始
                if (isKeyPressed(GLFW.GLFW_KEY_X)) {
                    if(!(event.player.level.isClientSide)) {
                        spawnHeartParticles((ServerLevel) event.player.getLevel(), event.player);
                        teleporting = true;
                    }
                }
            }
        }
    }
    private static void spawnHeartParticles(ServerLevel world, Player player) {
        // ハートパーティクルを生成して再生
        for (int i = 0; i < 360; i++) { // 必要な数だけ繰り返す
            double xOffset = player.getX() + (world.getRandom().nextDouble() - 0.5) * 2.0;
            double yOffset = player.getY() + player.getBbHeight() * 0.5;
            double zOffset = player.getZ() + (world.getRandom().nextDouble() - 0.5) * 2.0;
            world.sendParticles(ParticleTypes.PORTAL, xOffset, yOffset, zOffset, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
    private static void teleportPlayerToBlock(Player player) {
        // プレイヤーの視線の先にあるブロックを取得
        HitResult result = rayTracePlayerView(player, 500.0D);
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
            System.out.println("blocckks");
            System.out.println(blockPos);
            // プレイヤーをブロックの前にテレポート
            player.teleportTo(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5);
        }
    }

    private static HitResult rayTracePlayerView(Player player, double range) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.x * range, look.y * range, look.z * range);
        return player.level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    private static boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), keyCode) == GLFW.GLFW_PRESS;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(Teleport.class);
    }

}