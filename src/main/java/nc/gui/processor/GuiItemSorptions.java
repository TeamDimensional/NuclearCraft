package nc.gui.processor;

import nc.Global;
import nc.container.processor.ContainerSorptions;
import nc.gui.NCGui;
import nc.gui.element.*;
import nc.network.gui.*;
import nc.network.tile.TileUpdatePacket;
import nc.tile.*;
import nc.tile.internal.inventory.*;
import nc.tile.inventory.ITileInventory;
import nc.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

public abstract class GuiItemSorptions<TILE extends TileEntity & ITileGui<TILE, PACKET, INFO> & ITileInventory, PACKET extends TileUpdatePacket, INFO extends TileContainerInfo<TILE>> extends NCGui {
	
	protected final NCGui parent;
	protected final TILE tile;
	protected final EnumFacing[] dirs;
	protected final int slot;
	protected final ItemSorption.Type sorptionType;
	protected static ResourceLocation gui_textures;
	protected int[] a, b;
	
	public GuiItemSorptions(NCGui parent, TILE tile, int slot, ItemSorption.Type sorptionType) {
		super(new ContainerSorptions<>(tile));
		this.parent = parent;
		this.tile = tile;
		EnumFacing facing = tile.getFacingHorizontal();
		dirs = StreamHelper.map(BlockHelper.DIR_FROM_FACING, x -> x.apply(facing), EnumFacing[]::new);
		this.slot = slot;
		this.sorptionType = sorptionType;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (isEscapeKeyDown(keyCode)) {
			FMLCommonHandler.instance().showGuiScreen(parent);
		}
		else {
			super.keyTyped(typedChar, keyCode);
		}
	}
	
