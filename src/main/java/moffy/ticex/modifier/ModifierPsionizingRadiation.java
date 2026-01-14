package moffy.ticex.modifier;

import moffy.ticex.TicEX;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.lib.utils.TicEXPsiUtils;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.modifier.propeties.PsionizingRadiationProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;

import java.util.Map;
import java.util.function.BiFunction;

public class ModifierPsionizingRadiation
    extends NoLevelsModifier
    implements MeleeHitModifierHook, BlockBreakModifierHook, ProvidePropertyModifierHook, ValidateModifierHook {

    public static final ResourceLocation TIMES_CAST_LOC = TicEX.getResource("timescast");
    public static ResourceLocation AUTO_CASTING_LOC = TicEX.getResource("autocasting");
    public static ResourceLocation SOCKETS_LOC = TicEX.getResource("cad_sockets");

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(
            this,
            ModifierHooks.MELEE_HIT,
            ModifierHooks.BLOCK_BREAK,
            ModifierHooks.VALIDATE,
            TicEXRegistry.PROPERTY_PROVIDER_HOOK
        );
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry entry) {
        if (entry.getModifier() == this) {
            initPersistentData(tool);
        }
        return null;
    }

    @Override
    public void afterMeleeHit(
        IToolStackView tool,
        ModifierEntry modifier,
        ToolAttackContext context,
        float damageDealt
    ) {
        initPersistentData(tool);

        if (
            !tool.isBroken() &&
            context.getPlayerAttacker() != null &&
            !tool.getPersistentData().getBoolean(AUTO_CASTING_LOC)
        ) {
            return;
        }

        Player player = context.getPlayerAttacker();
        ItemStack toolStack = TicEXUtils.getToolStack(tool, player, this);
        TicEXPsiUtils.CastSpell(player, toolStack, spellContext -> {
            spellContext.attackedEntity = context.getLivingTarget();
        });
    }

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry entry, ToolHarvestContext context) {
        if (!tool.isBroken() && context.getPlayer() != null && !tool.getPersistentData().getBoolean(AUTO_CASTING_LOC)) {
            return;
        }

        Player player = context.getPlayer();
        ItemStack toolStack = TicEXUtils.getToolStack(tool, player, this);

        Direction sideHit = BlockSideHitListener.getSideHit(context.getPlayer());
        Vec3 hit = new Vec3(
            (double) context.getPos().getX() + 0.5D - sideHit.getStepX() * 0.5D,
            context.getPos().getY() + 0.5D - sideHit.getStepY() * 0.5D,
            context.getPos().getZ() + 0.5D - sideHit.getStepZ() * 0.5D
        );

        TicEXPsiUtils.CastSpell(player, toolStack, spellContext -> {
            spellContext.positionBroken = new BlockHitResult(hit, sideHit, context.getPos(), false);
        });
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return PsionizingRadiationProperty.getProperties();
    }

    protected void initPersistentData(IToolStackView tool) {
        ModDataNBT persistentData = tool.getPersistentData();
        if (!persistentData.contains(AUTO_CASTING_LOC, Tag.TAG_BYTE)) {
            persistentData.putBoolean(AUTO_CASTING_LOC, true);
        }

        if (!persistentData.contains(SOCKETS_LOC, Tag.TAG_INT)) {
            persistentData.putInt(SOCKETS_LOC, 1);
        }

        if (!persistentData.contains(TIMES_CAST_LOC, Tag.TAG_INT)) {
            persistentData.putInt(TIMES_CAST_LOC, 0);
        }
    }
}
