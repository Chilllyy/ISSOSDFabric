package me.chillywilly.issosd.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ISSModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private double pos_x;
    private double pos_y;

    public void load() {
        File folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        File file = new File(folder, "issPiss.json");
        if (!file.exists()) {
            reset();
            return;
        }
        try (var fr = new FileReader(file)) {
            ISSModConfig obj = GSON.fromJson(fr, ISSModConfig.class);
            setX(obj.getX());
            setY(obj.getY());
        } catch (Exception e) {
            IssosdClient.LOGGER.error("Failed to read file {}", file.getName(), e);
        }
    }

    public void save() {
        File folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            IssosdClient.LOGGER.error("Failed to create missing config folder");
            return;
        }
        File file = new File(folder, "issPiss.json");
        try (var fw = new FileWriter(file)) {
            GSON.toJson(this, ISSModConfig.class, fw);
            IssosdClient.LOGGER.info("Saved Config");
        } catch (Exception e) {
            IssosdClient.LOGGER.error("Failed to write file {}", file.getName(), e);
        }
    }

    public void reset() {
        pos_x = 0.5;
        pos_y = 0.0;
    }

    public double getX() {
        return pos_x;
    }

    public double getY() {
        return pos_y;
    }

    public void setX(double pos_x) {
        this.pos_x = pos_x;
    }

    public void setY(double pos_y) {
        this.pos_y = pos_y;
    }
}
