
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.animall.init;

import com.water.animall.Blaze.entity.TameBlazeEntity;
import com.water.animall.Cow.entity.TameCow;
import com.water.animall.Creeper.entity.SssssEntity;
import com.water.animall.Enderman.entity.TameEnderMan;
import com.water.animall.Ghast.entity.TameGhastEntity;
import com.water.animall.Gholem.entity.TameIronGolem;
import com.water.animall.Phantom.entity.TamePhantomEntity;
import com.water.animall.Slime.entity.TameSlimeEntity;
import com.water.animall.Spider.entity.TameSpiderEntity;
import com.water.main.Reference;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnimallModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);
	public static final RegistryObject<EntityType<SssssEntity>> SSSSS = register("sssssb",
			EntityType.Builder.<SssssEntity>of(SssssEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(SssssEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<TameSlimeEntity>> Slime = register("tameslime",
			EntityType.Builder.<TameSlimeEntity>of(TameSlimeEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameSlimeEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TamePhantomEntity>> PHANTOM = register("tamephantom",
			EntityType.Builder.<TamePhantomEntity>of(TamePhantomEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TamePhantomEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameBlazeEntity>> BLAZE = register("tameblaze",
			EntityType.Builder.<TameBlazeEntity>of(TameBlazeEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameBlazeEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameEnderMan>> ENDERMAN = register("tameender",
			EntityType.Builder.<TameEnderMan>of(TameEnderMan::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameEnderMan::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameGhastEntity>> GHAST = register("tameghast",
			EntityType.Builder.<TameGhastEntity>of(TameGhastEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameGhastEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameSpiderEntity>> SPIDER = register("tamespider",
			EntityType.Builder.<TameSpiderEntity>of(TameSpiderEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameSpiderEntity::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameIronGolem>> GOLEM = register("tamegolem",
			EntityType.Builder.<TameIronGolem>of(TameIronGolem::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameIronGolem::new)

					.sized(0.6f, 1.8f));

	public static final RegistryObject<EntityType<TameCow>> COW = register("tamecow",
			EntityType.Builder.<TameCow>of(TameCow::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(3).setCustomClientFactory(TameCow::new)

					.sized(0.6f, 1.8f));
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			SssssEntity.init();
			TameSlimeEntity.init();
			TamePhantomEntity.init();
			TameBlazeEntity.init();
			TameEnderMan.init();
			TameGhastEntity.init();
			TameSpiderEntity.init();
			TameIronGolem.init();
			TameCow.init();

		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(SSSSS.get(), SssssEntity.createAttributes().build());
		event.put(Slime.get(), TameSlimeEntity.createAttributes().build());
		event.put(PHANTOM.get(), TamePhantomEntity.createAttributes().build());
		event.put(BLAZE.get(), TameBlazeEntity.createAttributes().build());
		event.put(ENDERMAN.get(), TameEnderMan.createAttributes().build());
		event.put(GHAST.get(), TameGhastEntity.createAttributes().build());
		event.put(SPIDER.get(), TameSpiderEntity.createAttributes().build());
		event.put(GOLEM.get(), TameIronGolem.createAttributes().build());
		event.put(COW.get(), TameCow.createAttributes().build());

	}
}
