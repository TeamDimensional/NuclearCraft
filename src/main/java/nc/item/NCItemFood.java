package nc.item;

import nc.util.InfoHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;

import javax.annotation.Nullable;
import java.util.List;

public class NCItemFood extends ItemFood implements IInfoItem {
	
	private final PotionEffect[] effects;
	private final String[] tooltip;
	public String[] info;
	
	public NCItemFood(int amount, float saturation, boolean isWolfFood, PotionEffect[] potionEffects, String... tooltip) {
		super(amount, saturation, isWolfFood);
		effects = potionEffects;
		this.tooltip = tooltip;
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if (effects != null) {
			for (PotionEffect effect : effects) {
				player.addPotionEffect(new PotionEffect(effect));
			}
		}
	}
	
	@Override
	public void setInfo() {
		info = InfoHelper.buildInfo(getTranslationKey(), tooltip);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> currentTooltip, ITooltipFlag flag) {
		super.addInformation(itemStack, world, currentTooltip, flag);
		if (info.length > 0) {
			InfoHelper.infoFull(currentTooltip, TextFormatting.RED, InfoHelper.EMPTY_ARRAY, TextFormatting.AQUA, info);
		}
	}
}
