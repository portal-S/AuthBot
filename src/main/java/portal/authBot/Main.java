package portal.authBot;

import com.github.dreamsmoke.attribute.AttributeBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import portal.authBot.bot.BotClass;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.events.ServerEvents;
import portal.authBot.bot.executor.Commands;
import portal.authBot.bot.handler.BotHandler;
import portal.authBot.bot.mysql.BDClass;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main plugin;
    protected static Main instance;
    static YamlConfiguration configuration;
    public static File file ;

    public void onEnable(){
        instance = this;
        file = new File(this.getDataFolder() , "users.yml");
        getServer().getPluginManager().registerEvents(new ServerEvents(), this);
        this.getCommand("bot").setExecutor((CommandExecutor)new Commands());
        this.load();
       // BotHandler.autoMessage();
        try {
            BotClass.main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static YamlConfiguration getConfiguration(){
        return configuration;
    }

    public void load(){
        File configfile = new File(this.getDataFolder() , "config.yml");
        if(!configfile.exists()){
            this.saveDefaultConfig();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
                configuration = YamlConfiguration.loadConfiguration(file);
                configuration.setDefaults(YamlConfiguration.loadConfiguration(getInstance().getResource("Example.yml")));
                configuration.options().copyDefaults(true);
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }
    public static void loadUsers(){
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void sendOps(String message){
        Server server = Main.getInstance().getServer();
        for(Player p : server.getOnlinePlayers()){
            if(p.isOp()){
                p.sendMessage(Main.replace(message));
            }
        }

    }
    public static boolean hasOnline(String nickname){
        Server server = Bukkit.getServer();
        if(server.getOnlinePlayers() == null) return false;
        for(Player p : server.getOnlinePlayers()){
            if(p.getName().equals(nickname)){
                return true;
            }
        }
        return false;
    }

    /*public static String stringBuild(String st, Object f){
        return st.replace("%s", String.valueOf(f));
    }*/

   /* public static String stringBuild(String st, String ... f){
        String nString = "";
        for(String string : f){
            String[] split = st.split("%s");
            for(int i = 0 ; i< split.length ; i++){
                if(i != split.length){
                    nString = split[i] + string;
                    break;
                } else {
                    nString = string + split[i];
                    break;
                }
            }
        }
        return nString;
    }*/

    public static String stringBuilds(String st, String ... f){
        String nString = "";
        String[] split = st.split("%s");
        int l = 0;
        for(int i = 0 ; i < f.length ; ++i){
            l++;
            nString = nString + split[i] + f[i];
        }
        if(split.length > f.length){
            nString = nString + split[l++];
        }
        return nString;
    }

    public static String getIp(String nickname){
        Server server = Bukkit.getServer();
        Player p = server.getPlayer(nickname);
        return String.valueOf(p.getAddress().getAddress()).substring(1);
    }

    public static String replace(String string){
        return string.replace("&", "§");
    }

    public static String unreplace(String string){
        return string.replace("§", "&");
    }

    public static void sendMes(String nick, int pass){
        Player p = Bukkit.getServer().getPlayer(nick);
        p.sendMessage(replace("&7[&cDiscord&7] &7Ваш код привязки аккаунта &c" + pass));
        p.sendMessage(replace("&7Никому не сообщайте данный код."));
        p.sendMessage(replace("&7[&cDiscord&7] &7Код необходимо ввести в аккаунте &6Элис, &7в течении &c60 секунд"));
    }

    public static void sendMesConfirm(String nick){
        Player p = Bukkit.getServer().getPlayer(nick);
        p.sendMessage(replace("&7[&cDiscord&7] &7Ваш аккаунт успешно верифицирован."));
    }

    public static int random(){
        int result = 1111111 + (int) (Math.random() * 9999999);
        return result;
    }


}
