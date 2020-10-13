package com.zeitheron.thaumicadditions.items.tools;

import com.zeitheron.thaumicadditions.TAReconstructed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tools.ItemElementalPickaxe;
import thaumcraft.common.items.tools.ItemElementalShovel;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

public class ItemVoidElementalShovel extends ItemElementalShovel
{
	public ItemVoidElementalShovel()
	{
		super(ItemVoidElementalHoe.TOOLMAT_VOID_ELEMENTAL);
		TAReconstructed.resetRegistryName(this);
		ConfigItems.ITEM_VARIANT_HOLDERS.remove(this);
		setTranslationKey("void_elemental_shovel");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(!worldIn.isRemote && stack.getItemDamage() > 0 && entityIn.ticksExisted % 10 == 0 && worldIn.rand.nextBoolean())
		{
			float cd = AuraHelper.drainVis(worldIn, entityIn.getPosition(), 0.1F, true);
			if(cd >= 0.1F)
			{
				AuraHelper.drainVis(worldIn, entityIn.getPosition(), 0.1F, false);
				stack.setItemDamage(stack.getItemDamage() - 1);
			}
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stack1, ItemStack stack2)
	{
		return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 1));
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
		{
			items.add(defInst());
		}
	}
	
	public ItemStack defInst()
	{
		ItemStack w1 = new ItemStack(this);
		EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.DESTRUCTIVE, 1);
		return w1;
	}
}