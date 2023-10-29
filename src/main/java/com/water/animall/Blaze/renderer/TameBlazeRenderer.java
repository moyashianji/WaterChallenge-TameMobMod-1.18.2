package com.water.animall.Blaze.renderer;


import com.water.animall.Blaze.entity.TameBlazeEntity;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TameBlazeRenderer extends MobRenderer<TameBlazeEntity, BlazeModel<TameBlazeEntity>> {
    private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

    public TameBlazeRenderer(EntityRendererProvider.Context p_173933_) {
        super(p_173933_, new BlazeModel<>(p_173933_.bakeLayer(ModelLayers.BLAZE)), 0.5F);
    }

    protected int getBlockLightLevel(TameBlazeEntity p_113910_, BlockPos p_113911_) {
        return 15;
    }

    public ResourceLocation getTextureLocation(TameBlazeEntity p_113908_) {
        return BLAZE_LOCATION;
    }
}