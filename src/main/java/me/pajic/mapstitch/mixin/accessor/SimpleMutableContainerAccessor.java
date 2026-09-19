package me.pajic.mapstitch.mixin.accessor;

//? >26.2 {

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SimpleMutableContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SimpleMutableContainer.class)
public interface SimpleMutableContainerAccessor {

    @Accessor("items")
    List<ItemStack> mapstitch$getItems();
}
//?}
