package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockEnableable;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.tiles.TileAbstractSmelter;
import org.zeith.thaumicadditions.tiles.TileSmelterImpl;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.tiles.essentia.TileSmelter;

import java.util.Random;

public class BlockAbstractSmelter
		extends BlockDeviceHC<TileSmelterImpl>
		implements IBlockEnableable, IBlockHorizontal
{
	public float efficiency;
	public int speed;
	public int capacity;

	public BlockAbstractSmelter(String name, float efficiency, int speed, int capacity)
	{
		super(Material.IRON, TileSmelterImpl.class, name);
		this.efficiency = efficiency;
		this.capacity = capacity;
		this.speed = speed;
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(IBlockHorizontal.FACING, EnumFacing.NORTH).withProperty(IBlockEnableable.ENABLED, false));
	}

	public static void setFurnaceState(World world, BlockPos pos, boolean state)
	{
		if(state == WorldUtil.isEnabled(world.getBlockState(pos)))
			return;
		TileEntity tileentity = world.getTileEntity(pos);
		keepInventory = true;
		world.setBlockState(pos, world.getBlockState(pos).withProperty(IBlockEnableable.ENABLED, state), 3);
		if(tileentity != null)
		{
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
		keepInventory = false;
	}

	@Override
	public Class<TileSmelterImpl> getTileClass()
	{
		return TileSmelterImpl.class;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileSmelterImpl(efficiency, speed, capacity);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(IBlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite()).withProperty(IBlockEnableable.ENABLED, false);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2)
	{
		TileSmelter te = Cast.cast(worldIn.getTileEntity(pos), TileSmelter.class);
		if(te != null)
			te.checkNeighbours();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		GuiManager.openGui(player, Cast.cast(world.getTileEntity(pos), TileSyncable.class));
		return true;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return WorldUtil.isEnabled(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))) ? 13 : super.getLightValue(state, world, pos);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		IInventory te = Cast.cast(world.getTileEntity(pos), IInventory.class);
		if(te != null)
			return Container.calcRedstoneFromInventory(te);
		return 0;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileAbstractSmelter tileentity = Cast.cast(worldIn.getTileEntity(pos), TileAbstractSmelter.class);
		if(tileentity != null && !worldIn.isRemote && tileentity.vis > 0)
		{
			int ess = tileentity.vis;
			tileentity.inv.drop(worldIn, pos);
			AuraHelper.polluteAura(worldIn, pos, ess, true);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SideOnly(value = Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random r)
	{
		if(WorldUtil.isEnabled(state))
		{
			float f = pos.getX() + 0.5f;
			float f1 = pos.getY() + 0.2f + r.nextFloat() * 5.0f / 16.0f;
			float f2 = pos.getZ() + 0.5f;
			float f3 = 0.52f;
			float f4 = r.nextFloat() * 0.5f - 0.25f;

			if(WorldUtil.getFacing(state) == EnumFacing.WEST)
			{
				w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f - f3, f1, f2 + f4, 0.0, 0.0, 0.0);
				w.spawnParticle(EnumParticleTypes.FLAME, f - f3, f1, f2 + f4, 0.0, 0.0, 0.0);
			}

			if(WorldUtil.getFacing(state) == EnumFacing.EAST)
			{
				w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f3, f1, f2 + f4, 0.0, 0.0, 0.0);
				w.spawnParticle(EnumParticleTypes.FLAME, f + f3, f1, f2 + f4, 0.0, 0.0, 0.0);
			}

			if(WorldUtil.getFacing(state) == EnumFacing.NORTH)
			{
				w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f4, f1, f2 - f3, 0.0, 0.0, 0.0);
				w.spawnParticle(EnumParticleTypes.FLAME, f + f4, f1, f2 - f3, 0.0, 0.0, 0.0);
			}

			if(WorldUtil.getFacing(state) == EnumFacing.SOUTH)
			{
				w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f4, f1, f2 + f3, 0.0, 0.0, 0.0);
				w.spawnParticle(EnumParticleTypes.FLAME, f + f4, f1, f2 + f3, 0.0, 0.0, 0.0);
			}
		}
	}
}