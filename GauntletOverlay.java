/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.gauntlet;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GauntletOverlay extends Overlay {

    private final Client client;
    private final GauntletPlugin plugin;
    private final GauntletConfig config;

    private static final int MAX_DISTANCE = 2350;

    @Inject
    private GauntletOverlay(Client client, GauntletPlugin plugin, GauntletConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.inBoss()) {
            if (config.showAttackStyle()) {
                for (Projectile projectile : this.client.getProjectiles()) {
                    BufferedImage icon;
                    switch (projectile.getId()) {
                        case 1707:
                        case 1708:
                            icon = plugin.getATTACK_MAGE();
                            break;
                        case 1711:
                        case 1712:
                            icon = plugin.getATTACK_RANGE();
                            break;
                        case 1713:
                        case 1714:
                            icon = plugin.getATTACK_PRAYER();
                            break;
                        default:
                            icon = null;
                            break;
                    }

                    if (icon == null)
                        continue;

                    int x = (int) projectile.getX();
                    int y = (int) projectile.getY();

                    LocalPoint point = new LocalPoint(x, y);
                    Point loc = Perspective.getCanvasImageLocation(client, point, icon, 0);

                    if (loc == null)
                        continue;

                    graphics.drawImage(icon, loc.getX(), loc.getY(), null);
                }
            }

            BufferedImage attackIcon = null;
            if (config.countBossAttacks()) {
                switch (plugin.getCurrentStyle()) {
                    case MAGIC:
                        attackIcon = plugin.getATTACK_MAGE();
                        break;
                    case RANGE:
                        attackIcon = plugin.getATTACK_RANGE();
                        break;
                    default:
                        attackIcon = plugin.getATTACK_PRAYER();
                        break;
                }
            }

            for (NPC npc : this.client.getNpcs()) {
                String name = npc.getName();
                if (name == null || !npc.getName().matches("(Crystalline|Corrupted) Hunllef"))
                    continue;

                if (config.overlayBoss()) {
                    Polygon polygon = npc.getConvexHull();

                    if (polygon != null) {
                        Color color;
                        switch (plugin.getCurrentStyle()) {
                            case MAGIC:
                                color = Color.CYAN;
                                break;
                            case RANGE:
                                color = Color.GREEN;
                                break;
                            default:
                                color = Color.WHITE;
                                break;
                        }

                        graphics.draw(polygon);
                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                        graphics.fill(polygon);
                    }
                }

                if (attackIcon != null) {
                    LocalPoint point = npc.getLocalLocation();
                    Point imageLoc = Perspective.getCanvasImageLocation(client, point, attackIcon, npc.getLogicalHeight() / 2);

                    if (imageLoc == null)
                        continue;

                    graphics.drawImage(attackIcon, imageLoc.getX(), imageLoc.getY(), null);

                    String message = Integer.toString(plugin.getAttacksLeft());
                    if (config.countPlayerAttacks()) {
                        message += " | " + plugin.getPlayerCounter();
                    }

                    Point textLoc = Perspective.getCanvasTextLocation(client, graphics, point, message, npc.getLogicalHeight() / 2);

                    if (textLoc == null)
                        continue;

                    textLoc = new Point(textLoc.getX(), textLoc.getY() + 35);

                    Font oldFont = graphics.getFont();

                    graphics.setFont(new Font("Arial", Font.BOLD, 20));
                    Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);

                    OverlayUtil.renderTextLocation(graphics, pointShadow, message, Color.BLACK);
                    OverlayUtil.renderTextLocation(graphics, textLoc, message, Color.CYAN);

                    graphics.setFont(oldFont);
                }
            }
        } else {
            if (config.highlightResources()) {
                LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
                Point mousePosition = client.getMouseCanvasPosition();

                for (TileObject object : plugin.getResources().keySet()) {
                    Tile tile = plugin.getResources().get(object);
                    if (tile.getPlane() == client.getPlane()
                            && object.getLocalLocation().distanceTo(playerLocation) < MAX_DISTANCE) {
//                        Area objectClickbox = object.getClickbox(); // Don't use Convex Hull; you'll lag.

                        Polygon polygon = object.getCanvasTilePoly();
                        if (polygon != null) {
                            Color color = SystemColor.YELLOW;
                            BufferedImage icon;

                            if (config.highlightResourcesIcons())
                                switch (object.getId()) {
                                    case 36064: // Crystal Deposit
                                    case 35967:
                                        icon = plugin.getCRYSTAL_DEPOSIT();
                                        break;
                                    case 36066: // Phren Roots
                                    case 35969:
                                        icon = plugin.getPHREN_ROOTS();
                                        break;
                                    case 36068: // Fishing Spot
                                    case 35971:
                                        icon = plugin.getFISHING_SPOT();
                                        break;
                                    case 36070: // Grym Root
                                    case 35973:
                                        icon = plugin.getGRYM_ROOT();
                                        break;
                                    case 36072: // Linum Tirinum
                                    case 35975:
                                        icon = plugin.getLINUM_TIRINUM();
                                        break;
                                    default:
                                        icon = null;
                                        break;
                                }
                            else
                                icon = null;

                            graphics.setColor(color);

                            graphics.draw(polygon);
                            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                            graphics.fill(polygon);

                            if (icon != null) {
                                Rectangle bounds = polygon.getBounds();
                                int startX = (int) bounds.getCenterX() - (icon.getWidth() / 2);
                                int startY = (int) bounds.getCenterY() - (icon.getHeight() / 2);
                                graphics.drawImage(icon, startX, startY, null);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
