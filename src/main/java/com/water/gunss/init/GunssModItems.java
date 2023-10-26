
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.gunss.init;

import com.water.gunss.item.GunItem;
import com.water.main.Reference;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;


public class GunssModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final RegistryObject<Item> GUN = REGISTRY.register("gun", () -> new GunItem());
}
