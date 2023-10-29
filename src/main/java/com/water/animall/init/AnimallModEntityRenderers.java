
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.animall.init;

import com.water.animall.Blaze.renderer.TameBlazeRenderer;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Cow.renderer.TameCowRenderer;
import com.water.animall.Enderman.renderer.TameEndermanRenderer;
import com.water.animall.Ghast.renderer.TameGhastRenderer;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Gholem.renderer.TameIronGolemRenderer;
import com.water.animall.Slime.renderer.TameSlimeRenderer;
import com.water.animall.Phantom.renderer.TamePhantomRenderer;
import com.water.animall.Spider.renderer.TameSpiderRenderer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;
import com.water.animall.Creeper.renderer.SssssRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AnimallModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(AnimallModEntities.SSSSS.get(), SssssRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.Slime.get(), TameSlimeRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.PHANTOM.get(), TamePhantomRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.BLAZE.get(), TameBlazeRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.ENDERMAN.get(), TameEndermanRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.GHAST.get(), TameGhastRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.SPIDER.get(), TameSpiderRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.GOLEM.get(), TameIronGolemRenderer::new);
		event.registerEntityRenderer(AnimallModEntities.COW.get(), TameCowRenderer::new);

	}

}
