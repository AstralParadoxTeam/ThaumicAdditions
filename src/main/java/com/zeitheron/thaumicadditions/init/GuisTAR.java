package com.zeitheron.thaumicadditions.init;

import java.lang.reflect.Field;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.thaumicadditions.init.guis.GuiCallbackCrystalBag;
import com.zeitheron.thaumicadditions.init.guis.GuiCallbackEssentiaPistol;
import com.zeitheron.thaumicadditions.init.guis.GuiCallbackRepairVoid;

public class GuisTAR
{
	public static final IGuiCallback ESSENTIA_PISTOL = new GuiCallbackEssentiaPistol();
	public static final IGuiCallback VOID_ANVIL = new GuiCallbackRepairVoid();
	public static final IGuiCallback CRYSTAL_BAG = new GuiCallbackCrystalBag();
	
	public static void register()
	{
		for(Field f : GuisTAR.class.getDeclaredFields())
		{
			f.setAccessible(true);
			if(IGuiCallback.class.isAssignableFrom(f.getType()))
				try
				{
					GuiManager.registerGuiCallback((IGuiCallback) f.get(null));
				} catch(Throwable err)
				{
				}
		}
	}
}