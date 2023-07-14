package uk.co.catlord.mods.nabsack;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class Utils {
  public static boolean isNabsackLike(ItemStack itemStack) {
    if (itemStack == null) {
      return false;
    }
    
    // TODO: Make the item(s) configurable and default item bundle
    // TODO: Handle item stacks with more than one item (like water buckets)
    return itemStack.getItem() == Items.BUNDLE;
  }

  public static boolean isFullNabsack(ItemStack itemStack) {
    if (!isNabsackLike(itemStack)) {
      return false;
    }

    return itemStack.getOrCreateNbt().contains("StoredEntity");
  }

  public static boolean isEmptyNabsack(ItemStack itemStack) {
    if (!isNabsackLike(itemStack)) {
      return false;
    }

    return !itemStack.getOrCreateNbt().contains("StoredEntity") && !itemStack.getOrCreateNbt().contains("Items");
  }

  public static boolean isServer(World world) {
    return world.getServer() != null;
  }
}
