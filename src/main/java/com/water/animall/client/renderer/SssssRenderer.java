
package com.water.animall.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.water.animall.entity.SssssEntity;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;


public class SssssRenderer extends MobRenderer<SssssEntity, CreeperModel<SssssEntity>> {
	private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

	public SssssRenderer(EntityRendererProvider.Context context) {
		super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.5f);
	}



	protected void scale(Creeper p_114046_, PoseStack p_114047_, float p_114048_) {
		float f = p_114046_.getSwelling(p_114048_);
		float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
		f = Mth.clamp(f, 0.0F, 1.0F);
		f *= f;
		f *= f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		p_114047_.scale(f2, f3, f2);
	}

	protected float getWhiteOverlayProgress(Creeper p_114043_, float p_114044_) {
		float f = p_114043_.getSwelling(p_114044_);
		return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
	}
	@Override
	public ResourceLocation getTextureLocation(SssssEntity p_114041_) {
		return CREEPER_LOCATION;
	}
}

