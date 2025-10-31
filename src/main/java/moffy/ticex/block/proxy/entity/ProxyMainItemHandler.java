package moffy.ticex.block.proxy.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class ProxyMainItemHandler extends ItemStackHandler {

    protected final ProxyBlockEntity proxyBlockEntity;

    public ProxyMainItemHandler(ProxyBlockEntity proxyBlockEntity){
        super();
        this.proxyBlockEntity = proxyBlockEntity;
    }

    @Override
    protected void onContentsChanged(int s) {
        proxyBlockEntity.setChanged();
        proxyBlockEntity.refreshProxy();
        proxyBlockEntity.syncToClient();
    }
    @Override
    public boolean isItemValid(int s, ItemStack stack) {
        return !proxyBlockEntity.getBlockState().getValue(POWERED);
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return 1;
    }
}
