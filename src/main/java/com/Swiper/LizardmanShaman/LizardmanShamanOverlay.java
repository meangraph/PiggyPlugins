package com.Swiper.LizardmanShaman;

import com.example.EthanApiPlugin.EthanApiPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Set;

@Slf4j
@Singleton
class LizardmanShamanOverlay extends Overlay
{
    private final Client client;
    private final LizardmanShamanPlugin plugin;
    private static final Set<Integer> LIZARDMAN_TEMPLE_REGIONS = Set.of(5277);

    @Inject
    private LizardmanShamanOverlay(final Client client, final LizardmanShamanPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.getSpawnOverlayConfig().equals(LizardmanShamanConfig.SpawnOverlayConfig.DISABLED))
        {
            renderNormalTiles(graphics);
            renderExplosionTiles(graphics);
        }

        return null;
    }

    /**
     * Adapted from net.runelite.client.plugins.devtools.SceneOverlay
     *
     * @param graphics
     */
    private void renderNormalTiles(Graphics2D graphics)
    {
        int range = isInLizardmanTemple() ? 1 : 2;

        for (NPC npc : client.getNpcs())
        {
            if (!isSpawnNpc(npc) || isExplodingAnimation(npc))
            {
                continue;
            }

            for (int dx = -range; dx <= range; dx++)
            {
                for (int dy = -range; dy <= range; dy++)
                {
                    if (dx == 0 && dy == 0)
                    {
                        continue;
                    }

                    renderTileIfValidForMovement(graphics, npc, dx, dy);
                }
            }
        }
    }

    private void renderExplosionTiles(Graphics2D graphics)
    {
        int range = isInLizardmanTemple() ? 1 : 2;

        for (NPC npc : client.getNpcs())
        {
            if (!isExplodingAnimation(npc))
            {
                continue;
            }

            for (int dx = -range; dx <= range; dx++)
            {
                for (int dy = -range; dy <= range; dy++)
                {
                    if (dx == 0 && dy == 0)
                    {
                        continue;
                    }

                    renderTileIfValidForMovement(graphics, npc, dx, dy);
                }
            }
        }
    }

    /**
     * Adapted from net.runelite.client.plugins.devtools.SceneOverlay
     *
     * @param graphics
     * @param actor
     * @param dx
     * @param dy
     */
    private void renderTileIfValidForMovement(Graphics2D graphics, Actor actor, int dx, int dy)
    {
        WorldArea area = actor.getWorldArea();

        if (area == null)
        {
            return;
        }

        if (area.canTravelInDirection(client, dx, dy))
        {
            LocalPoint lp = actor.getLocalLocation();

            if (lp == null)
            {
                return;
            }

            lp = new LocalPoint(
                    lp.getX() + dx * Perspective.LOCAL_TILE_SIZE + dx * Perspective.LOCAL_TILE_SIZE * (area.getWidth() - 1) / 2,
                    lp.getY() + dy * Perspective.LOCAL_TILE_SIZE + dy * Perspective.LOCAL_TILE_SIZE * (area.getHeight() - 1) / 2);

            Polygon poly = Perspective.getCanvasTilePoly(client, lp);

            if (poly == null)
            {
                return;
            }

            if (isExplodingAnimation(actor))
            {
                renderPolygon(graphics, poly, 1, plugin.getExplosionBorderColor(), plugin.getExplosionFillColor());
            }
            else if (plugin.getSpawnOverlayConfig().equals(LizardmanShamanConfig.SpawnOverlayConfig.ALWAYS))
            {
                renderPolygon(graphics, poly, 1, plugin.getSpawnWalkableBorderColor(), plugin.getSpawnWalkableFillColor());
            }
        }
    }


    /**
     * Adapted from net.runelite.client.ui.overlay.OverlayUtil
     *
     * @param graphics
     * @param poly
     * @param strokeWidth
     * @param strokeColor
     * @param fillColor
     */
    private static void renderPolygon(Graphics2D graphics, Shape poly, int strokeWidth, Color strokeColor, Color fillColor)
    {
        graphics.setColor(strokeColor);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(fillColor);
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    /**
     * Returns true if the Actor's animation is exploding.
     *
     * @param actor an Actor object.
     * @return true if the Actor is exploding, else returns false.
     */
    private static boolean isExplodingAnimation(Actor actor) {
        final int NPC_ID_EXPLOSION = 7159;
        NPC spawn = (NPC) actor;
        int animationID = EthanApiPlugin.getAnimation(spawn);

        return animationID == NPC_ID_EXPLOSION;
    }

    /**
     * Returns true if the NPC is a Lizardman Shaman Spawn.
     *
     * @param npc an NPC object.
     * @return true if the NPC is a lizardman shaman spawn, else returns false.
     */
    private static boolean isSpawnNpc(NPC npc)
    {
        final int NPC_ID_SPAWN = 6768;

        return npc.getId() == NPC_ID_SPAWN;
    }

    // Returns true if the player is in the Lizardman Temple setting spawn tiles to 3x3 else spawn tiles are 5x5
    private boolean isInLizardmanTemple()
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }

        WorldPoint worldPoint = player.getWorldLocation();
        if (worldPoint == null)
        {
            return false;
        }

        int regionId = worldPoint.getRegionID();
        return LIZARDMAN_TEMPLE_REGIONS.contains(regionId);
    }
}
