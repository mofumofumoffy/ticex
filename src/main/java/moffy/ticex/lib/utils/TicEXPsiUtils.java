package moffy.ticex.lib.utils;

import java.util.function.Consumer;
import moffy.ticex.modifier.ModifierPsionizingRadiation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;

public class TicEXPsiUtils {

    public static void CastSpell(Player player, ItemStack toolStack, Consumer<SpellContext> consumer) {
        PlayerData data = PlayerDataHandler.get(player);
        ItemStack playerCad = PsiAPI.getPlayerCAD(player);
        ToolStack tool = ToolStack.from(toolStack);

        if (!playerCad.isEmpty() && tool.getPersistentData().getBoolean(ModifierPsionizingRadiation.AUTO_CASTING_LOC)) {
            ItemStack bullet = ISocketable.socketable(toolStack).getSelectedBullet();
            final ItemStack finalTool = toolStack;
            ItemCAD.cast(
                player.getCommandSenderWorld(),
                player,
                data,
                bullet,
                playerCad,
                5,
                10,
                0.05F,
                spellContext -> {
                    spellContext.tool = finalTool;
                    consumer.accept(spellContext);
                }
            );
        }
    }
}
