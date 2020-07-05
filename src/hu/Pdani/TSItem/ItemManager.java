package hu.Pdani.TSItem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static hu.Pdani.TSItem.TSItemPlugin.c;

public class ItemManager {
    public static ItemStack getItem(String item){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        NamespacedKey namekey = new NamespacedKey(plugin, "tsitem");
        ConfigurationSection options = plugin.getConfig().getConfigurationSection("items."+item);
        if(options == null)
            return null;
        int i_dam = options.getInt("damage",0);
        int i_cmd = options.getInt("custommodeldata",-1);
        String i_name = options.getString("name",null);
        String i_mat = options.getString("material","STONE");
        if(i_mat == null)
            i_mat = "STONE";
        int i_num = options.getInt("amount",1);
        if(i_num < 1)
            i_num = 1;
        List<String> i_lore = options.getStringList("lore");
        Material material = Material.getMaterial(i_mat.toUpperCase());
        if (material == null)
            material = Material.STONE;
        ItemStack is = new ItemStack(material,i_num);
        ItemMeta meta = is.getItemMeta();
        if(i_cmd > -1)
            meta.setCustomModelData(i_cmd);
        if(i_name != null)
            meta.setDisplayName(c(i_name));
        for(int i = 0; i < i_lore.size(); i++){
            i_lore.set(i, c(i_lore.get(i)));
        }
        meta.setLore(i_lore);
        meta.getPersistentDataContainer().set(namekey, PersistentDataType.STRING,item);
        is.setItemMeta(meta);
        if(meta instanceof Damageable){
            Damageable d = (Damageable) is.getItemMeta();
            d.setDamage(i_dam);
            is.setItemMeta((ItemMeta)d);
        }
        return is;
    }
    public static String getItemPerm(String item){
        TSItemPlugin plugin = TSItemPlugin.getPlugin();
        ConfigurationSection options = plugin.getConfig().getConfigurationSection("items."+item);
        if(options == null)
            return null;
        return options.getString("permission");
    }
}
