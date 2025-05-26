package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.syncher.SynchedEntityData;

@Mixin(SynchedEntityData.class)
public interface DataItemAccessor {
    @Accessor("itemsById")
    public Int2ObjectMap<SynchedEntityData.DataItem<?>> getItems();

    @Accessor("isDirty")
    public void setDirtyByTicEX(boolean dirty);
}
