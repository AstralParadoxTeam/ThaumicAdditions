package org.zeith.thaumicadditions.api.seals;

import net.minecraft.nbt.NBTTagCompound;
import org.zeith.thaumicadditions.tiles.TileSeal;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SealManager
{
	private static final List<SealCombination> combinations = new ArrayList<>();
	private static final Map<SealCombination, Function<TileSeal, SealInstance>> instances = new HashMap<>();

	public static ThreadLocal<Boolean> CROP_SEAL_DROP_GET = ThreadLocal.withInitial(() -> false);

	public static SealCombination getCombination(TileSeal seal)
	{
		for(int i = 0; i < combinations.size(); ++i)
			if(combinations.get(i).isValid(seal))
				return combinations.get(i);
		return null;
	}

	public static SealInstance makeInstance(TileSeal seal, SealCombination combo, @Nullable NBTTagCompound nbt)
	{
		Function<TileSeal, SealInstance> instanceMaker = instances.get(combo);
		if(instanceMaker != null)
		{
			SealInstance inst = instanceMaker.apply(seal);
			if(nbt != null)
				inst.readFromNBT(nbt);
			return inst;
		}
		return null;
	}

	public static void registerCombination(SealCombination combination, Function<TileSeal, SealInstance> instanceMaker)
	{
		combinations.add(combination);
		instances.put(combination, instanceMaker);
	}
}