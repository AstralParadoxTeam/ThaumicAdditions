package org.zeith.thaumicadditions.items.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.InfoTAR;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.lib.UtilsFX;

public class ItemRechargeCharm
		extends Item
		implements IBauble, IRenderBauble
{
	public ItemRechargeCharm()
	{
		setMaxStackSize(1);
		setTranslationKey("recharge_charm");
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.RARE;
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.CHARM;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player)
	{
		if(player instanceof EntityPlayer && !player.world.isRemote && player.ticksExisted % 4 == 0)
		{
			EntityPlayer ep = (EntityPlayer) player;
			int a;
			NonNullList inv = ep.inventory.mainInventory;
			for(int a2 = 0; a2 < InventoryPlayer.getHotbarSize(); ++a2)
			{
				if(RechargeHelper.rechargeItem(ep.world, (ItemStack) inv.get(a2), ep.getPosition(), ep, 1) <= 0.0f)
					continue;
				return;
			}
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(ep);
			for(a = 0; a < baubles.getSlots(); ++a)
			{
				if(RechargeHelper.rechargeItem(ep.world, baubles.getStackInSlot(a), ep.getPosition(), ep, 1) <= 0.0f)
					continue;
				return;
			}
			inv = ep.inventory.armorInventory;
			for(a = 0; a < inv.size(); ++a)
			{
				if(RechargeHelper.rechargeItem(ep.world, (ItemStack) inv.get(a), ep.getPosition(), ep, 1) <= 0.0f)
					continue;
				return;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks)
	{
		if(type == RenderType.BODY)
		{
			boolean armor = !player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
			Helper.rotateIfSneaking(player);
			Helper.translateToChest();
			Helper.defaultTransforms();
			com.zeitheron.hammercore.client.utils.UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/models/recharge_charm.png");
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-.25, -2.5 / 16D, armor ? -1 / 16D : 1 / 16D);
			GlStateManager.color(1, 1, 1);
			GlStateManager.scale(.5, .5, .5);
			UtilsFX.renderTextureIn3D(0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.1f);
		}
	}
}