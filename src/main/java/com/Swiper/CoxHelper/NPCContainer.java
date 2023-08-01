package com.Swiper.CoxHelper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;

import java.awt.*;

@Getter(AccessLevel.PACKAGE)
class NPCContainer
{

    private final NPC npc;
    private final int npcIndex;
    private final String npcName;
    private int npcSize;
    @Setter(AccessLevel.PACKAGE)
    private int ticksUntilAttack;
    @Setter(AccessLevel.PACKAGE)
    private int intermissionPeriod;
    @Setter(AccessLevel.PACKAGE)
    private int npcSpeed;
    @Setter(AccessLevel.PACKAGE)
    private Actor npcInteracting;
    @Setter(AccessLevel.PACKAGE)
    private Attackstyle attackStyle;


    NPCContainer(NPC npc)
    {
        this.npc = npc;
        this.npcName = npc.getName();
        this.npcIndex = npc.getIndex();
        this.npcInteracting = npc.getInteracting();
        this.npcSpeed = 0;
        this.ticksUntilAttack = 0;
        this.intermissionPeriod = 0;
        this.attackStyle = Attackstyle.UNKNOWN;
        final NPCComposition composition = npc.getTransformedComposition();

        if (composition != null)
        {
            this.npcSize = composition.getSize();
        }
    }

    @AllArgsConstructor
    @Getter(AccessLevel.PACKAGE)
    public enum Attackstyle
    {
        MAGE("Mage", Color.CYAN),
        RANGE("Range", Color.GREEN),
        MELEE("Melee", Color.RED),
        UNKNOWN("Unknown", Color.WHITE);

        private final String name;
        private final Color color;
    }
}
