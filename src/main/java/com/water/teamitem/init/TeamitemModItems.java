
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.teamitem.init;

import com.water.main.Reference;
import com.water.teamitem.item.WolfItem;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;


public class TeamitemModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final RegistryObject<Item> WOLF = REGISTRY.register("wolf", () -> new WolfItem());
}
