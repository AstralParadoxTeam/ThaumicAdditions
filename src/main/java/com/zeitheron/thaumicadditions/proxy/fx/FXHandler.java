package com.zeitheron.thaumicadditions.proxy.fx;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.thaumicadditions.net.fxh.FXHPacket;
import com.zeitheron.thaumicadditions.tiles.TileAuraDisperser;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class FXHandler
{
	public void spawnAuraDisperserFX(TileAuraDisperser tile)
	{
		if(tile == null)
			return;
		HCNet.INSTANCE.sendToAllAround(new FXHPacket(tile.getPos(), 0), new TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32));
	}
	
	public void renderMob(Entity entity, FrictionRotator rotator, double posX, double posY, double posZ, float partialTicks)
	{
		
	}
}