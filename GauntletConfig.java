/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.gauntlet;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Gauntlet")

public interface GauntletConfig extends Config {

    @ConfigItem(
            position = 0,
            keyName = "highlightResources",
            name = "Highlight Resources",
            description = "Highlights all the resources in each room with a color."
    )
    default boolean highlightResources() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "highlightResourcesIcons",
            name = "Highlight Icons",
            description = "Highlights all the icons in each room with an icon."
    )
    default boolean highlightResourcesIcons() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "countPlayerAttacks",
            name = "Count Player Attacks",
            description = "Count the player attacks until the boss switches their prayer."
    )
    default boolean countPlayerAttacks() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "countBossAttacks",
            name = "Count Boss Attacks",
            description = "Count the attacks until the boss switches their style."
    )
    default boolean countBossAttacks() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "showAttackStyle",
            name = "Show Attack Styles",
            description = "Mark orb with an individual attack style."
    )
    default boolean showAttackStyle() {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "overlayBoss",
            name = "Overlay the Boss",
            description = "Overlay the boss with the color attack style."
    )
    default boolean overlayBoss() {
        return false;
    }

    @ConfigItem(
            position = 6,
            keyName = "alertPrayerDrain",
            name = "Play Tune on Prayer Attack",
            description = "Plays a sound whenever the boss is about to shut down your prayer."
    )
    default boolean alertPrayerDrain() {
        return true;
    }
}
