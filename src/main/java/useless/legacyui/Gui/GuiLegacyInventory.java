package useless.legacyui.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.input.InputType;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.ContainerPlayer;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.sound.SoundType;
import net.minecraft.core.util.helper.DamageType;
import org.lwjgl.opengl.GL11;
import useless.legacyui.ConfigTranslations;
import useless.legacyui.GlobalOverrides;
import useless.legacyui.Gui.Container.ContainerInventoryLegacy;
import useless.prismaticlibe.gui.GuiAuditoryButtons;
import useless.prismaticlibe.gui.slot.SlotResizable;
import useless.legacyui.LegacyUI;

public class GuiLegacyInventory extends GuiInventory{
    protected EntityPlayer player;
    private DamageType hoveredDamageType;
    GuiTooltip guiTooltip;
    protected GuiAuditoryButtons craftingButton;
    public GuiLegacyInventory(EntityPlayer player) {
        super(player);
        mc = Minecraft.getMinecraft(this);
        this.player = player;
        this.guiTooltip = new GuiTooltip(mc);
        inventorySlots = new ContainerInventoryLegacy(player.inventory, ((ContainerPlayer)inventorySlots).craftMatrix, ((ContainerPlayer)inventorySlots).craftResult);
        armourButtonFloatX = 20 - 44;
        armourValuesFloat = 130 - 44 - 5;
        GlobalOverrides.currentGuiScreen = this;
    }

    public void initGui(){
        super.initGui();
        this.ySize = 176;
        craftingButton = new GuiAuditoryButtons(10, (width - xSize) / 2 + 138, (height - ySize) / 2 + 33, 20, 21, "");
        craftingButton.setMuted(true);
        craftingButton.visible = false;
        controlList.add(craftingButton);
    }
    protected void buttonPressed(GuiButton guibutton) {
        super.buttonPressed(guibutton);
        if (guibutton == craftingButton){
            LegacyUI.LOGGER.info("Craft Button Pressed");
            openCrafting();
        }

    }

    protected void openCrafting(){
        GlobalOverrides.armBackOverride();
        uiSound("legacyui.ui.press");
        mc.displayGuiScreen(new GuiLegacyCrafting(player));

    }
    public boolean getIsMouseOverSlot(Slot slot, int i, int j) {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        i -= k;
        j -= l;
        int slotSize = 16;
        if (slot instanceof SlotResizable) {
            slotSize = ((SlotResizable) slot).width;
        }
        return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + slotSize - 2 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + slotSize - 2 + 1;
    }

