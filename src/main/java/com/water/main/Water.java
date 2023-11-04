package com.water.main;

import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.event.Teleport;
import com.water.animall.Piglin.PiglinEntity;
import com.water.animall.init.AnimallModEntities;
import com.water.animall.init.AnimallModItems;
import com.water.fly.FlyEvent;
import com.water.gunss.init.GunssModEntities;
import com.water.gunss.init.GunssModItems;
import com.water.init.TestttModItems;
import com.water.item.RideItem;
import com.water.item.RidingUtilsCommonConfigs;
import com.water.tamemobitem.init.TamemobModItems;
import com.water.tamemobitem.item.TameslimeItem;
import com.water.teamitem.init.TeamitemModItems;
import com.water.teamitem.item.WolfItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class Water {

    public Water() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TestttModItems.REGISTRY.register(bus);
        RideItem.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RidingUtilsCommonConfigs.SPEC, "ridingutilities-common.toml");
        GunssModEntities.REGISTRY.register(bus);
        GunssModItems.REGISTRY.register(bus);
        TeamitemModItems.REGISTRY.register(bus);
        WolfItem.register();
        AnimallModItems.REGISTRY.register(bus);
        AnimallModEntities.REGISTRY.register(bus);
        SssssEntity.register();
        PiglinEntity.register();
        TamemobModItems.REGISTRY.register(bus);
        Teleport.register();
      //  ParlToEye.register();
        FlyEvent.register();
        DisableAttack.register();
        TameslimeItem.register();

        bus.addListener(this::onClientSetup);
    }


    private void setup(final FMLCommonSetupEvent event) {

    }

    private void onClientSetup(FMLClientSetupEvent event) {

    }

}
