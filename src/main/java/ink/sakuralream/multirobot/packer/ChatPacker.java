package ink.sakuralream.multirobot.packer;

import ink.sakuralream.multirobot.util.MessagePackType;
import ink.sakuralream.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ChatPacker extends Packer implements ISendable {

    public ChatPacker(AsyncPlayerChatEvent event){
        super(getMsg(event));
    }

    private static String getMsg(AsyncPlayerChatEvent event){
        JSONObject chatMessage = new JSONObject();
        chatMessage.put("version", PackVersion);
        chatMessage.put("type", MessagePackType.CHAT);
        chatMessage.put("world", MessageTools.Base64Encode(event.getPlayer().getWorld().getUID().toString()));
        chatMessage.put("world_display", MessageTools.Base64Encode(event.getPlayer().getWorld().getName()));
        chatMessage.put("sender", MessageTools.Base64Encode(event.getPlayer().getName()));
        JSONObject sender = new JSONObject();
        sender.put("name", MessageTools.Base64Encode(event.getPlayer().getName()));
        sender.put("uuid", MessageTools.Base64Encode(event.getPlayer().getUniqueId().toString()));
        sender.put("title", MessageTools.Base64Encode(PlaceholderAPI.setPlaceholders(event.getPlayer(), "%playerTitle_use%")));
        chatMessage.put("sender", sender);
        // chatMessage.put("title", PlayerTitleApi.getInstance().findByPlayerNameAndIsUse(event.getPlayer()).getTitleName());
        HashMap<String, String> chatArray = new HashMap<>();
        chatArray.put("type", "text");
        chatArray.put("content", MessageTools.Base64Encode(ChatColor.stripColor(event.getMessage())));
        chatMessage.put("content", new JSONArray().put(chatArray));
        return chatMessage.toString();
    }
}
