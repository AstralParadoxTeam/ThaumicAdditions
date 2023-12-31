package org.zeith.thaumicadditions.inventory.gui;

import com.zeitheron.hammercore.client.gui.GuiWidgets;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.gui.DynGuiTex;
import com.zeitheron.hammercore.client.utils.texture.gui.GuiTexBakery;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerCrystalCrusher;
import org.zeith.thaumicadditions.tiles.TileCrystalCrusher;

public class GuiCrystalCrusher
		extends GuiContainer
{
	public TileCrystalCrusher t;
	DynGuiTex tex;

	public GuiCrystalCrusher(EntityPlayer player, TileCrystalCrusher t)
	{
		super(new ContainerCrystalCrusher(player, t));
		this.t = t;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		GuiTexBakery b = GuiTexBakery.start();
		b.body(0, 0, xSize, ySize);
		for(Slot slot : inventorySlots.inventorySlots)
			b.slot(slot.xPos - 1, slot.yPos - 1);
		tex = b.bake();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		tex.render(guiLeft, guiTop);

		GuiWidgets.drawFurnaceArrow(guiLeft + 76, guiTop + 32, t.craftTime / 100F);

		int rgb = GuiTheme.current().slotColor;

		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/slots/crystal.png");
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft + 62 - 18, guiTop + 32, 0);
		GL11.glScalef(1 / 16F, 1 / 16F, 1);
		GL11.glColor4f(ColorHelper.getRed(rgb), ColorHelper.getGreen(rgb), ColorHelper.getBlue(rgb), 1);
		RenderUtil.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GL11.glPopMatrix();

		GL11.glColor4f(1, 1, 1, 1);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1, 1, 1, 1);
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);
	}
}