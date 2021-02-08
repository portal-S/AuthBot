package portal.authBot.bot.config;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import portal.authBot.Main;
import portal.authBot.bot.BotClass;
import scala.util.parsing.combinator.testing.Str;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BotConfig {

    public static ConfigurationSection getSection(){
        ConfigurationSection section = Main.getConfiguration().getConfigurationSection("users");
        return section;
    }

    public boolean hasPlayer(String nick){
        Set<String> key = getSection().getKeys(false);
        if(key.contains(nick)) return true;
        else return false;
    }

    public static boolean hasIp(String nick, String ip){
        if(getSection().getStringList(nick + ".ips").contains(ip)) return true;
        else return false;
    }
    public static String getId(String nick){
        return getSection().getString(nick + ".dsId");
    }

    public static String getNick(String nick){
        return BotClass.getJda().retrieveUserById(getId(nick)).complete().getAsTag();
    }

    public static List<String> getGuardList(){
        List users = new ArrayList();
        Set<String> userSet = getSection().getKeys(false);
        for(String user : userSet){
            users.add(user);
        }
        return users;
    }

    public static List<String> getIP(String nick){
        return getSection().getStringList(nick + ".ips");
    }


    public void addUser(String nick, String ip, String id) {
        Date dateNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        ConfigurationSection section = getSection();
        List<String> ips = new ArrayList<>();
        if(hasPlayer(nick)){
            ips = section.getStringList(nick + ".ips");
            ips.add(String.valueOf(ip));
            section.set(nick + ".ips", ips);
            section.set(nick + ".ldate", ft.format(dateNow));

        } else {
            ips.add(String.valueOf(ip));
            section.set(nick + ".ips", ips);
            section.set(nick + ".fdate", ft.format(dateNow));
            section.set(nick + ".dsId", id);
        }

        try {
            Main.getConfiguration().save(Main.file);
            Main.loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String firstDate(String nick){
        String date = getSection().getString(nick + ".fdate");
        if(date == null) return "---";
        return date;
    }

    public static String lastDate(String nick){
        String date = getSection().getString(nick + ".ldate");
        if(date == null) return "---";
        return date;

    }

    public void removeUser(String nick, String id, User user){
        ConfigurationSection section = getSection();
        if(hasPlayer(nick)){
            if(id.equals(section.get(nick+ ".dsId"))){
                section.set(nick , null);
                try {
                    Main.getConfiguration().save(Main.file);
                    Main.loadUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                user.openPrivateChannel().queue((channels) ->
                {
                    channels.sendMessageFormat("Аккаунт успешно отвязан").queue();
                    return;
                });
            } else {
                user.openPrivateChannel().queue((channels) ->
                {
                    channels.sendMessageFormat("Данный аккаунт не привязан к вашему Discord аккаунту").queue();
                    return;
                });
            }
        } else {
            user.openPrivateChannel().queue((channels) ->
            {
                channels.sendMessageFormat("Данный аккаунт не привязан").queue();
                return;
            });
        }
    }

    public void removeUser(String nick, CommandSender sender){
        ConfigurationSection section = getSection();
        if(hasPlayer(nick)){
            if(section.getKeys(false).contains(nick)){
                section.set(nick , null);
                try {
                    Main.getConfiguration().save(Main.file);
                    Main.loadUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(Main.replace("&aАккаунт &6" + nick + " &aуспешно отвязан"));
                return;
            }
        } else {
            {
                sender.sendMessage(Main.replace("&cАккаунт &6" + nick + " &cне привязан"));
                return;
            }
        }
    }

    public boolean onGuardDs(String id){
        Set<String> key = getSection().getKeys(false);
        for(String keys : key){
           ConfigurationSection section = Main.getConfiguration().getConfigurationSection("users." + keys);
           String cId = section.getString("dsId");
           if(id.equals(cId)){
               return true;
           }
        }
        return false;
    }

    public static void userDispatch(String message){
        ConfigurationSection section = getSection();
        Set<String> set = section.getKeys(false);
        if(set == null || set.isEmpty()) throw new NullPointerException("Set<String> isEmpty!");
        for( String users : set){
            String id = section.getString(users+ ".dsId");
            if(id == null) continue;
            User user = BotClass.getJda().retrieveUserById(id).complete();
            user.openPrivateChannel().queue((channels) ->
            {
                channels.sendMessageFormat(message).queue();
            });
        }
    }

    public static List<String> getMessages(){
        return Main.getInstance().getConfig().getStringList("messages");
    }


}
