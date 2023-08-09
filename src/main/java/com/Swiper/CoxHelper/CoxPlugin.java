/*
 * Copyright (c) 2019, xzact <https://github.com/xzact>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, gazivodag <https://github.com/gazivodag>
 * Copyright (c) 2019, lyzrds <https://discord.gg/5eb9Fe>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.Swiper.CoxHelper;

import com.Swiper.CoxHelper.Utils.CoxUtil;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import com.piggyplugins.PiggyUtils.RLApi.GraphicIDExtended;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.Swiper.CoxHelper.Utils.CoxUtil.getroom_type;

@Extension
@PluginDescriptor(
        name = "CoX Helper Extended",
        enabledByDefault = false,
        description = "All-in-one plugin for Chambers of Xeric with Auto Olm Prayers - Ported from OpenOSRS by Swiper",
        tags = {"CoX", "chamber", "xeric", "helper"}
)
@Slf4j
@Getter(AccessLevel.PACKAGE)
public class CoxPlugin extends Plugin
{
    private static final int ANIMATION_ID_G1 = 430;
    private static final Pattern TP_REGEX = Pattern.compile("You have been paired with <col=ff0000>(.*)</col>! The magical power will enact soon...");
    private final Map<NPC, NPCContainer> npcContainers = new HashMap<>();
    @Inject
    @Getter(AccessLevel.NONE)
    private Client client;
    @Inject
    @Getter(AccessLevel.NONE)
    private ChatMessageManager chatMessageManager;
    @Inject
    @Getter(AccessLevel.NONE)
    private CoxOverlay coxOverlay;
    @Inject
    @Getter(AccessLevel.NONE)
    private CoxInfoBox coxInfoBox;
    @Inject
    @Getter(AccessLevel.NONE)
    private CoxDebugBox coxDebugBox;
    @Inject
    @Getter(AccessLevel.NONE)
    private CoxConfig config;
    @Inject
    @Getter(AccessLevel.NONE)
    private OverlayManager overlayManager;
    @Inject
    @Getter(AccessLevel.NONE)
    private EventBus eventBus;
    @Inject
    private Olm olm;
    @Inject
    private MousePackets mousePackets;
    @Inject
    private WidgetPackets widgetPackets;
    @Inject
    private EthanApiPlugin api;
    //other
    private int vanguards;
    private boolean tektonActive;
    private int tektonAttackTicks;
    private List<NPC> npcsInTheArea;
    private Prayer toPrayOverhead = null;
    private Prayer toPrayBoost = null;
    private boolean treecut = false;
    private boolean iceout = false;
    private static final int TREE_STUMP = 29743;
    private static final int SMOKE_PUFF = 188;
    public static final int TEKTON_ANVIL = 7475;
    public static final int TEKTON_AUTO1 = 7482;
    public static final int TEKTON_AUTO2 = 7483;
    public static final int TEKTON_AUTO3 = 7484;
    public static final int TEKTON_FAST_AUTO1 = 7478;
    public static final int TEKTON_FAST_AUTO2 = 7488;
    public static final int TEKTON_ENRAGE_AUTO1 = 7492;
    public static final int TEKTON_ENRAGE_AUTO2 = 7493;
    public static final int TEKTON_ENRAGE_AUTO3 = 7494;

    private final Map<String, List<Integer>> roomNpcs = new HashMap<>();

    @Provides
    CoxConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CoxConfig.class);
    }

    @Override
    protected void startUp()
    {
        this.overlayManager.add(this.coxOverlay);
        this.overlayManager.add(this.coxInfoBox);
        this.overlayManager.add(this.coxDebugBox);
        roomNpcs.put("Mystics", Arrays.asList(NpcID.SKELETAL_MYSTIC, NpcID.SKELETAL_MYSTIC_7605, NpcID.SKELETAL_MYSTIC_7606));
        roomNpcs.put("Tekton", Arrays.asList(NpcID.TEKTON,NpcID.TEKTON_7541,NpcID.TEKTON_7542,NpcID.TEKTON_7545,NpcID.TEKTON_ENRAGED, NpcID.TEKTON_ENRAGED_7544));
        roomNpcs.put("Vanguards", Arrays.asList(NpcID.VANGUARD,NpcID.VANGUARD_7527,NpcID.VANGUARD_7528,NpcID.VANGUARD_7529,NpcID.VANGUARD_7526));
        roomNpcs.put("Muttadiles", Arrays.asList(NpcID.MUTTADILE,NpcID.MUTTADILE_7563,NpcID.MUTTADILE_7562));
        roomNpcs.put("Vasa", Arrays.asList(NpcID.VASA_NISTIRIO,NpcID.VASA_NISTIRIO_7567));
        this.olm.hardRest();
    }

    @Override
    protected void shutDown()
    {
        this.overlayManager.remove(this.coxOverlay);
        this.overlayManager.remove(this.coxInfoBox);
        this.overlayManager.remove(this.coxDebugBox);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (!this.inRaid())
        {
            return;
        }

        if (event.getType() == ChatMessageType.GAMEMESSAGE)
        {
            final Matcher tpMatcher = TP_REGEX.matcher(event.getMessage());

            if (tpMatcher.matches())
            {
                for (Player player : this.client.getPlayers())
                {
                    final String rawPlayerName = player.getName();

                    if (rawPlayerName != null)
                    {
                        final String fixedPlayerName = Text.sanitize(rawPlayerName);

                        if (fixedPlayerName.equals(Text.sanitize(tpMatcher.group(1))))
                        {
                            this.olm.getVictims().add(new Victim(player, Victim.Type.TELEPORT));
                        }
                    }
                }
            }

            switch (Text.standardize(event.getMessageNode().getValue()))
            {
                case "the great olm rises with the power of acid.":
                    olm.setPhaseType(Olm.PhaseType.ACID);
                    break;
                case "the great olm rises with the power of crystal.":
                    olm.setPhaseType(Olm.PhaseType.CRYSTAL);
                    break;
                case "the great olm rises with the power of flame.":
                    olm.setPhaseType(Olm.PhaseType.FLAME);
                    break;
                case "the great olm fires a sphere of aggression your way. your prayers have been sapped.":
                case "the great olm fires a sphere of aggression your way.":
                    this.olm.setPrayer(Prayer.PROTECT_FROM_MELEE);
                    toPrayOverhead = Prayer.PROTECT_FROM_MELEE;
                    break;
                case "the great olm fires a sphere of magical power your way. your prayers have been sapped.":
                case "the great olm fires a sphere of magical power your way.":
                    this.olm.setPrayer(Prayer.PROTECT_FROM_MAGIC);
                    toPrayOverhead = Prayer.PROTECT_FROM_MAGIC;
                    break;
                case "the great olm fires a sphere of accuracy and dexterity your way. your prayers have been sapped.":
                case "the great olm fires a sphere of accuracy and dexterity your way.":
                    this.olm.setPrayer(Prayer.PROTECT_FROM_MISSILES);
                    toPrayOverhead = Prayer.PROTECT_FROM_MISSILES;
                    break;
            }
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event)
    {
        if (!this.inRaid())
        {
            return;
        }

        final Projectile projectile = event.getProjectile();

        switch (projectile.getId())
        {
            case GraphicIDExtended.OLM_MAGE_ATTACK:
                this.olm.setPrayer(Prayer.PROTECT_FROM_MAGIC);
                toPrayOverhead = Prayer.PROTECT_FROM_MAGIC;
                break;
            case GraphicIDExtended.OLM_RANGE_ATTACK:
                this.olm.setPrayer(Prayer.PROTECT_FROM_MISSILES);
                toPrayOverhead = Prayer.PROTECT_FROM_MISSILES;
                break;
            case GraphicIDExtended.OLM_ACID_TRAIL:
                Actor actor = projectile.getInteracting();
                if (actor instanceof Player)
                {
                    this.olm.getVictims().add(new Victim((Player) actor, Victim.Type.ACID));
                }
                break;
        }
    }

    @Subscribe
    private void onGraphicChanged(GraphicChanged event)
    {
        if (!this.inRaid())
        {
            return;
        }

        if (!(event.getActor() instanceof Player))
        {
            return;
        }

        final Player player = (Player) event.getActor();

        if (player.hasSpotAnim(GraphicIDExtended.OLM_BURN) && player.hasSpotAnim(GraphicIDExtended.OLM_BURN)) {
            this.olm.getVictims().add(new Victim(player, Victim.Type.BURN));
        }

    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event)
    {
        if (!this.inRaid())
        {
            return;
        }

        final NPC npc = event.getNpc();

        switch (npc.getId())
        {
            case NpcID.TEKTON:
            case NpcID.TEKTON_7541:
            case NpcID.TEKTON_7542:
            case NpcID.TEKTON_7545:
            case NpcID.TEKTON_ENRAGED:
            case NpcID.TEKTON_ENRAGED_7544:
                this.npcContainers.put(npc, new NPCContainer(npc));
                this.tektonAttackTicks = 27;
                break;
            case NpcID.MUTTADILE:
            case NpcID.MUTTADILE_7562:
            case NpcID.MUTTADILE_7563:
            case NpcID.GUARDIAN:
            case NpcID.GUARDIAN_7570:
                this.npcContainers.put(npc, new NPCContainer(npc));
                break;
            case NpcID.VANGUARD:
            case NpcID.VANGUARD_7526:
            case NpcID.VANGUARD_7527:
            case NpcID.VANGUARD_7528:
            case NpcID.VANGUARD_7529:
                this.vanguards++;
                this.npcContainers.put(npc, new NPCContainer(npc));
                break;
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event)
    {
        if (!this.inRaid())
        {
            return;
        }

        final NPC npc = event.getNpc();

        switch (npc.getId())
        {
            case NpcID.TEKTON:
            case NpcID.TEKTON_7541:
            case NpcID.TEKTON_7542:
            case NpcID.TEKTON_7545:
            case NpcID.TEKTON_ENRAGED:
            case NpcID.TEKTON_ENRAGED_7544:
            case NpcID.MUTTADILE:
            case NpcID.MUTTADILE_7562:
            case NpcID.MUTTADILE_7563:
            case NpcID.GUARDIAN:
            case NpcID.GUARDIAN_7570:
            case NpcID.GUARDIAN_7571:
            case NpcID.GUARDIAN_7572:
                if (this.npcContainers.remove(event.getNpc()) != null && !this.npcContainers.isEmpty())
                {
                    this.npcContainers.remove(event.getNpc());
                }
                break;
            case NpcID.VANGUARD:
            case NpcID.VANGUARD_7526:
            case NpcID.VANGUARD_7527:
            case NpcID.VANGUARD_7528:
            case NpcID.VANGUARD_7529:
                if (this.npcContainers.remove(event.getNpc()) != null && !this.npcContainers.isEmpty())
                {
                    this.npcContainers.remove(event.getNpc());
                }
                this.vanguards--;
                break;
        }
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (!this.inRaid())
        {
            this.olm.hardRest();
            toPrayOverhead = null;
            toPrayBoost = null;
            treecut = false;
            iceout = false;
            npcsInTheArea.clear();
            return;
        }

        this.handleNpcs();

        if (this.olm.isActive())
        {
            this.olm.update();
        }
/*
        List<Integer> currentNpcIds;
        updateNpcsInTheArea();

        if (this.getCurrentRoomType() == CoxUtil.MYSTICS)
        //TODO: wheelchairMystics()
        {
            currentNpcIds = roomNpcs.get("Mystics");
            if (checkForNpcs(currentNpcIds))
            {
                toPrayOverhead = Prayer.PROTECT_FROM_MAGIC;
                toPrayBoost = Prayer.RIGOUR;
            }
            else
            {
                toPrayOverhead = null;
                toPrayBoost = null;
            }
        }
        else if (this.getCurrentRoomType() == CoxUtil.TEKTON)
        //TODO: wheelchairTekton()
        {
            currentNpcIds = roomNpcs.get("Tekton");
            if (checkForNpcs(currentNpcIds))
            {
                toPrayOverhead = Prayer.PROTECT_FROM_MELEE;
                toPrayBoost = Prayer.PIETY;
            }
            else
            {
                toPrayOverhead = null;
                toPrayBoost = null;
            }
        }

        else if (this.getCurrentRoomType() == CoxUtil.VANGUARDS && checkForNpcs(roomNpcs.get("Vanguards")))
        {
            //TODO: wheelchairVanguards()
        }
        else if (this.getCurrentRoomType() == CoxUtil.VESPULA && checkForNpcs(roomNpcs.get("Vespula")))
        {
            //TODO: wheelchairVespula()
        }
        else if (this.getCurrentRoomType() == CoxUtil.VASA && checkForNpcs(roomNpcs.get("Vasa")))
        {
            //TODO: wheelchairVasa()
        }

        else if (this.getCurrentRoomType() == CoxUtil.GUARDIANS)
        {
            //TODO: wheelchairGuardians()
        }

        else if (this.getCurrentRoomType() == CoxUtil.MUTTADILES)
        {
            //TODO: wheelchairMuttadiles()
        }

        else if (this.getCurrentRoomType() == CoxUtil.ICE_DEMON)
        {
            //TODO: wheelchairMuttadiles
        }

        else if (this.getCurrentRoomType() == CoxUtil.TIGHTROPE)
        {
            //TODO: wheelchairTightrope()
        }

        else if (this.getCurrentRoomType() == CoxUtil.CRABS)
        {
            //TODO: wheelchairCrabs()
        }

        else if (this.getCurrentRoomType() == CoxUtil.SHAMANS)
        {
            //TODO: wheelchairShamans()
        }
*/
        if (toPrayOverhead != null && !PrayerUtil.isPrayerActive(toPrayOverhead) && config.olmAutoPrayers())
        {
            PrayerUtil.togglePrayer(toPrayOverhead);
        }

        if (toPrayBoost != null && !PrayerUtil.isPrayerActive(toPrayBoost))
        {
            PrayerUtil.togglePrayer(toPrayBoost);
        }
    }


    private void updateNpcsInTheArea() {
        npcsInTheArea = client.getNpcs();
    }

    private boolean checkForNpcs(List<Integer> npcIds) {
        boolean condition = npcsInTheArea.stream().anyMatch(npc -> npcIds.contains(npc.getId()));
        return condition;
    }

    private void handleNpcs()
    {
        for (NPCContainer npcs : this.getNpcContainers().values())
        {
            switch (npcs.getNpc().getId())
            {
                case NpcID.TEKTON:
                case NpcID.TEKTON_7541:
                case NpcID.TEKTON_7542:
                case NpcID.TEKTON_7545:
                case NpcID.TEKTON_ENRAGED:
                case NpcID.TEKTON_ENRAGED_7544:
                    npcs.setTicksUntilAttack(npcs.getTicksUntilAttack() - 1);
                    npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
                    switch (npcs.getNpc().getAnimation())
                    {
                        case TEKTON_AUTO1:
                        case TEKTON_AUTO2:
                        case TEKTON_AUTO3:
                        case TEKTON_ENRAGE_AUTO1:
                        case TEKTON_ENRAGE_AUTO2:
                        case TEKTON_ENRAGE_AUTO3:
                            this.tektonActive = true;
                            if (npcs.getTicksUntilAttack() < 1)
                            {
                                npcs.setTicksUntilAttack(4);
                            }
                            break;
                        case TEKTON_FAST_AUTO1:
                        case TEKTON_FAST_AUTO2:
                            this.tektonActive = true;
                            if (npcs.getTicksUntilAttack() < 1)
                            {
                                npcs.setTicksUntilAttack(3);
                            }
                            break;
                        case TEKTON_ANVIL:
                            this.tektonActive = false;
                            this.tektonAttackTicks = 47;
                            if (npcs.getTicksUntilAttack() < 1)
                            {
                                npcs.setTicksUntilAttack(15);
                            }
                    }
                    break;
                case NpcID.GUARDIAN:
                case NpcID.GUARDIAN_7570:
                case NpcID.GUARDIAN_7571:
                case NpcID.GUARDIAN_7572:
                    npcs.setTicksUntilAttack(npcs.getTicksUntilAttack() - 1);
                    npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
                    if (npcs.getNpc().getAnimation() == ANIMATION_ID_G1 &&
                            npcs.getTicksUntilAttack() < 1)
                    {
                        npcs.setTicksUntilAttack(5);
                    }
                    break;
                case NpcID.VANGUARD_7529:
                    if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN)
                    {
                        npcs.setAttackStyle(NPCContainer.Attackstyle.MAGE);
                    }
                    break;
                case NpcID.VANGUARD_7528:
                    if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN)
                    {
                        npcs.setAttackStyle(NPCContainer.Attackstyle.RANGE);
                    }
                    break;
                case NpcID.VANGUARD_7527:
                    if (npcs.getAttackStyle() == NPCContainer.Attackstyle.UNKNOWN)
                    {
                        npcs.setAttackStyle(NPCContainer.Attackstyle.MELEE);
                    }
                    break;
            }
        }
        if (this.tektonActive && this.tektonAttackTicks > 0)
        {
            this.tektonAttackTicks--;
        }
    }

    boolean inRaid()
    {
        return this.client.getVarbitValue(Varbits.IN_RAID) == 1;
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated e) {
        if (e.getGraphicsObject().getId() == SMOKE_PUFF) {
            iceout = true;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event.getGameObject() == null)
        {
            return;
        }

        int id = event.getGameObject().getId();
        switch (id)
        {
            case Olm.HEAD_GAMEOBJECT_RISING:
            case Olm.HEAD_GAMEOBJECT_READY:
                if (this.olm.getHead() == null)
                {
                    this.olm.startPhase();
                }
                this.olm.setHead(event.getGameObject());
                break;
            case Olm.LEFT_HAND_GAMEOBJECT_RISING:
            case Olm.LEFT_HAND_GAMEOBJECT_READY:
                this.olm.setHand(event.getGameObject());
                break;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (event.getGameObject() == null)
        {
            return;
        }

        int id = event.getGameObject().getId();
        if (id == Olm.HEAD_GAMEOBJECT_READY)
        {
            this.olm.setHead(null);
        }
    }

    public int getCurrentRoomType()
    {
        LocalPoint lp = client.getLocalPlayer().getLocalLocation();
        int plane = client.getPlane();
        int x = lp.getSceneX();
        int y = lp.getSceneY();

        int template = client.getInstanceTemplateChunks()[plane][x / 8][y / 8];
        return getroom_type(template);
    }
}