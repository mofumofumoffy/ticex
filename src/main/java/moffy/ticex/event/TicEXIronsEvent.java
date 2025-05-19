package moffy.ticex.event;

import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.util.Utils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXIronsEvent {
    public static void onSpellDamage(SpellDamageEvent event){
        if(event.getSpellDamageSource().getEntity() instanceof Player caster){
            LivingEntity target = event.getEntity();
            ItemStack bookStack = Utils.getPlayerSpellbookStack(caster);
            if(bookStack != null && !bookStack.isEmpty() && bookStack.getItem() instanceof IModifiable){
                ToolStack book = ToolStack.from(bookStack);
                if(book.getModifierLevel(TicEXRegistry.OVERCASTING_MODIFIER.get()) > 0){
                    ToolAttackContext context = new ToolAttackContext(caster, caster, InteractionHand.MAIN_HAND, target, target, false, 0, false);

                    float damage = event.getAmount();
                    float damageTmp = damage;
                    for(ModifierEntry entry : book.getModifierList()){
                        damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(book, entry, context, damageTmp, damage);
                    }

                    if(damage <= 0){
                        event.setCanceled(true);
                        return;
                    }

                    event.setAmount(damage);
                    
                    for(ModifierEntry entry : book.getModifierList()){
                        entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(book, entry, context, damage, 0, 0);
                    }

                    for(ModifierEntry entry : book.getModifierList()){
                        entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(book, entry, context, damage);
                    }
                }
            }
        }
    }

    public static void onCastSpell(SpellOnCastEvent event){
        ItemStack bookStack = Utils.getPlayerSpellbookStack(event.getEntity());
        if(bookStack != null && !bookStack.isEmpty() && bookStack.getItem() instanceof IModifiable){
            ToolStack book = ToolStack.from(bookStack);
            if(book.getModifierLevel(TicEXRegistry.OVERCASTING_MODIFIER.get()) > 0){
                event.setManaCost(Math.round(event.getManaCost() * 1.4f));
            }
        }
    }

}
