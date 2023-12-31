package org.zeith.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.client.isr.ItemRenderJar;
import org.zeith.thaumicadditions.tiles.TileAbstractJarFillable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelJar;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS;

@AtTESR(TileAbstractJarFillable.class)
public class TESRAbstractJar
		extends TESR<TileAbstractJarFillable>
{
	public static final ResourceLocation TEX_LABEL = new ResourceLocation("thaumcraft", "textures/models/label.png");
	private static final ResourceLocation TEX_BRINE = new ResourceLocation("thaumcraft", "textures/models/jarbrine.png");
	private final ModelJar model = new ModelJar();

	@Override
	public void renderTileEntityAt(TileAbstractJarFillable tile, double x, double y, double z, float f, ResourceLocation destroyStage, float alpha)
	{
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glTranslatef((float) x + 0.5f, (float) y + 0.01f, (float) z + 0.5f);
		GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		GL11.glDisable(2896);
		if(tile.blocked)
		{
			GL11.glPushMatrix();
			this.bindTexture(TEX_BRINE);
			GL11.glScaled(1.001, 1.001, 1.001);
			this.model.renderLidBrace();
			GL11.glPopMatrix();
		}
		if(ThaumcraftApiHelper.getConnectableTile(tile.getWorld(), tile.getPos(), EnumFacing.UP) != null)
		{
			GL11.glPushMatrix();
			this.bindTexture(TEX_BRINE);
			GL11.glScaled(0.9, 1.0, 0.9);
			this.model.renderLidExtension();
			GL11.glPopMatrix();
		}
		if(tile.aspectFilter != null)
		{
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			switch(tile.facing)
			{
				case 3:
				{
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
					break;
				}
				case 5:
				{
					GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
					break;
				}
				case 4:
				{
					GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
				}
			}
			float rot = (tile.aspectFilter.getTag().hashCode() + tile.getPos().getX() + tile.facing) % 4 - 2;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, -0.4f, 0.315f);
			if(CONFIG_GRAPHICS.crooked)
				GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			UtilsFX.renderQuadCentered(TEX_LABEL, 0.5f, 1.0f, 1.0f, 1.0f, -99, 771, 1.0f);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, -0.4f, 0.316f);
			if(CONFIG_GRAPHICS.crooked)
				GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			GL11.glScaled(0.021, 0.021, 0.021);
			GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
			if(tile.aspectFilter != null)
				UtilsFX.drawTag(-8, -8, tile.aspectFilter);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
		if(tile.amount > 0)
			ItemRenderJar.renderLiquidInItem(tile.amount, tile.aspect, tile.getCapacity(), x, y, z);
		GL11.glEnable(2896);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
	}
}