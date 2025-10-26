package moffy.ticex.block.proxy.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class ProxyItemHandler extends ItemStackHandler {

    protected final ProxyBlockEntity proxyBlockEntity;

    public ProxyItemHandler(ProxyBlockEntity proxyBlockEntity){
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
}
