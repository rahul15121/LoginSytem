package me.vortexedge.registerlogin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RegisterLogin extends JavaPlugin implements Listener {

    private Set<String> loggedInPlayers = new HashSet<>();
    private FileConfiguration playerData;
    private File playerDataFile;

    @Override
    public void onEnable() {
        // Register commands
        if (this.getCommand("register") != null) {
            this.getCommand("register").setExecutor(new RegisterCommand());
        }
        if (this.getCommand("login") != null) {
            this.getCommand("login").setExecutor(new LoginCommand());
        }
        if (this.getCommand("changepassword") != null) {
            this.getCommand("changepassword").setExecutor(new ChangePasswordCommand());
        }

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);

        // Initialize player data file
        playerDataFile = new File(getDataFolder(), "players.yml");
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load player data
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    @Override
    public void onDisable() {
        // Save player data on disable
        savePlayerData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerData.contains(player.getName())) {
            sendPersistentTitle(player, "Register", "Use /register <password>");
        } else {
            sendPersistentTitle(player, "Login", "Use /login <password>");
        }
        loggedInPlayers.remove(player.getName()); // Ensure player is not logged in by default
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 10, false, false));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!loggedInPlayers.contains(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!loggedInPlayers.contains(player.getName())) {
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/register") && !command.startsWith("/login")) {
                if (command.startsWith("/op ")) {
                    player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You can't do that lol");
                    Bukkit.getLogger().info(player.getName() + " tried to use /op command");
                } else {
                    player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You must be logged in to use commands.");
                }
                event.setCancelled(true);
            }
        }
    }

    private class RegisterCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            Player player = (Player) sender;
            if (playerData.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You are already registered.");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "Usage: /register <password>");
                return true;
            }
            String password = args[0];
            playerData.set(player.getName(), password);
            savePlayerData();
            player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You have successfully registered.");
            return true;
        }
    }

    private class LoginCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            Player player = (Player) sender;
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "Usage: /login <password>");
                return true;
            }
            String password = args[0];
            if (playerData.contains(player.getName()) && playerData.getString(player.getName()).equals(password)) {
                loggedInPlayers.add(player.getName());
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You have successfully logged in.");
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOWNESS);
            } else {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "Incorrect password or you need to register first.");
            }
            return true;
        }
    }

    private class ChangePasswordCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            Player player = (Player) sender;
            if (!loggedInPlayers.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "You must be logged in to change your password.");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "Usage: /changepassword <new_password>");
                return true;
            }
            String newPassword = args[0];
            playerData.set(player.getName(), newPassword);
            savePlayerData();
            player.sendMessage(ChatColor.RED + "[security] " + ChatColor.WHITE + "Your password has been successfully changed.");
            return true;
        }
    }

    private void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPersistentTitle(Player player, String title, String subtitle) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!loggedInPlayers.contains(player.getName())) {
                    player.sendTitle(title, subtitle, 10, 70, 20);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 100); // Resend the title every 5 seconds (100 ticks)
    }
}