	@Override
	public void renderTooltips(int mouseX, int mouseY) {
		drawTooltip(Lang.localize("gui.nc.container.bottom_config") + " " + tile.getItemSorption(dirs[0], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[0], slot).getName() + "_config"), mouseX, mouseY, a[0], b[0], 18, 18);
		drawTooltip(Lang.localize("gui.nc.container.top_config") + " " + tile.getItemSorption(dirs[1], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[1], slot).getName() + "_config"), mouseX, mouseY, a[1], b[1], 18, 18);
		drawTooltip(Lang.localize("gui.nc.container.left_config") + " " + tile.getItemSorption(dirs[2], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[2], slot).getName() + "_config"), mouseX, mouseY, a[2], b[2], 18, 18);
		drawTooltip(Lang.localize("gui.nc.container.right_config") + " " + tile.getItemSorption(dirs[3], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[3], slot).getName() + "_config"), mouseX, mouseY, a[3], b[3], 18, 18);
		drawTooltip(Lang.localize("gui.nc.container.front_config") + " " + tile.getItemSorption(dirs[4], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[4], slot).getName() + "_config"), mouseX, mouseY, a[4], b[4], 18, 18);
		drawTooltip(Lang.localize("gui.nc.container.back_config") + " " + tile.getItemSorption(dirs[5], slot).getTextColor() + Lang.localize("gui.nc.container." + tile.getItemSorption(dirs[5], slot).getName() + "_config"), mouseX, mouseY, a[5], b[5], 18, 18);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 0.75F);
		IBlockState state = tile.getBlockState(tile.getTilePos());
		for (int i = 0; i < 6; ++i) {
			GuiBlockRenderer.renderGuiBlock(state, dirs[i], a[i] + 1, b[i] + 1, zLevel, 16, 16);
		}
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(gui_textures);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		for (int i = 0; i < 6; ++i) {
			buttonList.add(new NCEnumButton.ItemSorption(i, guiLeft + a[i], guiTop + b[i], tile.getItemSorption(dirs[i], slot), sorptionType));
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if (tile.getTileWorld().isRemote) {
			for (int i = 0; i < 6; ++i) {
				if (guiButton.id == i) {
					if (i == 4 && NCUtil.isModifierKeyDown()) {
						for (int j = 0; j < 6; ++j) {
							tile.setItemSorption(dirs[j], slot, ItemSorption.NON);
							((NCEnumButton.ItemSorption) buttonList.get(j)).set(ItemSorption.NON);
						}
						new ResetItemSorptionsPacket(tile, slot, false).sendToServer();
						return;
					}
					tile.toggleItemSorption(dirs[i], slot, sorptionType, false);
					new ToggleItemSorptionPacket(tile, dirs[i], slot, tile.getItemSorption(dirs[i], slot)).sendToServer();
					return;
				}
			}
		}
	}
	
	@Override
	protected void actionPerformedRight(GuiButton guiButton) {
		if (tile.getTileWorld().isRemote) {
			for (int i = 0; i < 6; ++i) {
				if (guiButton.id == i) {
					if (i == 4 && NCUtil.isModifierKeyDown()) {
						for (int j = 0; j < 6; ++j) {
							ItemSorption sorption = tile.getInventoryConnection(dirs[j]).getDefaultItemSorption(slot);
							tile.setItemSorption(dirs[j], slot, sorption);
							((NCEnumButton.ItemSorption) buttonList.get(j)).set(sorption);
						}
						new ResetItemSorptionsPacket(tile, slot, true).sendToServer();
						return;
					}
					tile.toggleItemSorption(dirs[i], slot, sorptionType, true);
					new ToggleItemSorptionPacket(tile, dirs[i], slot, tile.getItemSorption(dirs[i], slot)).sendToServer();
				}
			}
		}
	}
	
	public static class Input<TILE extends TileEntity & ITileGui<TILE, PACKET, INFO> & ITileInventory, PACKET extends TileUpdatePacket, INFO extends TileContainerInfo<TILE>> extends GuiItemSorptions<TILE, PACKET, INFO> {
		
		public Input(NCGui parent, TILE tile, int slot) {
			super(parent, tile, slot, ItemSorption.Type.INPUT);
			gui_textures = new ResourceLocation(Global.MOD_ID + ":textures/gui/container/input_item_config.png");
			a = new int[] {25, 25, 7, 43, 25, 43};
			b = new int[] {43, 7, 25, 25, 25, 43};
			xSize = 68;
			ySize = 68;
		}
	}
	
	public static class Output<TILE extends TileEntity & ITileGui<TILE, PACKET, INFO> & ITileInventory, PACKET extends TileUpdatePacket, INFO extends TileContainerInfo<TILE>> extends GuiItemSorptions<TILE, PACKET, INFO> {
		
		public ItemOutputSetting outputSetting;
		
		public Output(NCGui parent, TILE tile, int slot) {
			super(parent, tile, slot, ItemSorption.Type.OUTPUT);
			gui_textures = new ResourceLocation(Global.MOD_ID + ":textures/gui/container/output_item_config.png");
			a = new int[] {47, 47, 29, 65, 47, 65};
			b = new int[] {43, 7, 25, 25, 25, 43};
			xSize = 90;
			ySize = 68;
			outputSetting = tile.getItemOutputSetting(slot);
		}
		
		@Override
		public void renderTooltips(int mouseX, int mouseY) {
			super.renderTooltips(mouseX, mouseY);
			drawTooltip(Lang.localize("gui.nc.container.slot_setting_config") + " " + outputSetting.getTextColor() + Lang.localize("gui.nc.container." + outputSetting.getName() + "_setting_config"), mouseX, mouseY, 7, 25, 18, 18);
		}
		
		@Override
		public void initGui() {
			super.initGui();
			buttonList.add(new NCEnumButton.ItemOutputSetting(6, guiLeft + 7, guiTop + 25, outputSetting));
		}
		
		@Override
		protected void actionPerformed(GuiButton guiButton) {
			super.actionPerformed(guiButton);
			if (tile.getTileWorld().isRemote) {
				if (guiButton.id == 6) {
					outputSetting = outputSetting.next(false);
				}
			}
		}
		
		@Override
		protected void actionPerformedRight(GuiButton guiButton) {
			super.actionPerformedRight(guiButton);
			if (tile.getTileWorld().isRemote) {
				if (guiButton.id == 6) {
					outputSetting = outputSetting.next(true);
				}
			}
		}
		
		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			tile.setItemOutputSetting(slot, outputSetting);
			new ToggleItemOutputSettingPacket(tile, slot, outputSetting).sendToServer();
		}
	}
	
	public static class SpeedUpgrade<TILE extends TileEntity & ITileGui<TILE, PACKET, INFO> & ITileInventory, PACKET extends TileUpdatePacket, INFO extends TileContainerInfo<TILE>> extends Input<TILE, PACKET, INFO> {
		
		public SpeedUpgrade(NCGui parent, TILE tile, int slot) {
			super(parent, tile, slot);
			gui_textures = new ResourceLocation(Global.MOD_ID + ":textures/gui/container/speed_upgrade_item_config.png");
		}
	}
	
	public static class EnergyUpgrade<TILE extends TileEntity & ITileGui<TILE, PACKET, INFO> & ITileInventory, PACKET extends TileUpdatePacket, INFO extends TileContainerInfo<TILE>> extends Input<TILE, PACKET, INFO> {
		
		public EnergyUpgrade(NCGui parent, TILE tile, int slot) {
			super(parent, tile, slot);
			gui_textures = new ResourceLocation(Global.MOD_ID + ":textures/gui/container/energy_upgrade_item_config.png");
		}
	}
}
