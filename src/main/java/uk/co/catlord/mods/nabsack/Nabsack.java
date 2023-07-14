package uk.co.catlord.mods.nabsack;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;

public class Nabsack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nabsack");

	@Override
	public void onInitialize() {
		LOGGER.info("Starting Nabsack mod...");
		
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!Utils.isServer(world)) {
				return ActionResult.PASS;
			}

			LOGGER.info("Entity used: " + entity.toString());

			ItemStack itemStack = player.getStackInHand(hand);

			if (!Utils.isEmptyNabsack(itemStack)) {
				return ActionResult.PASS;
			}

			LOGGER.info("Nabsack used");

			NbtCompound nabsackTag = itemStack.getOrCreateNbt();
			NbtCompound entityTag = new NbtCompound();

			if (nabsackTag.contains("StoredEntity")) {
				return ActionResult.FAIL;
			}

			boolean entitySaved = entity.saveSelfNbt(entityTag);

			if (!entitySaved) {
				LOGGER.error("Entity could not be saved");
				return ActionResult.FAIL;
			}

			nabsackTag.put("StoredEntity", entityTag);

			NbtCompound spawnEggItemTag = new NbtCompound();
			spawnEggItemTag.putString("id", "minecraft:sheep_spawn_egg");
			spawnEggItemTag.putByte("Count", (byte) 1);
			NbtList itemsList = new NbtList();
			itemsList.add(spawnEggItemTag);

			nabsackTag.put("Items", itemsList);


			LOGGER.info("Entity saved to Nabsack");

			entity.remove(RemovalReason.DISCARDED);

			return ActionResult.SUCCESS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!Utils.isServer(world)) {
				return ActionResult.PASS;
			}

			LOGGER.info("Block used: " + hitResult.toString());

			ItemStack itemStack = player.getStackInHand(hand);

			if (!Utils.isNabsackLike(itemStack)) {
				return ActionResult.PASS;
			}

			LOGGER.info("Nabsack used");

			NbtCompound nabsackTag = itemStack.getOrCreateNbt();

			if (!nabsackTag.contains("StoredEntity")) {
				return ActionResult.FAIL;
			}

			NbtCompound entityTag = nabsackTag.getCompound("StoredEntity");

			EntityType<?> type = EntityType.get(entityTag.getString("id")).orElse(null);

			LOGGER.info("Entity type: " + type.toString());

			BlockPos spawnAt = new BlockPos(
				(int) Math.floor(hitResult.getPos().x),
				(int) Math.ceil(hitResult.getPos().y),
				(int) Math.floor(hitResult.getPos().z)
			);

			Entity spawnedEntity = type.spawn(
				world.getServer().getWorld(world.getRegistryKey()),
				null,
				null,
				spawnAt,
				SpawnReason.NATURAL,
				true,
				false
			);

			try {
				spawnedEntity.getClass().getMethod("readCustomDataFromNbt", NbtCompound.class).invoke(spawnedEntity, entityTag);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				LOGGER.error("Could not read custom data from NBT");
				e.printStackTrace();
			}

			if (spawnedEntity == null) {
				LOGGER.error("Entity could not be spawned from nabsack");
				return ActionResult.FAIL;
			}

			LOGGER.info("Entity spawned from Nabsack");

			nabsackTag.remove("StoredEntity");
			nabsackTag.remove("Items");

			return ActionResult.SUCCESS;
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack itemStack = player.getStackInHand(hand);

			if (!Utils.isServer(world)) {
				return TypedActionResult.pass(itemStack);
			}

			LOGGER.info("Item used");

			if (!Utils.isFullNabsack(itemStack)) {
				return TypedActionResult.pass(itemStack);
			}

			return TypedActionResult.fail(itemStack);
		});
	}
}
