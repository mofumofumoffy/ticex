package moffy.ticex.client.modules.ticex.screen;

import moffy.ticex.TicEX;
import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.block.transmuter.tank.TransmuterFluidTank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiTankModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidTransmuterScreen extends AbstractContainerScreen<FluidTransmuterContainerMenu> implements IScreenWithFluidTank {
    private static final int[] INPUT_TANK_START_X = {54, 22, 38, 70, 6};
    private static final ResourceLocation BACKGROUND = TicEX.getResource("textures/gui/fluid_transmuter.png");
    private static final ElementScreen SCALA = new ElementScreen(BACKGROUND, 176, 0, 34, 52, 256, 256);
    private static final ElementScreen FUEL_SLOT = new ElementScreen(BACKGROUND, 176, 52, 18, 36, 256, 256);
    private static final ElementScreen FUEL_TANK = new ElementScreen(BACKGROUND, 194, 52, 14, 38, 256, 256);
    private static final ElementScreen INPUT_TANK = new ElementScreen(BACKGROUND, 208, 52, 16, 54, 256, 256);

    private final GuiFuelModule fuel;
    private final GuiTankModule outputTank;

    private List<GuiTankModule> inputTanks;

    public FluidTransmuterScreen(FluidTransmuterContainerMenu container, Inventory pPlayerInventory, Component name) {
        super(container, pPlayerInventory, name);
        FluidTransmuterBlockEntity tile = container.getTile();

        this.inputTanks = new ArrayList<>();

        if (tile != null) {
            FuelModule fuelModule = tile.getFuelModule();
            fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot(), BACKGROUND);
            outputTank = new GuiTankModule(this, tile.getTank(), 114, 16, 34, 52, FluidTransmuterContainerMenu.TOOLTIP_FORMAT);
            updateTanks();
        } else {
            fuel = null;
            outputTank = null;
        }
    }

    private void updateTanks() {
        FluidTransmuterBlockEntity tile = menu.getTile();
        if (tile != null) {
            TransmuterFluidTank transmuterTank = tile.getTransmuterTank();

            List<GuiTankModule> tanks = new ArrayList<>();

            for (int i = 0; i < transmuterTank.getInputTanks(); i++) {
                IFluidHandler fluidHandler = transmuterTank.getFluidHandler(i);
                if (fluidHandler == null) continue;

                tanks.add(new GuiTankModule(this, fluidHandler, INPUT_TANK_START_X[i], 16, 14, 52, FluidTransmuterContainerMenu.TOOLTIP_FORMAT));
            }

            this.inputTanks = tanks;
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        FluidTransmuterBlockEntity tile = menu.getTile();
        if (tile != null && tile.getTransmuterTank().getInputTanks() != inputTanks.size()) {
            this.updateTanks();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int x, int y, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, x, y, partialTicks);
        this.renderTooltip(graphics, x, y);
    }


    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float v, int i, int i1) {
        GuiUtil.drawBackground(graphics, this, BACKGROUND);

        // fluids
        if (outputTank != null) outputTank.draw(graphics);

        // draw tank backgrounds first, then draw tank contents, less binding
        for (GuiTankModule tankModule : inputTanks) {
            INPUT_TANK.draw(graphics, tankModule.getX() - 1 + this.leftPos, tankModule.getY() - 1 + this.topPos);
        }

        // fuel
        if (fuel != null) {
            // draw the correct background for the fuel type
            if (menu.isHasFuelSlot()) {
                FUEL_SLOT.draw(graphics, leftPos + 150, topPos + 31);
            } else {
                FUEL_TANK.draw(graphics, leftPos + 152, topPos + 31);
            }
            fuel.draw(graphics);
        }

        // draw tank contents last, reduces bind calls
        for (GuiTankModule tankModule : inputTanks) {
            tankModule.draw(graphics);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int checkX = mouseX - this.leftPos;
        int checkY = mouseY - this.topPos;

        // highlight hovered tank
        if (outputTank != null) outputTank.highlightHoveredFluid(graphics, checkX, checkY);
        for (GuiTankModule tankModule : inputTanks) {
            tankModule.highlightHoveredFluid(graphics, checkX, checkY);
        }

        // highlight hovered fuel
        if (fuel != null) fuel.renderHighlight(graphics, checkX, checkY);

        // scala
        SCALA.draw(graphics, 114, 16, 100);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        // tank tooltip
        if (outputTank != null) outputTank.renderTooltip(graphics, mouseX, mouseY);

        for (GuiTankModule tankModule : inputTanks) {
            tankModule.renderTooltip(graphics, mouseX, mouseY);
        }

        // fuel tooltip
        if (fuel != null) fuel.addTooltip(graphics, mouseX, mouseY, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        assert minecraft != null && minecraft.player != null && minecraft.gameMode != null;
        if (!minecraft.player.isSpectator() && (button == 0 || button == 1) && !menu.getCarried().isEmpty()) {
            int checkX = (int) mouseX - leftPos;
            int checkY = (int) mouseY - topPos;

            // try tank first, offset 0
            if (outputTank != null && outputTank.tryClick(checkX, checkY, button, 0)) {
                return true;
            }
            // then try fuel, offset 2
            if (fuel != null && fuel.tryClick(checkX, checkY, button, 2)) {
                return true;
            }
            // finally, try all input tanks, offset 4+
            for (int i = 0; i < inputTanks.size(); i++) {
                if (inputTanks.get(i).tryClick(checkX, checkY, button, 4 + i * 2)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @SuppressWarnings("OptionalIsPresent")
    @Override
    public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
        int checkX = mouseX - leftPos;
        int checkY = mouseY - topPos;

        // try fuel first, its faster
        if (fuel != null) {
            FluidLocation ingredient = fuel.getFluidUnderMouse(checkX, checkY);
            if (ingredient != null) {
                return ingredient;
            }
        }

        // next output tank
        if (outputTank != null) {
            return outputTank.getFluidUnderMouse(checkX, checkY);
        }

        // finally input tanks
        Optional<GuiTankModule> inputTank = inputTanks.stream().findAny();
        if (inputTank.isPresent()) {
            return inputTank.get().getFluidUnderMouse(checkX, checkY);
        }

        return null;
    }
}
