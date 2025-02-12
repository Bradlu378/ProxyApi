package foxo.flanty.proxyApi.utils.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtils {

    public static Component deserialize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static String serialize(Component text) {
        return MiniMessage.miniMessage().serialize(text).replace("/", "");
    }

    public static Component color(String hex, String text) {
        return MiniMessage.miniMessage().deserialize("<reset><color:%1$s>%2$s<reset>".formatted(hex, text))
                .decoration(TextDecoration.ITALIC, false);
    }

    public static Component color(String hex, String text, boolean italic, boolean bold, boolean reset) {
        String tags = "<reset>" + (italic ? "<italic>" : "") + (bold ? "<bold>" : "");
        return MiniMessage.miniMessage().deserialize(tags + "<color:%1$s>%2$s".formatted(hex, text) + (reset ? "<reset>" : "")).decoration(TextDecoration.ITALIC, italic);
    }

    public static Component color(String hex, Component text, boolean italic, boolean bold, boolean reset) {
        String tags = "<reset>" + (italic ? "<italic>" : "") + (bold ? "<bold>" : "");
        return MiniMessage.miniMessage().deserialize(tags + "<color:%1$s>%2$s".formatted(hex, plainText(text)) + (reset ? "<reset>" : ""));
    }

    public static Component gradient(String hex1, String hex2, String text) {
        return MiniMessage.miniMessage().deserialize("<gradient:%1$s:%2$s>%3$s</gradient>".formatted(hex1, hex2, text))
                .decoration(TextDecoration.ITALIC, false);

    }

    public static Component gradient(String hex1, String hex2, String text, boolean italic, boolean bold, boolean reset) {
        String tags = "<reset>" + (italic ? "<italic>" : "") + (bold ? "<bold>" : "");
        return MiniMessage.miniMessage().deserialize(tags + "<gradient:%1$s:%2$s>%3$s</gradient>".formatted(hex1, hex2, text) + (reset ? "<reset>" : ""))
                .decoration(TextDecoration.ITALIC, italic);
    }

    public static Component gradient(String hex1, String hex2, Component text) {
        return MiniMessage.miniMessage().deserialize("<gradient:%1$s:%2$s>%3$s</gradient>".formatted(hex1, hex2, plainText(text)));
    }

    public static String plainText(Component component) {
        String s = PlainTextComponentSerializer.plainText().serialize(component).replaceAll("§[0-9a-z]", "");
        return s.substring(s.startsWith("[") ? 1 : 0, s.endsWith("]") ? s.length() - 1 : s.length());
    }
}
