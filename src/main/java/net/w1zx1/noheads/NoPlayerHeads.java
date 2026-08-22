package net.w1zx1.noheads;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class NoPlayerHeads extends JavaPlugin implements Listener {

    private List<String> noPlaceMessages;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin is disabled.");
    }

    private void loadMessages() {
        FileConfiguration config = getConfig();
        String lang = config.getString("language", "ru").toLowerCase();

        String defaultMessage;
        if (lang.equals("en")) {
            defaultMessage = "§7Sphere placement prevented. Good luck!";
        } else {
            defaultMessage = "§7Потеря сферы предотвращена. Удачи!";
        }

        // Собираем все сообщения: сначала стандартное, потом все из additional-messages
        List<String> messages = new ArrayList<>();
        messages.add(defaultMessage);

        // Получаем дополнительные сообщения и преобразуем & → §
        List<String> customMessages = config.getStringList("additional-messages");
        for (String msg : customMessages) {
            if (!msg.trim().isEmpty()) {
                messages.add(translateColorCodes(msg));
            }
        }

        noPlaceMessages = messages;
    }

    // Преобразование & в § (поддержка цветовых кодов и форматирования)
    private String translateColorCodes(String message) {
        return message.replace("&", "§");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHeadPlace(BlockPlaceEvent event) {
        Material type = event.getBlockPlaced().getType();

        if (type == Material.PLAYER_HEAD ||
            type == Material.PLAYER_WALL_HEAD ||
            (type.name().startsWith("PLAYER_") && type.name().endsWith("HEAD"))) {

            event.setCancelled(true);

            if (!noPlaceMessages.isEmpty()) {
                String randomMessage = noPlaceMessages.get(random.nextInt(noPlaceMessages.size()));
                event.getPlayer().sendMessage(randomMessage);
            }
        }
    }
}