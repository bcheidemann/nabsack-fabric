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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Nabsack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nabsack");

	@Override
	public void onInitialize() {
		LOGGER.info("Starting Nabsack mod...");
		
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!Utils.isServer(world)) {
				return ActionResult.PASS;
			}

			ItemStack itemStack = player.getStackInHand(hand);

			if (Utils.isEmptyNabsack(itemStack)) {
				return handleUseNabsackOnEntity(player, world, hand, entity, hitResult, itemStack);
			}

			if (Utils.isFullNabsack(itemStack)) {
				return ActionResult.FAIL;
			}

			return ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!Utils.isServer(world)) {
				return ActionResult.PASS;
			}

			ItemStack itemStack = player.getStackInHand(hand);

			if (Utils.isFullNabsack(itemStack)) {
				return handleUseNabsackOnBlock(player, world, hand, hitResult, itemStack);
			}
			
			return ActionResult.PASS;
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!Utils.isServer(world)) {
				return TypedActionResult.pass(null);
			}

			ItemStack itemStack = player.getStackInHand(hand);

			if (Utils.isFullNabsack(itemStack)) {
				return TypedActionResult.fail(null);
			}

			return TypedActionResult.pass(null);
		});
	}

	private ActionResult handleUseNabsackOnEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult, ItemStack handItemStack) {
		if (handItemStack.getOrCreateNbt().contains("Items")) {
			return ActionResult.PASS;
		}

		ItemStack fullNabsack = new ItemStack(Items.SHEEP_SPAWN_EGG);
		fullNabsack.setCustomName(entity.getName());
		NbtCompound nabsackTag = fullNabsack.getOrCreateNbt();
		NbtCompound entityTag = new NbtCompound();

		boolean entitySaved = entity.saveSelfNbt(entityTag);

		if (!entitySaved) {
			LOGGER.error("Entity could not be saved");
			return ActionResult.FAIL;
		}

		nabsackTag.put("StoredEntity", entityTag);
		player.setStackInHand(hand, fullNabsack);

		entity.remove(RemovalReason.DISCARDED);

		return ActionResult.SUCCESS;
	}

	private ActionResult handleUseNabsackOnBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult, ItemStack handItemStack) {
		NbtCompound nabsackTag = handItemStack.getOrCreateNbt();
		NbtCompound entityTag = nabsackTag.getCompound("StoredEntity");

		EntityType<?> type = EntityType.get(entityTag.getString("id")).orElse(null);

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

		// Replace the nabsack with an empty one (bundle)
		player.setStackInHand(hand, new ItemStack(Items.BUNDLE));

		return ActionResult.SUCCESS;
	}
}
