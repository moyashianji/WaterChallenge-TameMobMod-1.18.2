
package com.water.teamitem.item;

import com.water.init.TestttModItems;
import com.water.item.RideItem;
import com.water.teamitem.init.TeamitemModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WolfItem extends Item {
	public WolfItem() {
		super(new Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64).rarity(Rarity.COMMON));
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getPlayer();
		if (event.getItemStack().getItem() == TeamitemModItems.WOLF.get()) {

			if (player.level instanceof ServerLevel _level) {
				Entity entityToSpawn = new Creeper(EntityType.CREEPER, _level);
				if (entityToSpawn instanceof Mob _mobToSpawn)
					_mobToSpawn.finalizeSpawn(_level, player.level.getCurrentDifficultyAt(entityToSpawn.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
				((Wolf) entityToSpawn).tame(player);
				entityToSpawn.setPos(player.getX(),player.getY(),player.getZ());
				player.level.addFreshEntity(entityToSpawn);
			}

		}
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(WolfItem.class);
	}
}
