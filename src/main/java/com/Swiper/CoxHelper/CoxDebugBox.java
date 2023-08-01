package com.Swiper.CoxHelper;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Dimension;
import java.awt.Graphics2D;

@Singleton
public class CoxDebugBox extends Overlay
{
    private final Client client;
    private final CoxPlugin plugin;
    private final CoxConfig config;
    private final Olm olm;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    CoxDebugBox(Client client, CoxPlugin plugin, CoxConfig config, Olm olm)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.olm = olm;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.setPriority(OverlayPriority.HIGH);
        this.panelComponent.setPreferredSize(new Dimension(270, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!this.config.olmDebug() || !plugin.inRaid())
        {
            return null;
        }

        this.panelComponent.getChildren().clear();

        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("ticks").right(String.valueOf(client.getTickCount())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("active").right(String.valueOf(this.olm.isActive())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("handAnim").right(String.valueOf(this.olm.getHandAnimation())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("headAnim").right(String.valueOf(this.olm.getHeadAnimation())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("firstPhase").right(String.valueOf(this.olm.isFirstPhase())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("finalPhase").right(String.valueOf(this.olm.isFinalPhase())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("attackTicks").right(String.valueOf(this.olm.getTicksUntilNextAttack())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("attackCycle").right(String.valueOf(this.olm.getAttackCycle())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("specialCycle").right(String.valueOf(this.olm.getSpecialCycle())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("portalTicks").right(String.valueOf(this.olm.getPortalTicks())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("handCrippled").right(String.valueOf(this.olm.isCrippled())).build());
        this.panelComponent.getChildren().add(LineComponent.builder()
                .left("crippleTicks").right(String.valueOf(this.olm.getCrippleTicks())).build());

        return this.panelComponent.render(graphics);
    }
}