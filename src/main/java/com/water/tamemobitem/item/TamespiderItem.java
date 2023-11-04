
package com.water.tamemobitem.item;

import com.water.tamemobitem.procedures.TamespiderYoukuritukusitatokiProcedure;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;


public class TamespiderItem extends Item {
	public TamespiderItem() {
		super(new Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if(!(entity.level.isClientSide)) {
			ServerLevel level = (ServerLevel) world;

			for (int i = 0; i < 360; i++) { // 必要な数だけ繰り返す

				double xOffset = x + (world.getRandom().nextDouble() - 0.5) * 3.0;
				double yOffset = y + 2;
				double zOffset = z + (world.getRandom().nextDouble() - 0.5) * 3.0;
				level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, xOffset, yOffset, zOffset, 1, 0.0, 0.0, 0.0, 0.0);
			}
		}
		TamespiderYoukuritukusitatokiProcedure.execute(world, x, y, z);
		return ar;
	}
}
