package com.water.animall.Phantom.layer;

import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Phantom.model.TamePhantomModel;
import com.water.animall.Phantom.renderer.TamePhantomRenderer;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TamePhantomEyesLayer<T extends TamePhantomEntity> extends EyesLayer<T, TamePhantomModel<T>> {
    private static final RenderType PHANTOM_EYES = RenderType.eyes(new ResourceLocation("textures/entity/phantom_eyes.png"));

    public TamePhantomEyesLayer(RenderLayerParent<T, TamePhantomModel<T>> p_117342_) {
        super(p_117342_);
    }

    public RenderType renderType() {
        return PHANTOM_EYES;
    }
}