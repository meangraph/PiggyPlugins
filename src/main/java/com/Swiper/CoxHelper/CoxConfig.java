package com.Swiper.CoxHelper;

import java.awt.Color;
import java.awt.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("Cox")
public interface CoxConfig extends Config
{
    @ConfigSection(
            //name = "muttadileTitle",
            position = 1,
            name = "Muttadile",
            description = ""
    )
    String muttadileTitle = "Muttadile";

    @ConfigItem(
            position = 2,
            keyName = "muttadile",
            name = "Muttadile Marker",
            description = "Places an overlay around muttadiles showing their melee range.",
            section = muttadileTitle
    )
    default boolean muttadile()
    {
        return true;
    }

    @ConfigSection(
           // keyName = "tektonTitle",
            position = 3,
            name = "Tekton",
            description = ""
    )
    String tektonTitle = "Tekton";

    @ConfigItem(
            position = 4,
            keyName = "tekton",
            name = "Tekton Marker",
            description = "Places an overlay around Tekton showing his melee range.",
            section = tektonTitle
    )
    default boolean tekton()
    {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "tektonTickCounter",
            name = "Tekton Tick Counters",
            description = "Counts down current phase timer, and attack ticks.",
            section = tektonTitle
    )
    default boolean tektonTickCounter()
    {
        return true;
    }

    @ConfigSection(
           // keyName = "guardiansTitle",
            position = 5,
            name = "Guardians",
            description = ""
    )
    String guardiansTitle = "Guardians";

    @ConfigItem(
            position = 6,
            keyName = "guardians",
            name = "Guardians Overlay",
            description = "Places an overlay near Guardians showing safespot.",
            section = guardiansTitle
    )
    default boolean guardians()
    {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "guardinTickCounter",
            name = "Guardians Tick Timing",
            description = "Places an overlay on Guardians showing attack tick timers.",
            section = guardiansTitle
    )
    default boolean guardinTickCounter()
    {
        return true;
    }

    @ConfigSection(
            //keyName = "vanguardsTitle",
            position = 7,
            name = "Vanguards",
            description = ""
    )
    String vanguardsTitle = "Vanguards";

    @ConfigItem(
            position = 8,
            keyName = "vangHighlight",
            name = "Highlight Vanguards",
            description = "Color is based on their attack style.",
            section = vanguardsTitle
    )
    default boolean vangHighlight()
    {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "vangHealth",
            name = "Show Vanguards Current HP",
            description = "This will create an infobox with vanguards current hp.",
            section = vanguardsTitle
    )
    default boolean vangHealth()
    {
        return true;
    }

    @ConfigSection(
            //keyName = "olmTitle",
            position = 10,
            name = "Olm",
            description = ""
    )
    String olmTitle = "Olm";

    @ConfigItem(
            position = 11,
            keyName = "prayAgainstOlm",
            name = "Olm Show Prayer",
            description = "Shows what prayer to use during olm.",
            section = olmTitle
    )
    default boolean prayAgainstOlm()
    {
        return true;
    }

    @Range(
            min = 40,
            max = 100
    )
    @ConfigItem(
            position = 11,
            keyName = "prayAgainstOlmSize",
            name = "Olm Prayer Size",
            description = "Change the Size of the Olm Infobox.",
            section = olmTitle
    )
    @Units(Units.PIXELS)
    default int prayAgainstOlmSize()
    {
        return 40;
    }

    @ConfigItem(
            position = 12,
            keyName = "timers",
            name = "Olm Show Burn/Acid Timers",
            description = "Shows tick timers for burns/acids.",
            section = olmTitle
    )
    default boolean timers()
    {
        return true;
    }

    @ConfigItem(
            position = 13,
            keyName = "tpOverlay",
            name = "Olm Show Teleport Overlays",
            description = "Shows Overlays for targeted teleports.",
            section = olmTitle
    )
    default boolean tpOverlay()
    {
        return true;
    }

    @ConfigItem(
            position = 14,
            keyName = "olmTick",
            name = "Olm Tick Counter",
            description = "Show Tick Counter on Olm",
            section = olmTitle
    )
    default boolean olmTick()
    {
        return true;
    }

