package com.zeitheron.thaumicadditions.items.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.VecDir;
import com.zeitheron.hammercore.utils.math.vec.Vector3;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.net.fxh.FXShadowBeamParticle;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ItemShadowBeamStaff extends Item
{
	public ItemShadowBeamStaff()
	{
		setTranslationKey("shadow_beam_staff");
		setMaxStackSize(1);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return false;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote)
		{
			List<Vec3d> positions = new ArrayList<>();
			double cx = 0, cy = 0, cz = 0;
			recursiveLoop(playerIn, 1F, positions, 80);
			Vec3d v = positions.get(0);
			positions.set(0, new Vec3d(v.x, v.y - 0.5, v.z));
			for(int i = 0; i < positions.size(); ++i)
			{
				Vec3d s = positions.get(i);
				cx += s.x;
				cy += s.y;
				cz += s.z;
			}
			cx /= positions.size();
			cy /= positions.size();
			cz /= positions.size();
			Map<Entity, Integer> strikes = new HashMap<>();
			for(int i = 0; i < positions.size() - 1; ++i)
			{
				Vec3d s = positions.get(i);
				Vec3d e = positions.get(i + 1);
				Vec3d dir = e.subtract(s).normalize();
				double dist = s.distanceTo(e);
				new VecDir(s, e.normalize(), dist).getEntitiesWithinDir(worldIn, EntityLivingBase.class).forEach(ent -> strikes.put(ent, strikes.computeIfAbsent(ent, e2 -> 0) + 1));
			}
			strikes.remove(playerIn);
			strikes.forEach((ent, count) -> ent.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), 5 * count));
			HCNet.INSTANCE.sendToAllAround(FXShadowBeamParticle.create(positions), new TargetPoint(worldIn.provider.getDimension(), cx, cy, cz, 256));
			SoundUtil.playSoundEffect(worldIn, InfoTAR.MOD_ID + ":shadow_beam", cx, cy, cz, 5F, 1F, SoundCategory.PLAYERS);
		}
		playerIn.getCooldownTracker().setCooldown(this, 20);
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
	
	public static void recursiveLoop(EntityPlayer player, float partialTime, List<Vec3d> positions, double distance)
	{
		Vector3 v = new Vector3(player.prevPosX + (player.posX - player.prevPosX) * partialTime, player.prevPosY + (player.posY - player.prevPosY) * partialTime, player.prevPosZ + (player.posZ - player.prevPosZ) * partialTime);
		v.y += player.getEyeHeight();
		Vec3d headVec = v.vec3();
		positions.add(headVec);
		Vec3d lookVec = player.getLook(1);
		Vec3d endVec = headVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
		RayTraceResult res = player.world.rayTraceBlocks(headVec, endVec, false, true, true);
		if(res != null && res.typeOfHit == Type.BLOCK)
		{
			Vec3d end = res.hitVec;
			positions.add(end);
			distance -= positions.get(0).distanceTo(end);
			
			boolean hasChanged = true;
			while(hasChanged)
			{
				hasChanged = false;
				
				Vec3d start = positions.get(positions.size() - 1);
				Vec3d dir = start.subtract(positions.get(positions.size() - 2)).normalize();
				
				Axis axe = res.sideHit.getAxis();
				
				if(axe == Axis.X)
					dir = new Vec3d(-dir.x, dir.y, dir.z);
				if(axe == Axis.Y)
					dir = new Vec3d(dir.x, -dir.y, dir.z);
				if(axe == Axis.Z)
					dir = new Vec3d(dir.x, dir.y, -dir.z);
				
				end = start.add(dir.scale(distance));
				res = player.world.rayTraceBlocks(start.add(dir.scale(0.01)), end, false, true, true);
				
				if(res != null && res.typeOfHit == Type.BLOCK)
				{
					end = res.hitVec;
					positions.add(end);
					distance -= start.distanceTo(end);
					hasChanged = true;
				} else
				{
					positions.add(end);
					break;
				}
			}
		} else
		{
			positions.add(endVec);
		}
	}
}