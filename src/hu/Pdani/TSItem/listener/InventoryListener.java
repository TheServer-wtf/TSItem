package hu.Pdani.TSItem.listener;

import hu.Pdani.TSItem.TSItemPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {
    /*@EventHandler
    public void itemClick(InventoryClickEvent event){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
        ItemStack item = event.getCurrentItem();
        if(item == null || item.getType() == Material.AIR)
            return;
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if(pdc.has(namekey, PersistentDataType.STRING)){
            if(!event.getWhoClicked().hasPermission("tsitem.admin"))
                event.setCancelled(true);
        }
    }
    @EventHandler
    public void creativeItem(InventoryCreativeEvent event){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
        ItemStack item = event.getCursor();
        if(item.getType() == Material.AIR)
            return;
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if(pdc.has(namekey, PersistentDataType.STRING)){
            if(!event.getWhoClicked().hasPermission("tsitem.admin"))
                event.setCancelled(true);
        }
    }*/
    @EventHandler
    public void itemDrop(PlayerDropItemEvent event){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
        ItemStack item = event.getItemDrop().getItemStack();
        if(item == null || item.getType() == Material.AIR)
            return;
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if(pdc.has(namekey, PersistentDataType.STRING)){
            event.setCancelled(true);
        }
    }
}
