package me.pajic.mapstitch.pin;

import me.pajic.mapstitch.extension.BundleContentsMutableExtension;
import me.pajic.mapstitch.item.ModItems;
import me.pajic.mapstitch.util.ModUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemContainerContents;

//? >=26.1
import net.minecraft.world.item.ItemStackTemplate;
//? >26.2
import net.minecraft.util.Prediction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PinUtil {

    private static final int CONTAINER_SIZE = 27;
    public static final int DEFAULT_COLOR = 0xFFF9FFFE;

    public static int colorOf(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() & DEFAULT_COLOR : DEFAULT_COLOR;
    }

    public static boolean isPin(ItemStack stack, int color) {
        return stack.is(ModItems.ATLAS_PIN) && colorOf(stack) == color;
    }

    public static ItemStack createPin(int color) {
        ItemStack stack = new ItemStack(ModItems.ATLAS_PIN);
        setColor(stack, color);
        return stack;
    }

    public static Map<Integer, Integer> collectPinColors(Player player) {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) collect(inv.getItem(i), counts, true);
        return counts;
    }

    private static void collect(ItemStack stack, Map<Integer, Integer> counts, boolean allowContainers) {
        if (stack.isEmpty() || stack.is(ModItems.ATLAS)) return;
        if (stack.is(ModItems.ATLAS_PIN)) {
            counts.merge(colorOf(stack), stack.getCount(), Integer::sum);
            return;
        }
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            //~ if <26.1 'ItemStackTemplate' -> 'ItemStack'
            for (ItemStackTemplate entry : bundle.items()) {
                ItemStack s = entry/*? >=26.1 {*/.create()/*?}*/;
                if (s.is(ModItems.ATLAS_PIN)) counts.merge(colorOf(s), s.getCount(), Integer::sum);
            }
        }
        if (allowContainers) {
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            //~ if <26.1 'nonEmptyItemCopyStream' -> 'nonEmptyStream'
            if (container != null) container.nonEmptyItemCopyStream().forEach(s -> collect(s, counts, false));
        }
    }

    public static int countPins(Player player, int color) {
        return collectPinColors(player).getOrDefault(color, 0);
    }

    public static boolean consumePin(Player player, int color) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (isPin(stack, color)) {
                stack.shrink(1);
                inv.setChanged();
                return true;
            }
        }
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (removeFromContainer(inv.getItem(i), color, true)) {
                inv.setChanged();
                return true;
            }
        }
        return false;
    }

    private static boolean removeFromContainer(ItemStack stack, int color, boolean allowContainers) {
        if (stack.isEmpty() || stack.is(ModItems.ATLAS)) return false;

        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            int index = -1;
            int i = 0;
            //~ if <26.1 'ItemStackTemplate' -> 'ItemStack'
            for (ItemStackTemplate entry : bundle.items()) {
                if (isPin(entry/*? >=26.1 {*/.create()/*?}*/, color)) {
                    index = i;
                    break;
                }
                i++;
            }
            if (index != -1) {
                BundleContents.Mutable mutable = ModUtil.toMutable(bundle);
                ItemStack removed = ((BundleContentsMutableExtension) mutable).mapstitch$removeOneItemAtIndex(index);
                if (!removed.isEmpty()) {
                    stack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
                    return true;
                }
            }
        }

        if (allowContainers) {
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            if (container != null) {
                NonNullList<ItemStack> slots = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
                container.copyInto(slots);
                for (ItemStack s : slots) {
                    if (isPin(s, color)) {
                        s.shrink(1);
                        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(slots));
                        return true;
                    }
                }
                for (ItemStack s : slots) {
                    if (removeFromContainer(s, color, false)) {
                        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(slots));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void givePin(Player player, int color) {
        ItemStack stack = createPin(color);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false
                    //? >26.2
                    , Prediction.SERVER_ONLY
            );
        }
    }

    public static void setColor(ItemStack stack, int color) {
        if ((color & DEFAULT_COLOR) == DEFAULT_COLOR) stack.remove(DataComponents.DYED_COLOR);
        else stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color & DEFAULT_COLOR/*? <1.21.5 {*//*, true*//*?}*/));
    }

    public static int mixColor(ItemStack target, List<DyeColor> dyes) {
        int r = 0, g = 0, b = 0, maxSum = 0, count = 0;
        DyedItemColor existing = target.get(DataComponents.DYED_COLOR);
        if (existing != null) {
            int c = existing.rgb();
            int cr = c >> 16 & 0xFF, cg = c >> 8 & 0xFF, cb = c & 0xFF;
            r += cr; g += cg; b += cb;
            maxSum += Math.max(cr, Math.max(cg, cb));
            count++;
        }
        for (DyeColor dye : dyes) {
            int c = dye.getTextureDiffuseColor();
            int cr = c >> 16 & 0xFF, cg = c >> 8 & 0xFF, cb = c & 0xFF;
            r += cr; g += cg; b += cb;
            maxSum += Math.max(cr, Math.max(cg, cb));
            count++;
        }
        int ar = r / count, ag = g / count, ab = b / count;
        float avgMax = (float) maxSum / count;
        float max = Math.max(ar, Math.max(ag, ab));
        if (max > 0) {
            ar = (int) (ar * avgMax / max);
            ag = (int) (ag * avgMax / max);
            ab = (int) (ab * avgMax / max);
        }
        return ar << 16 | ag << 8 | ab;
    }
}
