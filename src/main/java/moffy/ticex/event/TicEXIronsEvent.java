package moffy.ticex.event;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TicEXIronsEvent {

    public static void onCastSpell(SpellOnCastEvent event) {
        ItemStack bookStack = Utils.getPlayerSpellbookStack(event.getEntity());
        if (bookStack != null && !bookStack.isEmpty() && bookStack.getItem() instanceof IModifiable) {
            ToolStack book = ToolStack.from(bookStack);
            if (book.getModifierLevel(TicEXRegistry.OVERCASTING_MODIFIER.get()) > 0) {
                event.setManaCost(Math.round(event.getManaCost()));
            }
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (source instanceof SpellDamageSource && source.getEntity() instanceof Player player) {
            ItemStack bookStack = Utils.getPlayerSpellbookStack(player);
            if (bookStack != null && bookStack.getItem() instanceof IModifiable) {
                IToolStackView book = ToolStack.from(bookStack);

                if (book.getModifierLevel(TicEXRegistry.OVERCASTING_MODIFIER.get()) > 0) {
                    ToolAttackContext context = new ToolAttackContext(
                        player,
                        player,
                        InteractionHand.MAIN_HAND,
                        target,
                        target,
                        false,
                        0,
                        false
                    );

                    float originalDamage = event.getAmount();
                    float attackDamageStat = book.getStats().get(ToolStats.ATTACK_DAMAGE);

                    float initialDamage = (float) Math.sqrt(originalDamage*originalDamage + attackDamageStat*attackDamageStat);

                    float damage = initialDamage;
                    for(ModifierEntry entry : book.getModifierList()){
                        damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(book, entry, context, initialDamage, damage);
                    }

                    if (damage <= 0) {
                        event.setCanceled(true);
                        return;
                    }

                    event.setAmount(damage);

                    for (ModifierEntry entry : book.getModifierList()) {
                        entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(book, entry, context, damage, 0, 0);
                    }

                    for (ModifierEntry entry : book.getModifierList()) {
                        entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(book, entry, context, damage);
                    }
                }
            }
        }
    }
}
