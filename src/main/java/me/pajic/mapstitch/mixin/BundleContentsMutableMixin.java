package me.pajic.mapstitch.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.pajic.mapstitch.extension.BundleContentsMutableExtension;
import me.pajic.mapstitch.item.AtlasItem;
import me.pajic.mapstitch.mixin.accessor.BundleContentsAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

//~ if <26.1 'getWeight(stack).getOrThrow()' -> 'getWeight(stack)' {
@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsMutableMixin implements BundleContentsMutableExtension {

    @Shadow @Final private List<ItemStack> items;
    @Shadow private Fraction weight;
    @Shadow @Nullable public abstract ItemStack removeOne();

    @Unique private boolean mapstitch$isAtlas = false;

    @Override
    public void mapstitch$setIsAtlas() {
        mapstitch$isAtlas = true;
    }

    @Override
    public ItemStack mapstitch$removeOneItemAtIndex(int index) {
        if (!items.isEmpty()) {
            ItemStack stack = items.get(index).copy();
            ItemStack removed = stack.split(1);
            weight = weight.subtract(BundleContentsAccessor.getWeight(stack).getOrThrow().multiplyBy(Fraction.getFraction(stack.getCount(), 1)));
            if (stack.isEmpty()) items.remove(index);
            else items.set(index, stack);
            return removed;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack mapstitch$removeOneStackOrdered(boolean filledMapsFirst) {
        if (!items.isEmpty()) {
            if (filledMapsFirst) {
                int filledMapIndex = -1;
                for (int i = 0; i < items.size(); i++) {
                    ItemStack stack = items.get(i);
                    if (stack.is(Items.FILLED_MAP)) {
                        filledMapIndex = i;
                        break;
                    }
                }
                if (filledMapIndex == -1) return removeOne();
                else {
                    ItemStack stack = items.remove(filledMapIndex).copy();
                    weight = weight.subtract(BundleContentsAccessor.getWeight(stack).getOrThrow().multiplyBy(Fraction.getFraction(stack.getCount(), 1)));
                    return stack;
                }
            } else {
                int emptyMapIndex = -1;
                for (int i = 0; i < items.size(); i++) {
                    ItemStack stack = items.get(i);
                    if (stack.is(Items.MAP) || stack.is(Items.PAPER)) {
                        emptyMapIndex = i;
                        break;
                    }
                }
                if (emptyMapIndex == -1) return removeOne();
                else {
                    ItemStack stack = items.remove(emptyMapIndex).copy();
                    weight = weight.subtract(BundleContentsAccessor.getWeight(stack).getOrThrow().multiplyBy(Fraction.getFraction(stack.getCount(), 1)));
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @ModifyExpressionValue(
            method = "getMaxAmountToAdd",
            at = @At(
                    value = "FIELD",
                    target = "Lorg/apache/commons/lang3/math/Fraction;ONE:Lorg/apache/commons/lang3/math/Fraction;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private Fraction increaseCapacity(Fraction original) {
        return mapstitch$isAtlas ? AtlasItem.getMaxSize() : original;
    }

    @WrapOperation(
            method = "findStackIndex",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean checkStackSizeLimit(ItemStack a, ItemStack b, Operation<Boolean> original) {
        boolean result = original.call(a, b);
        if (mapstitch$isAtlas) return result && a.getCount() + b.getCount() <= a.getMaxStackSize();
        return result;
    }
}
//~}
