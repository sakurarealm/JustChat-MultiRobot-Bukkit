package cn.endymx.multirobot.util;

import cn.endymx.multirobot.LoadClass;
import cn.endymx.multirobot.packer.CMDPacker;
import cn.endymx.multirobot.packer.Packer;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.util.List;

public class MessageDecode {
    private final String data;
    private final LoadClass plugin;

    public MessageDecode(String data, LoadClass plugin) {
        this.data = data;
        this.plugin = plugin;
    }

    public void decodeData() {
        try {
            JSONObject json = new JSONObject(data);
            if (json.getInt("version") == Packer.PackVersion) {
                switch (json.getInt("type")) {
                    case MessagePackType.PING:
                        if (plugin.client.clientManager != null) {
                            //喂狗
                            plugin.client.clientManager.getPulseManager().feed();
                        }
                        break;
                    case MessagePackType.INFO:
                        try {
                            String content = json.getString("content");
                            plugin.getServer().broadcastMessage(MessageTools.Base64Decode(content));
                        } catch(Exception ignored) {

                        }
                        break;
                    case MessagePackType.UID: break;
                    case MessagePackType.CHAT:
                        String world = MessageTools.Base64Decode(json.getString("world_display"));
                        String sender = MessageTools.Base64Decode(json.getString("sender"));
                        TextComponent bc = new TextComponent(plugin.config.getString("messageFormQQ").replace("%world%", world).replace("%player%", sender));
                        JSONArray mjson = json.getJSONArray("content");
                        for (int i = 0; i < mjson.length(); i++) {
                            JSONObject msg = mjson.getJSONObject(i);
                            switch (msg.getString("type")) {
                                case "text":
                                    bc.addExtra(decodeTextMessage(MessageTools.Base64Decode(msg.getString("content"))));
                                    break;
                                case "cqcode":
                                    switch (msg.getString("function")) {
                                        case "CQ:at":
                                            TextComponent at = new TextComponent(MessageTools.Base64Decode(msg.getString("target")));
                                            at.setColor(ChatColor.BLUE);
                                            at.setItalic(true);
                                            bc.addExtra(at);
                                            break;
                                        case "CQ:image":
                                            TextComponent image = new TextComponent(MessageTools.Base64Decode(msg.getString("content")));
                                            //if (plugin.vv) image.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  "/getimage " + msg.getString("url") + " " + msg.getInt("width") + " " + msg.getInt("height") + " " + msg.getString("extension")));
                                            image.setColor(ChatColor.BLUE);
                                            //if (plugin.vv) image.setUnderlined(true);
                                            bc.addExtra(image);
                                            break;
                                        case "CQ:face":
                                            TextComponent face = new TextComponent("[表情:" + MessageTools.Base64Decode(msg.getString("content")) + "]");
                                            //if (plugin.vv) face.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  "/getimage [local]face/" + msg.getInt("id") + "." + msg.getString("extension") + " " + 28 + " " + 28 + " " + msg.getString("extension")));
                                            face.setColor(ChatColor.YELLOW);
                                            //if (plugin.vv) face.setUnderlined(true);
                                            bc.addExtra(face);
                                            break;
                                        case "CQ:hb":
                                            TextComponent hb = new TextComponent("[红包:" + MessageTools.Base64Decode(msg.getString("title") + "]"));
                                            hb.setColor(ChatColor.RED);
                                            bc.addExtra(hb);
                                            break;
                                        case "CQ:rich":
                                            if(!msg.getString("text").equals("")){
                                                TextComponent rich = new TextComponent(MessageTools.Base64Decode(msg.getString("text")));
                                                rich.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MessageTools.Base64Decode(msg.getString("url"))));
                                                rich.setColor(ChatColor.BLUE);
                                                rich.setUnderlined(true);
                                                bc.addExtra(rich);
                                            }
                                            break;
                                        case "CQ:share":
                                            TextComponent share = new TextComponent("[分享] " + MessageTools.Base64Decode(msg.getString("title")));
                                            share.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MessageTools.Base64Decode(msg.getString("url"))));
                                            share.setColor(ChatColor.BLUE);
                                            share.setUnderlined(true);
                                            bc.addExtra(share);
                                            break;
                                    }
                                    break;
                            }
                        }
                        plugin.getLogger().info(bc.toPlainText());
                        Object[] players = plugin.getServer().getOnlinePlayers().toArray();
                        for (int i = 0; i < players.length; i++){
                            if(plugin.qq.get(((Player)players[i]).getName()) == null || plugin.qq.get(((Player)players[i]).getName())){
                                if(plugin.config.getString("chatColorFormQQ")) bc = ChatColor.stripColor(bc.toString());
                                ((Player)players[i]).spigot().sendMessage(bc);
                            }
                        }
                        break;
                    case MessagePackType.CMD_List:
                        if (json.getInt("subtype") == 0) {
                            plugin.client.clientManager.send(new CMDPacker(json.getString("sender"),
                                    json.getString("world"),
                                    json.getString("world_display"),
                                    plugin.getServer().getMaxPlayers(),
                                    plugin.getServer().getOnlinePlayers().toArray())
                            );
                        } else {
                            plugin.getLogger().info("收到类型无法识别的消息");
                        }
                        break;
                    default:
                        plugin.getLogger().info("收到类型无法识别的消息");
                        break;
                }
            } else {
                plugin.getLogger().info("收到不同版本的消息");
            }
        } catch (JSONException e) {
            plugin.getLogger().warning("收到无法解析的信息");
        }
    }


    private TextComponent decodeTextMessage(String content) {
        UrlDetector parser = new UrlDetector(content, UrlDetectorOptions.HTML);
        List<Url> detectUrl = parser.detect();

        TextComponent retText = new TextComponent();

        int plainTextStart = 0;
        int i = 0;  // Original Content Pointer
        for (Url nowUrl : detectUrl) {
            String originalUrl = nowUrl.getOriginalUrl();

            // next array
            int[] next = new int[originalUrl.length()];
            getNext(originalUrl, next);

            // match
            int j = 0;
            for (j = 0; (i < content.length()) && (j < originalUrl.length()); ) {
                if ((j == -1) || (content.charAt(i) == originalUrl.charAt(j))) {
                    i++;
                    j++;
                } else {
                    j = next[j];
                }
            }

            /// current plain Text
            ///     Start  plainTextStart
            ///     End    i - originalUrl.length()
            ///     Length i - originalUrl.length() - plainTextStart
            if (i - originalUrl.length() - plainTextStart > 0) {
                String part = content.substring(plainTextStart, i - originalUrl.length());
                TextComponent partText = new TextComponent(part);
                retText.addExtra(partText);
            }

            /// current URL
            ///     Start  i - originalUrl.length()
            ///     End    i
            ///     Length originalUrl.length()
            String part = content.substring(i - originalUrl.length(), i);

            TextComponent partText = new TextComponent();
            try {
                String asciiUrl = IDN.toASCII(nowUrl.getHost());
                InetAddress address = InetAddress.getByName(asciiUrl);
                String url = nowUrl.toString();

                partText = new TextComponent(part);
                partText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                partText.setColor(ChatColor.BLUE);
                partText.setUnderlined(true);
            } catch (Exception e) {
                partText = new TextComponent(part);
            }

            retText.addExtra(partText);

            plainTextStart = i;

        }

        if (i < content.length()) {
            String part = content.substring(i, content.length());
            TextComponent partText = new TextComponent(part);
            retText.addExtra(partText);
        }

        return retText;
    }

    private void getNext(String pattern, int[] next) {
        int i = 0;
        int k = -1;
        next[0] = -1;

        for (i = 0; i < pattern.length() - 1; ) {
            if ((k == -1) || (pattern.charAt(i) == pattern.charAt(k))) {
                i++;
                k++;
                if (pattern.charAt(k) != pattern.charAt(i)) next[i] = k;
                else next[i] = next[k];
            } else k = next[k];
        }
    }
}
