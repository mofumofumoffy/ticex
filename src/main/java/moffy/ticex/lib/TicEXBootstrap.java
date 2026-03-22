package moffy.ticex.lib;

import moffy.ticex.TicEX;
import moffy.ticex.jei.ticex.TicEXJEIIntegration;
import moffy.ticex.lib.registry.JeiIntegrationsRegistry;

public class TicEXBootstrap {

    public static final JeiIntegrationsRegistry INSTANCE = new JeiIntegrationsRegistry();

    public static void setup() {
        INSTANCE.register(
                TicEX.getResource("ticex_compat"),
                TicEXJEIIntegration.class
        );
    }
}
