package hu.Pdani.TSItem.listener;

import hu.Pdani.TSItem.ItemManager;
import hu.Pdani.TSItem.TSItemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static hu.Pdani.TSItem.TSItemPlugin.c;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("tsitem.admin"))
            return true;
        String version = TSItemPlugin.getPlugin().getDescription().getVersion();
        List<String> authors = TSItemPlugin.getPlugin().getDescription().getAuthors();
        String longauthors = TSItemPlugin.getPlugin().getDescription().getAuthors().stream().collect(Collectors.joining(", ", "", ""));
        String description = TSItemPlugin.getPlugin().getDescription().getDescription();
        if(args.length == 0 || (args[0].equalsIgnoreCase("help"))){
            sendHelp(sender);
            return true;
        }
        switch (args[0].toUpperCase()){
            case "ABOUT":
                sender.sendMessage(c("&6&lTSItem plugin"));
                sender.sendMessage(c("&a "+description));
                sender.sendMessage(c("&eVersion: "+version));
                sender.sendMessage(c("&eAuthors: "+longauthors));
                break;
            case "RELOAD":
                TSItemPlugin.getPlugin().reloadConfig();
                sender.sendMessage(c("&aReloaded config!"));
                break;
            case "LIST":
                int num = 1;
                if(args.length >= 2) {
                    try {
                        num = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                ConfigurationSection sec = TSItemPlugin.getPlugin().getConfig().getConfigurationSection("items");
                if(sec == null){
                    sender.sendMessage(c("&cThere are no loaded items."));
                    return true;
                }
                String[] list = sec.getKeys(false).toArray(new String[0]);
                if(list.length == 0){
                    sender.sendMessage(c("&cThere are no loaded items."));
                    return true;
                }
                int page = (args.length < 2) ? 1 : num;
                int limit = 10;
                int start = (page-1) * limit;
                if(start >= list.length)
                    start = 0;
                int end = start + limit;
                if(list.length < end)
                    end = list.length;
                for(int i = start; i < end; i++){
                    sender.sendMessage((i+1)+". "+list[i]);
                }
                if(list.length > end)
                    sender.sendMessage(c("&eNext page: /tsitem list "+(page+1)));
                break;
            case "GIVE":
                if(args.length < 2){
                    sender.sendMessage(c("&cUsage: /tsitem give <item> [player]"));
                    break;
                } else if(args.length < 3) {
                    if(!(sender instanceof Player)){
                        sender.sendMessage(c("&cUsage: /tsitem give <item> <player>"));
                        break;
                    }
                    ((Player) sender).getInventory().addItem(ItemManager.getItem(args[1]));
                    break;
                }
                Player target = TSItemPlugin.getPlugin().getServer().getPlayer(args[2]);
                if(target == null){
                    sender.sendMessage(c("&cThe requested player is not online!"));
                    break;
                }
                target.getInventory().addItem(ItemManager.getItem(args[1]));
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender){
        String version = TSItemPlugin.getPlugin().getDescription().getVersion();
        List<String> authors = TSItemPlugin.getPlugin().getDescription().getAuthors();
        sender.sendMessage(c("&eTSItem plugin v"+version+", created by "+authors.get(0)));
        sender.sendMessage(c("&7- &9/tsitem reload <file> &7- &6Reload config file"));
        sender.sendMessage(c("&7- &9/tsitem give <item> [player] &7- &6Give the specified item to the player"));
        sender.sendMessage(c("&7- &9/tsitem list [page] &7- &6List all the loaded items"));
        sender.sendMessage(c("&7- &9/tsitem help &7- &6Display this list"));
        sender.sendMessage(c("&7- &9/tsitem about &7- &6Information about the plugin"));
    }
}
