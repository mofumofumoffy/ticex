package moffy.ticex.block.proxy.entity;

import net.minecraftforge.items.ItemStackHandler;

public class ProxyPlayerItemHandler extends ItemStackHandler {
    protected final ProxyBlockEntity proxyBlockEntity;

    public ProxyPlayerItemHandler(ProxyBlockEntity proxyBlockEntity){
        super();
        this.proxyBlockEntity = proxyBlockEntity;
    }
}
