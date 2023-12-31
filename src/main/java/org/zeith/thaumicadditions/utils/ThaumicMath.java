package org.zeith.thaumicadditions.utils;

public class ThaumicMath
{
	public static double cap(double value, double mx)
	{
		double d = value < 0D ? Math.min(value, -mx) : Math.max(value, mx);
		if(Math.abs(d) < mx)
			d = Math.abs(d) / d * mx;
		return d;
	}

	public static double mcap(double value, double mn)
	{
		return value < 0D ? Math.max(value, -mn) : Math.min(value, mn);
	}
}