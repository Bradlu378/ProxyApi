package foxo.flanty.proxyApi.utils.message;

import net.kyori.adventure.text.Component;

public interface Style {

    Style GRAY = (message) -> ComponentUtils.color("gray", message);

    Style WHITE = (message) -> ComponentUtils.color("#ffffff", message);

    Style BLACK = (message) -> ComponentUtils.color("#111111", message);

    Style DARK_GRAY = (message) -> ComponentUtils.color("dark_gray", message);

    Style GOLD = (message) -> ComponentUtils.color("gold", message);

    Style DARK_AQUA = (message) -> ComponentUtils.color("dark_aqua", message);

    Style GREEN = (message) -> ComponentUtils.color("#53B05C", message);

    Style RED = (message) -> ComponentUtils.color("#FB5454", message);

    Style SCHALKER = (message) -> ComponentUtils.gradient("#E14585", "#F970AA", message);

    Style SCHALKER_1 = (message) -> ComponentUtils.color("#E14585", message);

    Component style(String message);
}

