package org.zeith.thaumicadditions.recipes;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.zeith.thaumicadditions.api.EdibleAspect;
import org.zeith.thaumicadditions.utils.Foods;

import java.util.Objects;

public class RecipeClearSalt
		extends Impl<IRecipe>
		implements IRecipe
{
	private static boolean nbtEqual(NBTTagCompound a, NBTTagCompound b)
	{
		return Objects.equals(a, b);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		boolean water = false;

		Item food = null;
		NBTTagCompound nbt = null;
		int foodq = 0;
		int dmg = 0;
		boolean hasAspects = false;
		int l = inv.getSizeInventory();
		for(int i = 0; i < l; ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty())
				continue;
			if(Foods.isFood(stack.getItem()))
			{
				if(food != null)
				{
					if(stack.getItem() != food || stack.getItemDamage() != dmg || !nbtEqual(nbt, stack.getTagCompound()))
						return false;
					else
						foodq++;
				} else
				{
					food = stack.getItem();
					dmg = stack.getItemDamage();
					nbt = stack.getTagCompound();
					hasAspects = EdibleAspect.getSalt(stack).visSize() > 0;
					foodq++;
				}
			} else if(stack.getItem() == Items.WATER_BUCKET)
			{
				if(water)
					return false;
				water = true;
			} else
				return false;
		}

		return food != null && foodq > 0 && hasAspects && water;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		boolean water = false;

		Item food = null;
		NBTTagCompound nbt = null;
		int foodq = 0;
		int dmg = 0;
		boolean hasAspects = false;
		int l = inv.getSizeInventory();
		for(int i = 0; i < l; ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty())
				continue;
			if(Foods.isFood(stack.getItem()))
			{
				if(food != null)
				{
					if(stack.getItem() != food || stack.getItemDamage() != dmg || !nbtEqual(nbt, stack.getTagCompound()))
						return ItemStack.EMPTY;
					else
						foodq++;
				} else
				{
					food = stack.getItem();
					dmg = stack.getItemDamage();
					nbt = stack.getTagCompound();
					hasAspects = EdibleAspect.getSalt(stack).visSize() > 0;
					foodq++;
				}
			} else if(stack.getItem() == Items.WATER_BUCKET)
			{
				if(water)
					return ItemStack.EMPTY;
				water = true;
			} else
				return ItemStack.EMPTY;
		}

		if(food != null && foodq > 0 && hasAspects && water)
		{
			ItemStack s = new ItemStack(food, foodq, dmg);
			if(nbt != null)
				s.setTagCompound(nbt);
			return EdibleAspect.withoutSalt(s);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height > 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
}