    public void drawScreen(int x, int y, float renderPartialTicks) {
        super.drawScreen(x, y, renderPartialTicks);
        int inventoryTex = this.mc.renderEngine.getTexture("/assets/legacyui/gui/legacyinventory.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(inventoryTex);
        if (craftingButton.isHovered(x , y)){
            this.drawTexturedModalRect(craftingButton.xPosition, craftingButton.yPosition, 177, 77, 20, 21);
        }
        else {
            this.drawTexturedModalRect(craftingButton.xPosition, craftingButton.yPosition, 177, 54, 20, 21);
        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer() {
        drawStringNoShadow(fontRenderer, "Inventory",8, 82, LegacyUI.getGuiLabelColor());
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float f) {
        int inventoryTex = this.mc.renderEngine.getTexture("/assets/legacyui/gui/legacyinventory.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(inventoryTex);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glPushMatrix();
        GL11.glTranslatef(j + 51 + 44, k + 75 - 2, 50.0f);
        float f1 = 30.0f;
        GL11.glScalef(-f1, f1, f1);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        float f2 = this.mc.thePlayer.renderYawOffset;
        float f3 = this.mc.thePlayer.yRot;
        float f4 = this.mc.thePlayer.xRot;
        float f5 = (float)(j + 94) - this.xSize_lo; // Controls where the character looks
        float f6 = (float)(k + 75 - 50) - this.ySize_lo; // Controls where the character looks
        GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
        Lighting.enableLight();
        GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-((float)Math.atan(f6 / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        this.mc.thePlayer.renderYawOffset = (float)Math.atan(f5 / 40.0f) * 20.0f;
        this.mc.thePlayer.yRot = (float)Math.atan(f5 / 40.0f) * 40.0f;
        this.mc.thePlayer.xRot = -((float)Math.atan(f6 / 40.0f)) * 20.0f;
        this.mc.thePlayer.entityBrightness = 1.0f;
        GL11.glTranslatef(0.0f, this.mc.thePlayer.heightOffset, 0.0f);
        EntityRenderDispatcher.instance.viewLerpYaw = 180.0f;
        EntityRenderDispatcher.instance.renderEntityWithPosYaw(this.mc.thePlayer, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        this.mc.thePlayer.entityBrightness = 0.0f;
        this.mc.thePlayer.renderYawOffset = f2;
        this.mc.thePlayer.yRot = f3;
        this.mc.thePlayer.xRot = f4;
        GL11.glPopMatrix();
        Lighting.disable();
        GL11.glDisable(32826);

        if (this.mc.inputType == InputType.CONTROLLER) {
            int spacing = 14;
            int buttonsOffset = 2;
            int aX = 50;
            drawStringNoShadow(fontRenderer, "Take all", aX + spacing, this.height - 24, 0xFFFFFF);
            int promptASize = fontRenderer.getStringWidth("Take all");

            int bX = aX + spacing + promptASize;
            drawStringNoShadow(fontRenderer, "Exit", bX + spacing, this.height - 24, 0xFFFFFF);
            int promptBSize = fontRenderer.getStringWidth("Exit");

            int xX = bX + spacing + promptBSize;
            drawStringNoShadow(fontRenderer, "Take half", xX + spacing, this.height - 24, 0xFFFFFF);
            int promptXSize = fontRenderer.getStringWidth("Take half");

            int yX = xX + spacing + promptXSize;
            drawStringNoShadow(fontRenderer, "Quick move", yX + spacing, this.height - 24, 0xFFFFFF);
            int promptYSize = fontRenderer.getStringWidth("Quick move");

            int rbX = yX + spacing + promptYSize;
            drawStringNoShadow(fontRenderer, "What's this?", rbX + spacing + 7, this.height - 24, 0xFFFFFF);
            int promptRBSize = fontRenderer.getStringWidth("What's this?");

            int controllerTexture = this.mc.renderEngine.getTexture("/assets/legacyui/gui/xbox360.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(controllerTexture);
            GL11.glScaled(.5D, .5D, .5D);

            this.drawTexturedModalRect((aX + buttonsOffset) * 2, (this.height - 26) * 2, 0, 0, 20, 20);
            this.drawTexturedModalRect((bX + buttonsOffset) * 2, (this.height - 26) * 2, 20, 0, 20, 20);
            this.drawTexturedModalRect((xX + buttonsOffset) * 2, (this.height - 26) * 2, 40, 0, 20, 20);
            this.drawTexturedModalRect((yX + buttonsOffset) * 2, (this.height - 26) * 2, 60, 0, 20, 20);
            this.drawTexturedModalRect((rbX + buttonsOffset) * 2, (this.height - 26) * 2, 128, 0, 34, 21);

            GL11.glScaled(2, 2, 2);
        }
    }
    public void drawProtectionOverlay(int mouseX, int mouseY) {
        this.hoveredDamageType = null;
        int x = this.width / 2 - this.armourValuesFloat - 4;
        int y = this.height / 2 - 79;
        int w = 44;
        int h = 44;
        GL11.glEnable(3042);
        this.drawGradientRect(x, y, x + w, y + h, this.protectionOverlayBgColor.getARGB(), this.protectionOverlayBgColor.getARGB());
        int iconsTex = this.mc.renderEngine.getTexture("/gui/icons.png");
        this.mc.renderEngine.bindTexture(iconsTex);
        GL11.glDisable(3042);
        GL11.glDisable(2884);
        int w2 = 26;
        int x2 = x;
        int h2 = 4;
        int i = 0;
        for (DamageType damageType : DamageType.values()) {
            if (damageType.display()) {
                int y2 = y + i * 10;
                float protection = this.mc.thePlayer.inventory.getTotalProtectionAmount(damageType);
                if (protection > 1.0f) {
                    protection = 1.0f;
                }
                int l = (int)(protection * 255.0f);
                int color = 255 - l << 16 | l << 8 | 0xFF000000;
                GL11.glEnable(3553);
                GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                this.drawTexturedModalRect(x2 + 2, y2 + 2, 0, 16 + 9 * damageType.getTexcoord(), 9, 9);
                GL11.glDisable(3553);
                this.drawRectWidthHeight(x2 + 14, y2 + 4, w2 + 2, h2 + 1, -16777216);
                this.drawRectWidthHeight(x2 + 15, y2 + 4, (int)(protection * (float)w2), h2, color);
                if (mouseX >= x2 && mouseY >= y2 + 2 && mouseX <= x2 + w && mouseY <= y2 + 12) {
                    this.hoveredDamageType = damageType;
                }
            }
            ++i;
        }
        GL11.glEnable(3553);
        if (this.hoveredDamageType != null) {
            int protection = Math.round(this.mc.thePlayer.inventory.getTotalProtectionAmount(this.hoveredDamageType) * 100.0f);
            if (protection < 0) {
                protection = 0;
            }
            if (protection > 100) {
                protection = 100;
            }
            String str = I18n.getInstance().translateKey("damagetype." + this.hoveredDamageType.name().toLowerCase()) + ":\n" + protection + " / 100";
            this.guiTooltip.render(str, mouseX, mouseY, 8, -8);
        }
    }
    private void uiSound(String soundDir){
        if (LegacyUI.config.getBoolean(ConfigTranslations.USE_LEGACY_SOUNDS.getKey())){
            mc.sndManager.playSound(soundDir, SoundType.GUI_SOUNDS, 1, 1);
        }
        else {
            mc.sndManager.playSound("random.click", SoundType.GUI_SOUNDS, 1, 1);
        }
    }

}
