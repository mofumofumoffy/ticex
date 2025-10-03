package moffy.ticex.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SynchedEntityData.class)
public interface DataItemAccessor {
    @Accessor("itemsById")
    Int2ObjectMap<SynchedEntityData.DataItem<?>> getItems();

    @Accessor("isDirty")
    void setDirtyByTicEX(boolean dirty);
}
