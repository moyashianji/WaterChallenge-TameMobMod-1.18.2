package com.water.animall.Phantom.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Phantom.layer.TamePhantomEyesLayer;
import com.water.animall.Phantom.model.TamePhantomModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TamePhantomRenderer extends MobRenderer<TamePhantomEntity, TamePhantomModel<TamePhantomEntity>> {
    private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

    public TamePhantomRenderer(EntityRendererProvider.Context p_174338_) {
        super(p_174338_, new TamePhantomModel<>(p_174338_.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
        this.addLayer(new TamePhantomEyesLayer<>(this));
    }

    public ResourceLocation getTextureLocation(TamePhantomEntity p_115679_) {
        return PHANTOM_LOCATION;
    }

    protected void scale(TamePhantomEntity p_115681_, PoseStack p_115682_, float p_115683_) {
        int i = p_115681_.getTamePhantomEntitySize();
        float f = 1.0F + 0.15F * (float)i;
        p_115682_.scale(f, f, f);
        p_115682_.translate(0.0D, 1.3125D, 0.1875D);
    }

    protected void setupRotations(TamePhantomEntity p_115685_, PoseStack p_115686_, float p_115687_, float p_115688_, float p_115689_) {
        super.setupRotations(p_115685_, p_115686_, p_115687_, p_115688_, p_115689_);
        p_115686_.mulPose(Vector3f.XP.rotationDegrees(p_115685_.getXRot()));
    }
}