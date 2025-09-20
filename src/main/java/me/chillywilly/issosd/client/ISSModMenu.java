package me.chillywilly.issosd.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.function.Consumer;

public class ISSModMenu implements ModMenuApi {
    @Override
    public void attachModpackBadges(Consumer<String> consumer) {
        consumer.accept("modmenu");
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ISSModConfigScreen();
    }
}
