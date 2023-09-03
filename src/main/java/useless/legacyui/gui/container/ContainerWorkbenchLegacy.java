package useless.legacyui.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.achievement.stat.StatFileWriter;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Block;
import net.minecraft.core.crafting.CraftingManager;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.*;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotCrafting;
import net.minecraft.core.world.World;
import useless.legacyui.ConfigTranslations;
import useless.prismaticlibe.gui.slot.SlotCraftingDisplay;
import useless.prismaticlibe.gui.slot.SlotResizable;
import useless.legacyui.LegacyUI;
import useless.legacyui.sorting.RecipeGroup;
import useless.legacyui.sorting.SortingCategory;
import useless.legacyui.utils.InventoryUtil;
import useless.legacyui.sorting.RecipeCost;

import java.util.List;

public class ContainerWorkbenchLegacy extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private final World field_20133_c;
    private int x;
    private int y;
    private int z;
    private InventoryPlayer inventoryPlayer;
    private final boolean isInInventory;
    private int craftingSlotsNumber = 0;
    private int inventorySlotsNumber = 0;

    private int getTotalSlots(){
        return craftingSlotsNumber + inventorySlotsNumber;
    }

    public ContainerWorkbenchLegacy(InventoryPlayer inventoryplayer, World world, int i, int j, int k) {
        this.field_20133_c = world;
        this.x = i;
        this.y = j;
        this.z = k;
        this.inventoryPlayer = inventoryplayer;
        this.isInInventory = false;
    }
    public ContainerWorkbenchLegacy(InventoryPlayer inventoryplayer) {
        this.field_20133_c = null;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.inventoryPlayer = inventoryplayer;
        this.isInInventory = true;
    }
    public void craftingSlots() {
        this.addSlot(new SlotCrafting(this.inventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 107, 127)); craftingSlotsNumber++;
        int baseIterator;
        int subIterator;

        if (isInInventory){
            // 2x2 Crafting
            for (baseIterator = 0; baseIterator < 2; ++baseIterator) {
                for (subIterator = 0; subIterator < 2; ++subIterator) {
                    this.addSlot(new Slot(this.craftMatrix, subIterator + baseIterator * 3, 29 + subIterator * 18, 118 + baseIterator * 18)); craftingSlotsNumber++;
                }
            }
        }
        else {
            // 3x3 Crafting
            for (baseIterator = 0; baseIterator < 3; ++baseIterator) {
                for (subIterator = 0; subIterator < 3; ++subIterator) {
                    this.addSlot(new Slot(this.craftMatrix, subIterator + baseIterator * 3, 20 + subIterator * 18, 109 + baseIterator * 18)); craftingSlotsNumber++;
                }
            }
        }


        // 3x9 inventory
        for (baseIterator = 0; baseIterator < 3; ++baseIterator) {
            for (subIterator = 0; subIterator < 9; ++subIterator) {
                this.addSlot(new SlotResizable(this.inventoryPlayer, subIterator + baseIterator * 9 + 9, 153 + subIterator * 12, 112 + baseIterator * 12, 12)); inventorySlotsNumber++;
            }
        }

        // 1x9 hotbar
        for (baseIterator = 0; baseIterator < 9; ++baseIterator) {
            this.addSlot(new SlotResizable(this.inventoryPlayer, baseIterator, 153 + baseIterator * 12, 154, 12)); inventorySlotsNumber++;
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }
    public void setRecipes(EntityPlayer player, SortingCategory category, StatFileWriter statWriter, int currentSlotId, int currentScrollAmount, boolean showCraftingPreview) {
        for (RecipeGroup group : category.getRecipeGroups(isInInventory)){
            LegacyUI.LOGGER.debug("CategoryGroup: " + group.getContainer(0, isInInventory).inventorySlots.get(0).getStack().getItem().getKey());
        }
        LegacyUI.LOGGER.debug("Category: " + category + " | slotId: " + currentSlotId + " | currentScroll: " + currentScrollAmount + " | craftPreview: " + showCraftingPreview);
        this.inventorySlots.clear();
        craftingSlots();

        RecipeGroup[] craftingGroups = category.getRecipeGroups(isInInventory);
        boolean discovered;
        boolean highlighted;
        ItemStack item;

        int index = 0;
        for (RecipeGroup group : craftingGroups){
        ContainerGuidebookRecipeCrafting currentContainer;
        boolean craftable;
            if (index == currentSlotId){ // special rendering for scrolling and recipe preview
                currentContainer = group.getContainer(currentScrollAmount, isInInventory);
                craftable = canCraft(player, new RecipeCost(currentContainer));

                // Recipebar preview
                item = currentContainer.inventorySlots.get(0).getStack();
                discovered = isDicovered(item, statWriter, player);
                this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 12 + 18 * index, 56, currentContainer.inventorySlots.get(0).getStack(), discovered || craftable, !craftable, LegacyUI.getHighlightColor()));

                if (group.getRecipes(isInInventory).length > 1) { // If multiple items in recipe group
                    int idUpper = currentScrollAmount + 1; // Next item in group
                    int idLower = currentScrollAmount - 1; // Last item in group

                    // Next item preview
                    item = group.getContainer(idUpper, isInInventory).inventorySlots.get(0).getStack();
                    discovered = isDicovered(item, statWriter, player);
                    craftable = canCraft(player, new RecipeCost(group.getContainer(idUpper, isInInventory)));
                    this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 12 + 18 * index, 56 + 21, item, discovered || craftable, !craftable, LegacyUI.getHighlightColor()));

                    // Previous item preview
                    item = group.getContainer(idLower, isInInventory).inventorySlots.get(0).getStack();
                    discovered = isDicovered(item, statWriter, player);
                    craftable = canCraft(player, new RecipeCost(group.getContainer(idLower, isInInventory)));
                    this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 12 + 18 * index, 56 - 21, item, discovered || craftable, !craftable, LegacyUI.getHighlightColor()));

                }

                // Crafting table result preview
                if (showCraftingPreview){
                    RecipeCost cost = new RecipeCost(currentContainer);
                    craftable = canCraft(player, cost);
                    item = currentContainer.inventorySlots.get(0).getStack();
                    discovered = isDicovered(item, statWriter, player);
                    this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 103, 123, item, discovered || craftable, !craftable, LegacyUI.getHighlightColor(), 26));

                    for (int j = 1; j < currentContainer.inventorySlots.size(); j++) {
                        item = currentContainer.inventorySlots.get(j).getStack();
                        discovered = isDicovered(item, statWriter, player);

                        int k = 0;
                        if (item != null){
                            for (int i = 0; i < cost.itemStacks.length; i++){
                                if (cost.itemStacks[i].getItem() == item.getItem()){
                                    cost.quantity[i] -= 1;
                                    k = i;
                                    break;
                                }
                            }
                        }

                        if (currentContainer.inventorySlots.size() > 5){
                            // Render 3x3 crafting grid
                            this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 20 + 18 * ((j - 1) % 3), 109 + 18 * ((j - 1) / 3), item, discovered, InventoryUtil.itemsInInventory(inventoryPlayer, item, cost.useAlts) <= cost.quantity[k] && item != null, LegacyUI.getHighlightColor()));
                        }
                        else {
                            // Render 2x2 crafting gird
                            this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 29 + 18 * ((j - 1) % 2), 118 + 18 * ((j - 1) / 2), item, discovered, InventoryUtil.itemsInInventory(inventoryPlayer, item, cost.useAlts) <= cost.quantity[k] && item != null, LegacyUI.getHighlightColor()));
                        }


                    }
                }
            }
            else { // Renders first slot of none selected groups
                item = group.getContainer(0, isInInventory).inventorySlots.get(0).getStack();
                discovered = isDicovered(item, statWriter, player);
                craftable = canCraft(player, new RecipeCost(group.getContainer(0, isInInventory)));
                this.addSlot(new SlotCraftingDisplay(this.inventorySlots.size(), 12 + 18 * index, 56, item, discovered || craftable, !craftable, LegacyUI.getHighlightColor()));
            }

            index++;
        }

    }
    public boolean craft(Minecraft mc, int windowId, SortingCategory category, int currentSlotId, int currentScrollAmount){
        ContainerGuidebookRecipeCrafting recipe = category.getRecipeGroups(isInInventory)[currentSlotId].getContainer(currentScrollAmount, isInInventory);
        RecipeCost recipeCost = new RecipeCost(recipe);

        if (canCraft(mc.thePlayer, recipeCost)){
            for (int i = 1; i < recipe.inventorySlots.size(); i++){
                ItemStack itemStack = recipe.inventorySlots.get(i).getStack();
                if (itemStack != null){
                    int slotId = InventoryUtil.findStackIndex(mc.thePlayer.inventory.mainInventory, itemStack, recipeCost.useAlts); // Finds slot index of an inventory slot with a desired item
                    if (slotId == -1) {continue;}
                    if (slotId < 9){ slotId += 36;}



                    if (recipe.inventorySlots.size() > 5 || isInInventory){// 3x3 crafting
                        int offset = isInInventory ? -4:1;
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_LEFT, new int[]{slotId + offset}, mc.thePlayer); // Picks up stack
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_RIGHT, new int[]{i}, mc.thePlayer); // Places one item
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_LEFT, new int[]{slotId + offset}, mc.thePlayer); // Puts down stack
                    }
                    else {// 2x2 crafting
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_LEFT, new int[]{slotId + 1}, mc.thePlayer); // Picks up stack
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_RIGHT, new int[]{i + (i/3)}, mc.thePlayer); // Places one item
                        mc.playerController.doInventoryAction(windowId, InventoryAction.CLICK_LEFT, new int[]{slotId + 1}, mc.thePlayer); // Puts down stack
                    }
                }
            }
            mc.playerController.doInventoryAction(windowId, InventoryAction.MOVE_STACK, new int[]{0}, mc.thePlayer);
            return true; // Craft succeeded
        }
        return false; // Craft failed
    }
    private boolean canCraft(EntityPlayer player, RecipeCost cost){
        boolean canCraft = true;
        for (int i = 0; i < cost.itemStacks.length; i++){
            canCraft = canCraft && InventoryUtil.itemsInInventory(player.inventory, cost.itemStacks[i], cost.useAlts) >= cost.quantity[i];
        }
        return canCraft;
    }
    private boolean isDicovered(ItemStack item, StatFileWriter statWriter, EntityPlayer player){
        if (!LegacyUI.config.getBoolean(ConfigTranslations.CRAFTING_HIDE_UNDISCOVERED.getKey())){
            return true;
        }
        if (player.getGamemode() == Gamemode.creative) {
            return true;
        }
        if (item == null) {
            return false;
        } else {
            return statWriter.readStat(StatList.pickUpItemStats[item.itemID]) > 0;
        }
    }
    private boolean isHighlighted(ItemStack item, EntityPlayer player){
        return item != null;
    }
    public void onCraftMatrixChanged(IInventory iinventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
    }
    public void onCraftGuiClosed(EntityPlayer player) {
        super.onCraftGuiClosed(player);
        boolean insert = false;

        for (int i = 0; i < 9; ++i) {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            if (itemstack != null) {
                this.storeOrDropItem(player, itemstack);
                insert = true;
            }
        }

        if (insert) {
            player.world.playSoundAtEntity(player, "random.insert", 0.1F, 1.0F);
        }

    }
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        if (isInInventory) {return true;}
        if (this.field_20133_c.getBlockId(this.x, this.y, this.z) != Block.workbench.id) {
            return false;
        } else {
            return entityplayer.distanceToSqr((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
        }
    }
    public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
        int offset = isInInventory ? -5:0;
        if (slot.id == 0) {
            return this.getSlots(0, 1, false);
        } else if (slot.id >= 1 && slot.id < 9 + offset) {
            return this.getSlots(1, 9 + offset, false);
        } else {
            if (action == InventoryAction.MOVE_SIMILAR) {
                if (slot.id >= 10 + offset && slot.id <= 45 + offset) {
                    return this.getSlots(10 + offset, 36, false);
                }
            } else {
                if (slot.id >= 10 + offset && slot.id <= 36 + offset) {
                    return this.getSlots(10 + offset, 27, false);
                }

                if (slot.id >= 37 + offset && slot.id <= 45 + offset) {
                    return this.getSlots(37 + offset, 9, false);
                }
            }

            return null;
        }
    }
    public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, EntityPlayer player) {
        int offset = isInInventory ? -5:0;
        if (slot.id >= 10 + offset && slot.id <= 45 + offset) { // Inventory
            if (target == 1) {
                return this.getSlots(1, craftingSlotsNumber-1, false);
            } else if (slot.id <= 36 + offset) {
                return this.getSlots(37 + offset, 9, false);
            } else {
                return this.getSlots(10 + offset, 27, false);
            }
        } else if (slot.id < 10 + offset) { // Crafting
            return slot.id == 0 ? this.getSlots(10 + offset, 36, true) : this.getSlots(10 + offset, 36, false);
        } else {
            return null;
        }
    }
    public void handleHotbarSwap(int[] args, EntityPlayer player) {
        // Dont hotbar swap the crafting guide!
        if (args[0] > getTotalSlots()) {
            return;
        }

        if (args.length < 2) {
            return;
        }
        int hotbarSlotNumber = args[1];
        if (hotbarSlotNumber < 1 || hotbarSlotNumber > 9) {
            return;
        }
        Slot slot = this.getSlot(args[0]);
        Slot hotbarSlot = this.getSlot(this.getHotbarSlotId(hotbarSlotNumber));
        if (hotbarSlot == null || slot == hotbarSlot) {
            return;
        }
        ItemStack slotStack = slot.getStack();
        ItemStack hotbarStack = hotbarSlot.getStack();
        if (slotStack != null) {
            slot.putStack(null);
            slot.onPickupFromSlot(slotStack);
        }
        if (hotbarStack != null) {
            hotbarSlot.putStack(null);
            hotbarSlot.onPickupFromSlot(hotbarStack);
        }
        this.mergeItems(slotStack, hotbarSlot.id);
        this.storeOrDropItem(player, slotStack);
        this.mergeItems(hotbarStack, slot.id);
        this.storeOrDropItem(player, hotbarStack);
        slot.onSlotChanged();
        hotbarSlot.onSlotChanged();
    }
    public int getHotbarSlotId(int number) {
        // - 14 to account for the 14 display items
        return craftingSlotsNumber + inventorySlotsNumber + number - 9;
    }


}
