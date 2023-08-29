package useless.legacyui.Gui.Container;

import net.minecraft.core.player.inventory.*;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.player.inventory.slot.SlotArmor;
import net.minecraft.core.player.inventory.slot.SlotCrafting;
import useless.prismaticlibe.gui.slot.SlotResizable;

public class ContainerInventoryLegacy extends ContainerPlayer {
    public ContainerInventoryLegacy(InventoryPlayer inventoryplayer, InventoryCrafting craftMatrix, IInventory craftResult) {
        this(inventoryplayer, true, craftMatrix, craftResult);
    }
    public ContainerInventoryLegacy(InventoryPlayer inventory, boolean isSinglePlayer, InventoryCrafting craftMatrix, IInventory craftResult) {
        super(inventory, isSinglePlayer);
        inventorySlots.clear();
        this.craftMatrix = craftMatrix;
        this.craftResult = craftResult;
        int i;
        this.addSlot(new SlotCrafting(inventory.player, this.craftMatrix, this.craftResult, 0, 155, 11));
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new SlotResizable(this.craftMatrix, j + i * 2, 126 + j * 12, 8 + i * 12, 12));
            }
        }
        for (i = 0; i < 4; ++i) {
            int j1 = i;
            this.addSlot(new SlotArmor(this, inventory, inventory.getSizeInventory() - 1 - i, 8 + 41, 8 + i * 18, j1));
        }
        for (i = 0; i < 3; ++i) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(inventory, k1 + (i + 1) * 9, 8 + k1 * 18, 84 + 10 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142 + 10));
        }
        this.onCraftMatrixChanged(this.craftMatrix);

    }



}
