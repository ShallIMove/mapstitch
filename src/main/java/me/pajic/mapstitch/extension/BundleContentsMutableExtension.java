package me.pajic.mapstitch.extension;

import net.minecraft.world.item.ItemStack;

public interface BundleContentsMutableExtension {
    void mapstitch$setIsAtlas();
	ItemStack mapstitch$removeOneItemAtIndex(int index);
	ItemStack mapstitch$removeOneStackOrdered(boolean filledMapsFirst);
}
