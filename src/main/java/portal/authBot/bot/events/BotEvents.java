package portal.authBot.bot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import portal.authBot.Main;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.handler.BotHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class BotEvents extends ListenerAdapter {

    private static HashMap<String, Integer> pass = new HashMap<>();
    private static Set delay = new HashSet();
    BotHandler botHandler = new BotHandler();
    BotConfig botConfig = new BotConfig();
    ServerEvents serverEvents = new ServerEvents();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if(user.isBot()) return;
        PrivateChannel channels = user.openPrivateChannel().complete();
        MessageChannel channel = event.getChannel();
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (channel.getType() != ChannelType.PRIVATE) {
            if ((command[0].equals("!guard") || command[0].equals("!verify")) && command.length == 1) {
                    channels.sendMessageFormat("Привет, чтобы узнать доступные команды, напиши !guard").queue();
            }
        } else if (channel.getType() == ChannelType.PRIVATE) {
            if (command[0].equals("!guard") || command[0].equals("!verify")) {
                if (command.length == 1) {
                        channels.sendMessageFormat("Привет, для привязки используй ").queue();
                        channels.sendMessageFormat("!guard ник - оправить запрос на привязку").queue();
                        channels.sendMessageFormat("!confirm ник код - привязать аккаунт").queue();
                        channels.sendMessageFormat("(необходимо находиться на сервере)").queue();
                        channels.sendMessageFormat("!remove ник - снять привязку").queue();
                } else if (command.length == 2) {
                    if (Main.hasOnline(command[1])) {
                        if(botConfig.hasPlayer(command[1])) {
                                channels.sendMessageFormat("Этот аккаунт уже привязан").queue();
                            return;
                        }
                        if(!botHandler.hasDelay(user.getId())) {
                            delay.add(user.getId());
                            botHandler.dsDelay(user.getId());
                            if (!botHandler.hasCode(command[1])) {
                                channels.sendMessageFormat("Вам отправлено сообщение с кодом в игре. Введите его сюда в течении 60 секунд").queue();
                                channels.sendMessageFormat("!confirm ник код - привязать аккаунт").queue();
                                int passInt = Main.random();
                                pass.put(command[1], passInt);
                                Main.sendMes(command[1], passInt);
                                botHandler.timer(command[1], channels);
                            } else {
                                channels.sendMessageFormat("Код уже был отправлен. Подтвердите его или подождите его истечения").queue();
                            }
                        } else {
                            channels.sendMessageFormat("Задержка на отправку запроса. Подождите 30 секунд с предыдущего запроса").queue();
                            return;
                        }
                    } else {
                            channels.sendMessageFormat("Такого игрока нет на сервере").queue();
                            return;
                    }
                } else {
                        channels.sendMessageFormat("Привет, для привязки используй ").queue();
                        channels.sendMessageFormat("!guard ник - оправить запрос на привязку").queue();
                        channels.sendMessageFormat("!confirm ник код - привязать аккаунт").queue();
                        channels.sendMessageFormat("(необходимо находиться на сервере)").queue();
                        channels.sendMessageFormat("!remove ник - снять привязку").queue();
                        channels.sendMessageFormat("!auth ник - принять заявку на привязку").queue();
                }
            } else if (command[0].equals("!confirm") && command.length == 3) {
                if(botConfig.onGuardDs(user.getId())){
                    channels.sendMessageFormat("К вашему аккаунту Discord уже привязан аккаунт").queue();
                    return;
                }
                if (getPass().containsKey(command[1]) && command[2].toString().equals(getPass().get(command[1]).toString())) {
                        channels.sendMessageFormat("Аккаунт успешно привязан").queue();
                        botConfig.addUser(command[1], Main.getIp(command[1]), user.getId());
                        Main.sendMesConfirm(command[1]);
                        HashMap set = getPass();
                        set.remove(command[1]);
                        setPass(set);
                        return;
                } else {
                        channels.sendMessageFormat("Такой код не существует или истёк").queue();
                        return;
                }
            }
            else if (command[0].equals("!remove")) {
                if(command.length == 2){
                    botConfig.removeUser(command[1], user.getId(), user);
                } else {
                        channels.sendMessageFormat("!remove ник - снять привязку").queue();
                        return;
                }
            }
            else if (command[0].equals("!auth")) {
                if(command.length == 2){
                   if(serverEvents.getAuth().containsKey(command[1])){
                       HashMap auth = serverEvents.getAuth();
                       botConfig.addUser(command[1], String.valueOf(auth.get(command[1])), user.getId());
                       auth.remove(command[1]);
                       serverEvents.setAuth(auth);
                       channels.sendMessageFormat("IP успешно добавлен").queue();
                    } else channels.sendMessageFormat("Нет ожидающих подтверждения запросов").queue();
                } else {
                    channels.sendMessageFormat("!auth ник - принять заявку").queue();
                    return;
                }
            }
        }
        super.onMessageReceived(event);
    }

    public HashMap getPass() {
        return this.pass;
    }

    public void setPass(HashMap<String, Integer> pass) {this.pass = pass;}

    public Set getDelay() {
        return this.delay;
    }

    public void setDelay(Set delay) {this.delay = delay;}
}

