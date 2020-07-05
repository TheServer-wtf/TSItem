package hu.Pdani.TSItem.listener;

import hu.Pdani.TSItem.ItemManager;
import hu.Pdani.TSItem.TSItemPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static hu.Pdani.TSItem.TSItemPlugin.c;
import static hu.Pdani.TSItem.TSItemPlugin.rFirst;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerListener implements Listener {
    private HashMap<Player,Long> last = new HashMap<>();
    @EventHandler
    public void preCmd(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();
        Player player = event.getPlayer();
        if(cmd.startsWith("/")){
            cmd = rFirst(cmd,"/","");
        }
        ConfigurationSection sec = TSItemPlugin.getPlugin().getConfig().getConfigurationSection("items");
        if(sec == null){
            return;
        }
        for(String k : sec.getKeys(false)){
            if(sec.isSet(k+".execute")){
                String exec = sec.getString(k+".execute");
                if(exec.equalsIgnoreCase(cmd)){
                    event.setCancelled(true);
                    String perm = ItemManager.getItemPerm(k);
                    if((perm != null && !perm.isEmpty()) && !player.hasPermission(perm)){
                        return;
                    }
                    ItemStack is = ItemManager.getItem(k);
                    player.getInventory().addItem(is);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            ItemStack item = event.getItem();
            if(item == null || item.getType() == Material.AIR)
                return;
            TSItemPlugin plugin = TSItemPlugin.getPlugin();
            NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
            if(!item.hasItemMeta())
                return;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            if(pdc.has(namekey, PersistentDataType.STRING)){
                event.setCancelled(true);
                String name = pdc.get(namekey,PersistentDataType.STRING);
                String i_func = plugin.getConfig().getString("items."+name+".function");
                int i_limit = plugin.getConfig().getInt("items."+name+".limit",0);
                if(i_func == null || i_func.isEmpty())
                    return;
                long current = System.currentTimeMillis();
                switch (i_func.toUpperCase()){
                    case "MENU":
                        if(i_limit > 0){
                            if(last.containsKey(event.getPlayer())) {
                                long time = last.get(event.getPlayer());
                                int diff = (int) ((current-time)/1000);
                                if(diff < i_limit)
                                    break;
                                last.put(event.getPlayer(),current);
                            } else {
                                last.put(event.getPlayer(),current);
                            }
                        }
                        String menu = plugin.getConfig().getString("items."+name+".menu");
                        if(menu == null || menu.isEmpty())
                            break;
                        plugin.getGm().openMenu(event.getPlayer(),menu);
                        break;
                    case "COMMANDS":
                    case "COMMAND":
                        if(i_limit > 0){
                            if(last.containsKey(event.getPlayer())) {
                                long time = last.get(event.getPlayer());
                                int diff = (int) ((current-time)/1000);
                                if(diff < i_limit)
                                    break;
                                last.put(event.getPlayer(),current);
                            } else {
                                last.put(event.getPlayer(),current);
                            }
                        }
                        List<String> commands = plugin.getConfig().getStringList("items."+name+".commands");
                        for(String c : commands){
                            if(c.startsWith("CONSOLE$")) {
                                c = rFirst(c,"CONSOLE$", "");
                                c = c.replace("%player%", event.getPlayer().getName());
                                if (c.startsWith("/"))
                                    c = rFirst(c,"/", "");
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), c);
                            } else {
                                c = c.replace("%player%", event.getPlayer().getName());
                                if (c.startsWith("/"))
                                    c = rFirst(c,"/", "");
                                plugin.getServer().dispatchCommand(event.getPlayer(), c);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        if(plugin.getConfig().getBoolean("disableonjoin",false))
            return;
        NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        List<String> onjoin = plugin.getConfig().getStringList("onjoin");
        for(String o : onjoin){
            if(!o.contains("$"))
                continue;
            String[] split = o.split(Pattern.quote("$"));
            switch (split[0].toUpperCase()){
                case "FORCEHOTBAR":
                    if(!split[1].contains(":"))
                        break;
                    String item = split[1].split(Pattern.quote(":"))[0];
                    String slot = split[1].split(Pattern.quote(":"))[1];
                    int pos = -1;
                    try{
                        pos = Integer.parseInt(slot);
                    } catch (NumberFormatException ignored){}
                    if(pos <= -1 || pos >= 9)
                        break;
                    ItemStack inpos = inv.getItem(pos);
                    if(inpos != null && inpos.hasItemMeta()){
                        if(inpos.getItemMeta().getPersistentDataContainer().has(namekey, PersistentDataType.STRING)){
                            break;
                        }
                    }
                    String perm = ItemManager.getItemPerm(item);
                    if((perm != null && !perm.isEmpty()) && !player.hasPermission(perm))
                        break;
                    ItemStack is = ItemManager.getItem(item);
                    inv.setItem(pos,is);
                    break;
                case "HOTBAR":
                    pos = -1;
                    if(!split[1].contains(":"))
                        item = split[1];
                    else
                        item = split[1].split(Pattern.quote(":"))[0];
                    for(int i = 0;i < 9;i++){
                        ItemStack ip = inv.getItem(i);
                        if(ip != null && ip.getType() != Material.AIR){
                            if(ip.hasItemMeta() && ip.getItemMeta().getPersistentDataContainer().has(namekey, PersistentDataType.STRING)){
                                if(ip.getItemMeta().getPersistentDataContainer().get(namekey,PersistentDataType.STRING).equals(item)){
                                    break;
                                }
                            }
                            continue;
                        }
                        if(pos == -1)
                            pos = i;
                    }
                    if(pos == -1)
                        break;
                    perm = ItemManager.getItemPerm(item);
                    if((perm != null && !perm.isEmpty()) && !player.hasPermission(perm))
                        break;
                    is = ItemManager.getItem(item);
                    inv.setItem(pos,is);
                    break;
                default:
                    break;
            }
        }
    }
}
