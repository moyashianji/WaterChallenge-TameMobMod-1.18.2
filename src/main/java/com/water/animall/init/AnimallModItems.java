
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.animall.init;

import com.water.main.Reference;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;


public class AnimallModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final RegistryObject<Item> SSSSS = REGISTRY.register("sssss_spawn_egg",
			() -> new ForgeSpawnEggItem(AnimallModEntities.SSSSS, -1, -1, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
