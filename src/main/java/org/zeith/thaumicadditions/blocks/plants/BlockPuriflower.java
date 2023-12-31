package org.zeith.thaumicadditions.blocks.plants;

import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.Random;

public class BlockPuriflower
		extends BlockBush
{
	public BlockPuriflower()
	{
		setSoundType(SoundType.PLANT);
		setTranslationKey("puriflower");
		lightValue = 8;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.scheduleUpdate(pos, this, worldIn.rand.nextInt(1000) + 400);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		worldIn.scheduleUpdate(pos, this, worldIn.rand.nextInt(1000) + 400);
		checkAndDropBlock(worldIn, pos, state);

		float flux = AuraHandler.getFlux(worldIn, pos);
		float rem = Math.min(flux, .5F);

		if(rem > 0F)
			AuraHandler.drainFlux(worldIn, pos, rem, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if(rand.nextInt(5) == 0)
		{
			float xr = (float) pos.getX() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * .5F;
			float yr = (float) pos.getY() + 0.3f;
			float zr = (float) pos.getZ() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.5F;
			int rgb = 0x00CCFF;
			FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, 0.0, 0.0, 0.0, 10, ColorHelper.getRed(rgb), ColorHelper.getGreen(rgb), ColorHelper.getBlue(rgb), 0.001f);
		}
	}
}