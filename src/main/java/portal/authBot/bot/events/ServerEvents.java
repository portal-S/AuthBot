package portal.authBot.bot.events;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.lwjgl.Sys;
import portal.authBot.Main;
import portal.authBot.bot.BotClass;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.handler.BotHandler;

import java.util.HashMap;

public class ServerEvents implements Listener {

    BotConfig botConfig = new BotConfig();
    BotClass botClass = new BotClass();
    BotHandler botHandler = new BotHandler();
    private static HashMap<String, String> auth = new HashMap<>();

    /*@EventHandler
    public void joinEvent(PlayerLoginEvent e){
        String nick = e.getPlayer().getName();
        String ip = String.valueOf(e.getAddress()).substring(1);
        if(botConfig.hasPlayer(nick)){
            User user = botClass.getJda().retrieveUserById(botConfig.getId(nick)).complete();
            if(!botConfig.hasIp(nick, String.valueOf(e.getAddress()).substring(1))) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Main.replace("&cВаш ip не привязан к данному аккаунту. Письмо с подтверждением выслано в Discord."));
                if(!botHandler.hasAuth(nick, ip)){
                    user.openPrivateChannel().queue((channels) ->
                    {
                        channels.sendMessageFormat("Совершена попытка входа в ваш аккаунт с неизвестного устройства." +
                                " Если это были не вы, то необходимо сменить пароль от игрового аккаунта.").queue();
                        channels.sendMessageFormat("Чтобы разрешить этому устройству вход в ваш аккаунт, введите '!auth ник' в течении 60 секунд").queue();
                        channels.sendMessageFormat("Подключаемый ip: " + ip).queue();
                    });
                    if(auth.containsKey(nick)){
                        auth.remove(nick);
                        auth.put(nick, ip);
                    } else auth.put(nick, ip);

                    botHandler.authTimer(nick, user.openPrivateChannel().complete());

                }
            }
        }
    }*/
    public HashMap getAuth() {
        return this.auth;
    }

    public void setAuth(HashMap<String, String> auth) {
        this.auth = auth;
    }

}
