package me.chillywilly.issosd.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ISSModConfigScreen extends Screen {
    private final ISSModConfig config;
    public ISSModConfigScreen() {
        super(Text.translatable("issosd.menu.title"));
        this.config = IssosdClient.config;
    }

    @Override
    public void init() {
        config.load();
        this.clearChildren();
        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;

        SliderWidget widget_x = new SliderWidget(centerX - 100, baseY, 200, 20, Text.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"), config.getX()) {
            @Override
            protected void updateMessage() {
            }

            @Override
            protected void applyValue() {
                config.setX(this.value);
                this.setMessage(Text.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"));
            }
        };

        SliderWidget widget_y = new SliderWidget(centerX - 100, baseY + 40, 200, 20, Text.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"), config.getY()) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                config.setY(this.value);
                this.setMessage(Text.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"));
            }
        };

        this.addDrawableChild(widget_x);
        this.addDrawableChild(widget_y);
    }

    @Override
    public void removed() {
        config.save();
    }
}
