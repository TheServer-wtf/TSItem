package hu.Pdani.TSItem.utils;

import hu.Pdani.TSItem.ItemManager;
import hu.Pdani.TSItem.TSItemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static hu.Pdani.TSItem.TSItemPlugin.c;

public class MyCommand extends AMyCommand {

    public MyCommand(CommandMap commandMap, JavaPlugin plugin, String name) {
        super(plugin, name);
        addDescription("Gives the list of items specified in the config");
        addUsage("/"+name);

        registerCommand(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ConfigurationSection sec = TSItemPlugin.getPlugin().getConfig().getConfigurationSection("items");
        if(sec == null){
            return true;
        }
        List<String> items = new ArrayList<>();
        for(String i : sec.getKeys(false)){
            String exec = sec.getString(i+".execute");
            if(exec != null && !exec.isEmpty()){
                if(exec.equalsIgnoreCase(command.getName()))
                    items.add(i);
            }
        }
        if(!sec.getKeys(false).contains(command.getName())){
            return true;
        }
        if (commandSender instanceof Player){
            Player sender = (Player) commandSender;
            for(String k : items) {
                String perm = ItemManager.getItemPerm(k);
                if ((perm != null && !perm.isEmpty()) && !sender.hasPermission(perm)) {
                    continue;
                }
                ItemStack is = ItemManager.getItem(k);
                sender.getInventory().addItem(is);
            }
        } else {
            commandSender.sendMessage("In-game only.");
        }

        return true;
    }
}