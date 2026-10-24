package dev.war.openfight.client.gui.widgets.common;

import dev.war.openfight.api.utils.ExtensibleEnum;
import dev.war.openfight.api.utils.ExtensibleEnumManager;

/**
 * Enum interface for widget styles
 */
public interface WidgetTheme extends ExtensibleEnum {
    ExtensibleEnumManager<WidgetTheme> ENUM_MANAGER = new ExtensibleEnumManager<> ("widget_theme");
}
