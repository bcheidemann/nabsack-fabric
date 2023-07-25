package uk.co.catlord.mods.nabsack;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class Utils {
  public static boolean isFullNabsack(ItemStack itemStack) {
    return itemStack.getItem() == Items.SHEEP_SPAWN_EGG && itemStack.getOrCreateNbt().contains("StoredEntity");
  }

  public static boolean isEmptyNabsack(ItemStack itemStack) {
    return itemStack.getItem() == Items.BUNDLE;
  }

  public static boolean isServer(World world) {
    return world.getServer() != null;
  }
}
