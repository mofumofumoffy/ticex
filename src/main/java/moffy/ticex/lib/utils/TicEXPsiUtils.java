package moffy.ticex.lib.utils;

import java.util.function.Consumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;

public class TicEXPsiUtils {
    public static void CastSpell(Player player, ItemStack toolStack, Consumer<SpellContext> consumer){
        PlayerData data = PlayerDataHandler.get(player);
		ItemStack playerCad = PsiAPI.getPlayerCAD(player);

        if (!playerCad.isEmpty()) {
            ItemStack bullet = ISocketable.socketable(toolStack).getSelectedBullet();
            final ItemStack finalTool = toolStack;
            ItemCAD.cast(player.getCommandSenderWorld(), player, data, bullet, playerCad, 5, 10, 0.05F, (spellContext)->{
                spellContext.tool = finalTool;
                consumer.accept(spellContext);
            });
        }
    }
}
