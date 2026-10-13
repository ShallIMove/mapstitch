package me.pajic.mapstitch.recipe;

import me.pajic.mapstitch.item.ModItems;
import me.pajic.mapstitch.pin.PinUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//? >=26.1 {
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
*///?}

public class AtlasPinBulkDyeRecipe extends CustomRecipe {

    //? >=26.1 {
    public static final AtlasPinBulkDyeRecipe INSTANCE = new AtlasPinBulkDyeRecipe();
    public static final MapCodec<AtlasPinBulkDyeRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, AtlasPinBulkDyeRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AtlasPinBulkDyeRecipe() {
        super();
    }
    //?} else {
    /*public AtlasPinBulkDyeRecipe(CraftingBookCategory category) {
        super(category);
    }
    *///?}

    private record Contents(ItemStack target, int targetCount, List<DyeColor> dyes) {}

    private static @Nullable Contents parse(CraftingInput input) {
        ItemStack target = ItemStack.EMPTY;
        int targets = 0;
        List<DyeColor> dyes = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(ModItems.ATLAS_PIN)) {
                if (target.isEmpty()) target = stack;
                else if (!ItemStack.isSameItemSameComponents(stack, target)) return null;
                targets++;
            } else {
                DyeColor dye = dyeColor(stack);
                if (dye == null) return null;
                dyes.add(dye);
            }
        }
        return targets >= 2 && !dyes.isEmpty() ? new Contents(target, targets, dyes) : null;
    }

    private static @Nullable DyeColor dyeColor(ItemStack stack) {
        //? >=26.1 {
        return stack.is(ItemTags.DYES) ? stack.getOrDefault(DataComponents.DYE, DyeColor.WHITE) : null;
        //?} else {
        /*return stack.getItem() instanceof DyeItem dyeItem ? dyeItem.getDyeColor() : null;
        *///?}
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return parse(input) != null;
    }

    @Override
    //? >=26.1 {
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
    //?} else {
    /*public @NotNull ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
    *///?}
        Contents contents = parse(input);
        if (contents == null) return ItemStack.EMPTY;
        ItemStack result = contents.target().copyWithCount(contents.targetCount());
        PinUtil.setColor(result, PinUtil.mixColor(contents.target(), contents.dyes()));
        return result;
    }

    //? <26.1 {
    /*@Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }
    *///?}

    @Override
    public @NotNull RecipeSerializer<AtlasPinBulkDyeRecipe> getSerializer() {
        return ModRecipes.ATLAS_PIN_BULK_DYE;
    }
}