    @ConfigItem(
            position = 15,
            keyName = "olmDebug",
            name = "Olm Debug Info",
            description = "Dev tool to show info about olm",
            section = olmTitle
    )
    default boolean olmDebug()
    {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "olmPShowPhase",
            name = "Olm Phase Type",
            description = "Will highlight olm depending on which phase type is active. Red=Flame Green=Acid Purple=Crystal",
            section = olmTitle
    )
    default boolean olmPShowPhase()
    {
        return false;
    }

    @ConfigItem(
            position = 17,
            keyName = "olmAutoPrayers",
            name = "Auto pray against olm",
            description = "Automatically switch overhead prayers against olm",
            section = olmTitle
    )

    default boolean olmAutoPrayers() { return false; }

    @ConfigSection(
            //keyName = "colors",
            position = 18,
            name = "Colors",
            description = ""
    )
    String colors = "Colors";

    @ConfigItem(
            position = 19,
            keyName = "muttaColor",
            name = "Muttadile Tile Color",
            description = "Change hit area tile color for muttadiles",
            section = colors,
            hidden = false
            //unhide = "Muttadile"
    )
    default Color muttaColor()
    {
        return new Color(0, 255, 99);
    }

    @ConfigItem(
            position = 20,
            keyName = "guardColor",
            name = "Guardians Tile Color",
            description = "Change safespot area tile color for Guardians",
            section = colors,
            hidden = false
            //unhide = "Guardians"
    )
    default Color guardColor()
    {
        return new Color(0, 255, 99);
    }

    @ConfigItem(
            position = 21,
            keyName = "tektonColor",
            name = "Tekton Tile Color",
            description = "Change hit area tile color for Tekton",
            section = colors,
            hidden = false
            //unhide = "Tekton"
    )
    default Color tektonColor()
    {
        return new Color(193, 255, 245);
    }

    @ConfigItem(
            position = 22,
            keyName = "burnColor",
            name = "Burn Victim Color",
            description = "Changes tile color for burn victim.",
            section = colors,
            hidden = false
            //unhide = "timers"
    )
    default Color burnColor()
    {
        return new Color(255, 100, 0);
    }

    @ConfigItem(
            position = 23,
            keyName = "acidColor",
            name = "Acid Victim Color",
            description = "Changes tile color for acid victim.",
            section = colors,
            hidden = false
            //unhide = "timers"
    )
    default Color acidColor()
    {
        return new Color(69, 241, 44);
    }

    @ConfigItem(
            position = 24,
            keyName = "tpColor",
            name = "Teleport Target Color",
            description = "Changes tile color for teleport target.",
            section = colors,
            hidden = false
            //unhide = "tpOverlay"
    )
    default Color tpColor()
    {
        return new Color(193, 255, 245);
    }

    @ConfigItem(
            position = 25,
            keyName = "olmSpecialColor",
            name = "Olm Special Color",
            description = "Changes color of a special on Olm's tick counter",
            section = colors,
            hidden = false
            //unhide = "olmTick"
    )
    default Color olmSpecialColor()
    {
        return new Color(89, 255, 0);
    }

    @ConfigSection(
            //keyName = "text",
            position = 26,
            name = "Text",
            description = ""
    )
    String text = "Text";

    @ConfigItem(
            position = 27,
            keyName = "fontStyle",
            name = "Font Style",
            description = "Bold/Italics/Plain",
            section = text
    )
    default FontStyle fontStyle()
    {
        return FontStyle.BOLD;
    }

    @Range(
            min = 9,
            max = 20
    )
    @ConfigItem(
            position = 28,
            keyName = "textSize",
            name = "Text Size",
            description = "Text Size for Timers.",
            section = text
    )
    //@Units(Units.POINTS)
    default int textSize()
    {
        return 14;
    }

    @ConfigItem(
            position = 29,
            keyName = "shadows",
            name = "Shadows",
            description = "Adds Shadows to text.",
            section = text
    )
    default boolean shadows()
    {
        return true;
    }

    @Getter
    @AllArgsConstructor
    enum FontStyle
    {
        BOLD("Bold", Font.BOLD),
        ITALIC("Italic", Font.ITALIC),
        PLAIN("Plain", Font.PLAIN);

        private final String name;
        private final int font;

        @Override
        public String toString()
        {
            return this.getName();
        }
    }
}