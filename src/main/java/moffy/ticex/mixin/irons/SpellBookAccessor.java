package moffy.ticex.mixin.irons;

import io.redspace.ironsspellbooks.item.SpellBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SpellBook.class, remap = false)
public interface SpellBookAccessor {

    @Mutable
    @Accessor("maxSpellSlots")
    void setMaxSpellSlots(int maxSpellSlots);
}
