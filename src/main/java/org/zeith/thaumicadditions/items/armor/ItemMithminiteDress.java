package org.zeith.thaumicadditions.items.armor;

import com.google.common.collect.Multimap;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.events.LivingEventsTAR;
import org.zeith.thaumicadditions.init.PotionsTAR;
import org.zeith.thaumicadditions.items.baubles.ItemFragnantPendant;
import org.zeith.thaumicadditions.net.PacketRemovePotionEffect;
import org.zeith.thaumicadditions.utils.ThaumicHelper;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.lib.potions.PotionWarpWard;

import java.util.HashSet;
import java.util.UUID;

public class ItemMithminiteDress
		extends ItemArmor
		implements IVisDiscountGear, IGoggles
{
	public static final ArmorMaterial MITHMINITE = EnumHelper.addArmorMaterial("TAR_MITHMINITE", "tar_mithminite", 0, new int[]{
			6,
			10,
			15,
			8
	}, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);
	private final int[] discounts = new int[]{
			0,
			0,
			10,
			15,
			20,
			15
	};

	public ItemMithminiteDress(EntityEquipmentSlot slot)
	{
		super(MITHMINITE, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer mp, ItemStack itemStack)
	{
		ItemStack tmp;
		boolean headWorn = !(tmp = mp.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).isEmpty() && tmp.getItem() instanceof ItemMithminiteDress;
		boolean bodyWorn = !(tmp = mp.getItemStackFromSlot(EntityEquipmentSlot.CHEST)).isEmpty() && tmp.getItem() instanceof ItemMithminiteDress;
		boolean beltWorn = !(tmp = mp.getItemStackFromSlot(EntityEquipmentSlot.LEGS)).isEmpty() && tmp.getItem() instanceof ItemMithminiteDress;
		boolean bootsWorn = !(tmp = mp.getItemStackFromSlot(EntityEquipmentSlot.FEET)).isEmpty() && tmp.getItem() instanceof ItemMithminiteDress;
		boolean fullSet = headWorn && bodyWorn && beltWorn && bootsWorn;

		if(fullSet)
		{
			if(mp.ticksExisted % 20 == 0)
				for(Potion p : new HashSet<>(mp.getActivePotionMap().keySet()))
					if(p.isBadEffect())
						mp.removePotionEffect(p);
		}

		switch(armorType)
		{
			case HEAD:
			{
				mp.addPotionEffect(new PotionEffect(PotionsTAR.SANITY_CHECKER, 2, 0, true, false));
				if(mp.isInWater() && mp.ticksExisted % 10 == 0)
					mp.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 31, 0, true, false));
				if(!mp.isPotionActive(PotionWarpWard.instance) && mp.ticksExisted % 40 == 0 && ItemFragnantPendant.ODOUR_POWDER.canConsume(mp.inventory))
				{
					if(world.rand.nextBoolean())
						ItemFragnantPendant.ODOUR_POWDER.consume(mp.inventory);
					ThaumicHelper.applyWarpWard(mp);
				}
				if(mp.ticksExisted % 10 == 0 && !world.isRemote)
				{
					boolean nightVision = world.getLightBrightness(mp.getPosition()) * 16F < 7F;
					if(!nightVision && itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("AimNightVision"))
					{
						RayTraceResult rtr = RayTracer.retrace(mp, 12);
						if(rtr != null && rtr.typeOfHit == Type.BLOCK)
							nightVision = world.getLightBrightness(rtr.getBlockPos().offset(rtr.sideHit)) * 16F < 3F;
					}
					if(nightVision)
						mp.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 299, 0, true, false));
					else if(mp.isPotionActive(MobEffects.NIGHT_VISION))
					{
						mp.removeActivePotionEffect(MobEffects.NIGHT_VISION);
						if(mp instanceof EntityPlayerMP && !world.isRemote)
							HCNet.INSTANCE.sendTo(new PacketRemovePotionEffect(MobEffects.NIGHT_VISION), (EntityPlayerMP) mp);
					}
				}
			}
			break;

			case CHEST:
			{
				mp.getEntityData().setBoolean("TAR_Flight", true);

				UUID id = new UUID(7899251962038665688L, -7792477727062207483L);
				AttributeModifier mod = new AttributeModifier(id, "TAR_MCHEST_HP", 20, 0).setSaved(true);
				IAttributeInstance attr = mp.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
				attr.removeModifier(id);
				attr.applyModifier(mod);

				if(mp.isBurning())
				{
					mp.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 119, 0, true, false));
					mp.extinguish();
				}
			}
			break;

			case LEGS:
			{
				mp.getEntityData().setInteger("TAR_LockFOV", 5);
			}
			break;

			case FEET:
			{
				if(!mp.capabilities.isFlying && mp.moveForward > 0.0f)
				{
					if(mp.world.isRemote && !mp.isSneaking())
					{
						if(!LivingEventsTAR.prevStep.containsKey(mp.getEntityId()))
							LivingEventsTAR.prevStep.put(mp.getEntityId(), Float.valueOf(mp.stepHeight));
						mp.stepHeight = 1.0f;
					}
					if(mp.onGround)
					{
						float bonus = 0.06f;
						if(mp.isInWater())
							bonus /= 2.0f;
						mp.moveRelative(0.0f, 0.0f, bonus, 1.0f);
					} else
					{
						if(mp.isInWater())
							mp.moveRelative(0.0f, 0.0f, 0.03f, 1.0f);
						mp.jumpMovementFactor = 0.05f;
					}
				}
			}
			break;

			default:
				break;
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
		if(slot == armorType)
		{
			if(slot == EntityEquipmentSlot.HEAD)
			{
				map.put("generic.luck", new AttributeModifier(new UUID(-2404983985385037352L, -6639556222455360507L), "TAR_MHEAD_LUCK", 1, 1));
			} else if(slot == EntityEquipmentSlot.LEGS)
			{
				map.put("generic.movementSpeed", new AttributeModifier(new UUID(7971309556076323288L, -6639837697432071133L), "TAR_MHEAD_LUCK", 1, 1));
				map.put("generic.flyingSpeed", new AttributeModifier(new UUID(7971309556076332790L, -6639837697432071133L), "TAR_MHEAD_LUCK", 1, 1));
			} else if(slot == EntityEquipmentSlot.FEET)
			{

			}
		}
		return map;
	}

	@Override
	public ItemMithminiteDress setTranslationKey(String key)
	{
		return (ItemMithminiteDress) super.setTranslationKey(key);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return InfoTAR.MOD_ID + ":textures/armor/mithminite_" + (slot == EntityEquipmentSlot.LEGS ? "1" : "0") + ".png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		return null;
	}

	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player)
	{
		return discounts[armorType.ordinal()];
	}

	@Override
	public boolean showIngamePopups(ItemStack stack, EntityLivingBase owner)
	{
		return armorType == EntityEquipmentSlot.HEAD;
	}
}