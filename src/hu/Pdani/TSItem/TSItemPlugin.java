package hu.Pdani.TSItem;

import hu.Pdani.TSItem.listener.CommandListener;
import hu.Pdani.TSItem.listener.InventoryListener;
import hu.Pdani.TSItem.listener.PlayerListener;
import hu.Pdani.TSItem.utils.MyCommand;
import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.manager.GuiManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSItemPlugin extends JavaPlugin {
    private static TSItemPlugin plugin;
    private static GuiManager gm = null;

    @Override
    public void onEnable() {
        plugin = this;
        Plugin tsmenu = getServer().getPluginManager().getPlugin("TSMenu");
        if((tsmenu instanceof TSMenuPlugin)){
            gm = ((TSMenuPlugin)tsmenu).getGm();
        }
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        saveDefaultConfig();
        getCommand("tsitem").setExecutor(new CommandListener());
        CommandMap commandMap = null;
        try {
            Field f = null;
            f = getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe(e.getMessage());
        }
        ConfigurationSection sec = TSItemPlugin.getPlugin().getConfig().getConfigurationSection("items");
        if(sec != null) {
            List<String> items = new ArrayList<>();
            for (String i : sec.getKeys(false)) {
                String exec = sec.getString(i + ".execute");
                if (exec != null && !exec.isEmpty()) {
                    if(!items.contains(exec))
                        items.add(exec);
                }
            }
            if(commandMap != null) {
                for (String mc : items) {
                    new MyCommand(commandMap, this, mc.toLowerCase());
                }
            }
        }
    }

    @Override
    public void onDisable() {

    }

    public GuiManager getGm() {
        return getGuiManager();
    }

    public static GuiManager getGuiManager() {
        return gm;
    }

    public static TSItemPlugin getPlugin() {
        return plugin;
    }

    public static String c(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }

    public static String rFirst(String target, String search, String replace){
        return target.replaceFirst(Pattern.quote(search), Matcher.quoteReplacement(replace));
    }
}
