
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.water.tamemobitem.init;

import com.water.main.Reference;
import com.water.tamemobitem.item.*;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;



public class TamemobModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final RegistryObject<Item> TAMEFHANTOM = REGISTRY.register("tamefhantom", () -> new TamefhantomItem());
	public static final RegistryObject<Item> TAMESLIME = REGISTRY.register("tameslime", () -> new TameslimeItem());
	public static final RegistryObject<Item> TAMEBLAZE = REGISTRY.register("tameblaze", () -> new TameblazeItem());
	public static final RegistryObject<Item> TAMEENDER = REGISTRY.register("tameender", () -> new TameenderItem());
	public static final RegistryObject<Item> TAMEGHAST = REGISTRY.register("tameghast", () -> new TameghastItem());
	public static final RegistryObject<Item> TAMEPIGLIN = REGISTRY.register("tamepiglin", () -> new TamepiglinItem());
	public static final RegistryObject<Item> TAMESPIDER = REGISTRY.register("tamespider", () -> new TamespiderItem());
	public static final RegistryObject<Item> TAMECREEPER = REGISTRY.register("tamecreeper", () -> new TamecreeperItem());
	public static final RegistryObject<Item> TAMEGOLEM = REGISTRY.register("tamegolem", () -> new TamegolemItem());
	public static final RegistryObject<Item> TAMECOW = REGISTRY.register("tamecow", () -> new TamecowItem());
	public static final RegistryObject<Item> TAMEWOLF = REGISTRY.register("tamewolf", () -> new TamewolfItem());
}
