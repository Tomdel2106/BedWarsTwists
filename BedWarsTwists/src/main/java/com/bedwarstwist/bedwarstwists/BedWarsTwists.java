package com.votrenomdepaquetage.bedwarstwists;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BedWarsTwists extends JavaPlugin implements Listener {

    private BedWars bedWars;
    private Map<String, Runnable> twists;

    @Override
    public void onEnable() {
        bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
        getServer().getPluginManager().registerEvents(this, this);
        initializeTwists();
    }

    private void initializeTwists() {
        twists = new HashMap<>();
        twists.put("Double Ressources", this::applyDoubleRessources);
        twists.put("Gravité Réduite", this::applyGraviteReduite);
        twists.put("Équipements Aléatoires", this::applyEquipementsAleatoires);
        twists.put("Casser la Map", this::applyCasserLaMap);
        twists.put("Casser son Lit", this::applyCasserSonLit);
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getNewState() == IArena.GameState.waiting) {
            event.getArena().getPlayers().forEach(this::openTwistMenu);
        }
    }

    private void openTwistMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "Sélectionnez un Twist");

        ItemStack doubleRessources = createMenuItem(Material.DIAMOND, "Double Ressources", "Les générateurs produisent deux fois plus.");
        ItemStack graviteReduite = createMenuItem(Material.FEATHER, "Gravité Réduite", "Les joueurs sautent plus haut.");
        ItemStack equipementsAleatoires = createMenuItem(Material.IRON_SWORD, "Équipements Aléatoires", "Commencez avec un équipement aléatoire.");
        ItemStack casserLaMap = createMenuItem(Material.DIAMOND_PICKAXE, "Casser la Map", "La map se détruit progressivement.");
        ItemStack casserSonLit = createMenuItem(Material.RED_BED, "Casser son Lit", "Chaque équipe doit casser son propre lit.");

        menu.setItem(0, doubleRessources);
        menu.setItem(1, graviteReduite);
        menu.setItem(2, equipementsAleatoires);
        menu.setItem(3, casserLaMap);
        menu.setItem(4, casserSonLit);

        player.openInventory(menu);
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b" + name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Sélectionnez un Twist")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                IArena arena = bedWars.getArenaUtil().getArenaByPlayer(player);
                if (arena != null && arena.getStatus() == IArena.GameState.waiting) {
                    String twistName = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                    Runnable twistAction = twists.get(twistName);
                    if (twistAction != null) {
                        twistAction.run();
                        sendTitleToPlayers(arena, "Twist Sélectionné", twistName);
                    }
                }
                player.closeInventory();
            }
        }
    }

    private void sendTitleToPlayers(IArena arena, String title, String subtitle) {
        arena.getPlayers().forEach(player -> {
            player.sendTitle(title, subtitle, 10, 70, 20);
        });
    }

    private void applyDoubleRessources() {
        getLogger().info("Application du twist Double Ressources");
        // Implémentez la logique pour doubler les ressources
    }

    private void applyGraviteReduite() {
        getLogger().info("Application du twist Gravité Réduite");
        // Implémentez la logique pour réduire la gravité
    }

    private void applyEquipementsAleatoires() {
        getLogger().info("Application du twist Équipements Aléatoires");
        // Implémentez la logique pour donner des équipements aléatoires
    }

    private void applyCasserLaMap() {
        getLogger().info("Application du twist Casser la Map");
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (IArena arena : bedWars.getArenaUtil().getArenas()) {
                if (arena.getStatus() == IArena.GameState.playing) {
                    // Logique pour détruire progressivement la map
                    // Cette partie nécessite une implémentation prudente
                }
            }
        }, 20 * 60, 20 * 60); // Exécute toutes les minutes
    }

    private void applyCasserSonLit() {
        getLogger().info("Application du twist Casser son Lit");
        for (IArena arena : bedWars.getArenaUtil().getArenas()) {
            if (arena.getStatus() == IArena.GameState.playing) {
                for (ITeam team : arena.getTeams()) {
                    team.getMembers().forEach(player -> {
                        player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
                        player.sendMessage("Vous devez casser votre propre lit !");
                    });
                }
            }
        }
    }
}