package org.zeith.thaumicadditions.items.weapons;

import com.zeitheron.hammercore.client.render.item.IItemRender;
import com.zeitheron.hammercore.client.render.vertex.SimpleBlockRendering;
import com.zeitheron.hammercore.client.utils.RenderBlocks;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.api.animator.BaseItemAnimator;
import org.zeith.thaumicadditions.api.animator.IAnimatableItem;
import org.zeith.thaumicadditions.api.animator.ItemShootableWeaponAnimator;
import org.zeith.thaumicadditions.api.items.EssentiaJarManager;
import org.zeith.thaumicadditions.api.items.EssentiaJarManager.IJar;
import org.zeith.thaumicadditions.entity.EntityEssentiaShot;
import org.zeith.thaumicadditions.init.GuisTAR;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.Arrays;
import java.util.List;

public class ItemEssentiaPistol
		extends Item
		implements IAnimatableItem
{
	final AspectList emptylist = new AspectList();

	public ItemEssentiaPistol()
	{
		setTranslationKey("essentia_pistol");
		setMaxStackSize(1);
	}

	public static void setJar(ItemStack pistol, ItemStack jar)
	{
		if(!pistol.isEmpty())
		{
			if(!pistol.hasTagCompound())
				pistol.setTagCompound(new NBTTagCompound());
			pistol.getTagCompound().setTag("Jar", jar.serializeNBT());
		}
	}

	public static ItemStack getJar(ItemStack pistol)
	{
		if(!pistol.isEmpty() && pistol.hasTagCompound() && pistol.getTagCompound().hasKey("Jar"))
			return new ItemStack(pistol.getTagCompound().getCompoundTag("Jar"));
		return ItemStack.EMPTY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		int i = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Count") : 1;
		tooltip.add(I18n.format("tooltip.thaumadditions:essentia_pistol.shoot", i));
		ItemStack jarStack = getJar(stack);
		IJar jar = EssentiaJarManager.fromStack(jarStack);
		AspectList list = jar != null ? jar.getEssentia(jarStack) : emptylist;
		if(list != null)
			for(Aspect a : list.getAspectsSortedByAmount())
				tooltip.add(" - " + a.getName() + " x" + String.format("%,d", list.getAmount(a)));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(playerIn.isSneaking())
		{
			GuiManager.openGuiCallback(GuisTAR.ESSENTIA_PISTOL, playerIn, new WorldLocation(worldIn, playerIn.getPosition()));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}

		ItemStack stack = playerIn.getHeldItem(handIn);
		ItemStack jarStack = getJar(stack);
		IJar jar = EssentiaJarManager.fromStack(jarStack);
		AspectList aspects = jar != null ? jar.getEssentia(jarStack) : emptylist;
		if(aspects != null && aspects.visSize() > 0)
		{
			if(!worldIn.isRemote)
			{
				Aspect as = aspects.getAspects()[0];
				if(as == null)
					return super.onItemRightClick(worldIn, playerIn, handIn);
				int amt = Math.min(Math.max(stack.getTagCompound().getInteger("Count"), 1), aspects.getAmount(as));
				AspectList a = new AspectList();
				a.add(as, amt);
				EntityEssentiaShot shot = new EntityEssentiaShot(worldIn, playerIn, a);

				EnumHandSide hs = playerIn.getPrimaryHand();
				int shift = (hs == EnumHandSide.RIGHT && handIn == EnumHand.MAIN_HAND) || (hs == EnumHandSide.LEFT && handIn == EnumHand.OFF_HAND) ? 90 : -90;

				float yaw = playerIn.rotationYaw + shift, pitch = playerIn.rotationPitch;
				float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
				float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
				float f2 = -MathHelper.cos(-pitch * 0.017453292F);
				float f3 = MathHelper.sin(-pitch * 0.017453292F);
				Vec3d look = new Vec3d(f1 * f2, f3, f * f2);
				Vec3d actualLook = playerIn.getLook(1F).scale(0.4F);

				shot.posX += look.x * 0.45F + actualLook.x;
				shot.posY += actualLook.y;
				shot.posZ += look.z * 0.45F + actualLook.z;

				shot.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 0F);
				worldIn.spawnEntity(shot);
				if(!playerIn.capabilities.isCreativeMode)
				{
					jar.drain(jarStack, as, amt);
					setJar(stack, jarStack);
				}
				HCNet.swingArm(playerIn, handIn);
				SoundUtil.playSoundEffect(worldIn, InfoTAR.MOD_ID + ":essentia_pistol_shoot", playerIn.posX, playerIn.posY, playerIn.posZ, 1F, 1F, SoundCategory.PLAYERS);
			}
			playerIn.getCooldownTracker().setCooldown(this, 10);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public BaseItemAnimator getAnimator(ItemStack stack)
	{
		return ItemShootableWeaponAnimator.DEF_0125;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float overrideSwing(float amount, ItemStack stack, EntityPlayer player, float partialTime)
	{
		return amount;
	}

	@SideOnly(Side.CLIENT)
	public static class ItemRendererEssentiaPistol
			implements IItemRender
	{
		final AspectList emptylist = new AspectList();

		@Override
		public void renderItem(ItemStack item)
		{
			ItemStack jarStack = getJar(item);
			IJar jar = EssentiaJarManager.fromStack(jarStack);
			AspectList list = jar != null ? jar.getEssentia(jarStack) : emptylist;

			if(list == null || list.visSize() == 0)
				return;

			float fill = list.visSize() / Math.max(1F, jar.capacity(jarStack));
			int color = AspectUtil.getColor(list, true);

			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();

			GlStateManager.disableBlend();
			GL11.glBlendFunc(770, 771);

			GL11.glDisable(2896);
			GlStateManager.rotate(22.5F, 1, 0, 0);
			GlStateManager.color(1F, 1F, 1F, 1F);
			SimpleBlockRendering sbr = RenderBlocks.forMod(InfoTAR.MOD_ID).simpleRenderer;
			sbr.begin();
			sbr.setSprite(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow"));
			Arrays.fill(sbr.rgb, color);
			sbr.setRenderBounds(0.475F, 0.57F, 0.495F, .56F, 0.57F + 0.18F * fill, 0.586F);
			sbr.setBrightness(255);
			sbr.drawBlock(0, -0.025F, -0.02F);
			sbr.end();
			GL11.glEnable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
	}
}