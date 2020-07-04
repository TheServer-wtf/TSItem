package hu.Pdani.TSItem;

import hu.Pdani.TSItem.listener.InventoryListener;
import hu.Pdani.TSItem.listener.PlayerListener;
import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.manager.GuiManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
