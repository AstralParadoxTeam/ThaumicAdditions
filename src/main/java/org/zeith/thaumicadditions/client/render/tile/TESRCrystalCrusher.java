package org.zeith.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.utils.FrictionRotator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.client.models.ModelCrystalCrusher;
import org.zeith.thaumicadditions.tiles.TileCrystalCrusher;

public class TESRCrystalCrusher
		extends TESR<TileCrystalCrusher>
{
	public ResourceLocation texture = new ResourceLocation(InfoTAR.MOD_ID, "textures/models/crystal_crusher.png");
	public ModelCrystalCrusher model = new ModelCrystalCrusher();

	@Override
	public void renderTileEntityAt(TileCrystalCrusher te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		float lift = te.craftTime / 100F * 180F;
		lift = (float) Math.sin(Math.toRadians(lift));

		FrictionRotator cr = te.rotator;
		for(int i = 0; i < (destroyStage != null ? 2 : 1); ++i)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(x + .5, y + 1.5, z + .5);
			// GL11.glRotated(90, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			bindTexture(i == 1 ? destroyStage : texture);
			model.shape1.render(1 / 16F);
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 2 / 16F * lift, 0);
			GL11.glRotatef(cr.getActualRotation(partialTicks), 0, 1, 0);
			model.shape3.render(1 / 16F);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderItem(ItemStack item)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(.5, 1.5, .5);
		GL11.glRotated(180, 1, 0, 0);
		bindTexture(texture);
		model.render(null, 0, 0, 0, 0, 0, 1 / 16F);
		GL11.glPopMatrix();
	}
}