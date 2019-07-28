/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.gauntlet;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SoundEffectVolume;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.DecorativeObjectChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WallObjectChanged;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PluginDescriptor(
        name = "Gauntlet",
        description = "All-in-one plugin for the Gauntlet.",
        tags = {"Gauntlet"},
        enabledByDefault = false
)

public class GauntletPlugin extends Plugin {

    @Getter
    private BufferedImage CRYSTAL_DEPOSIT;

    @Getter
    private BufferedImage PHREN_ROOTS;

    @Getter
    private BufferedImage FISHING_SPOT;

    @Getter
    private BufferedImage GRYM_ROOT;

    @Getter
    private BufferedImage LINUM_TIRINUM;

    @Getter
    private BufferedImage ATTACK_RANGE;

    @Getter
    private BufferedImage ATTACK_MAGE;

    @Getter
    private BufferedImage ATTACK_PRAYER;

    @Inject
    private Client client;

    @Getter(AccessLevel.PUBLIC)
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletOverlay overlay;

    @Inject
    private GauntletConfig config;

    @Getter
    private final Map<TileObject, Tile> resources = new HashMap<>();

    @Getter
    private Set<Projectile> projectiles = new HashSet<>();

    @Provides
    GauntletConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(GauntletConfig.class);
    }

    @Getter
    @Setter
    private int attacksLeft;

    @Getter
    @Setter
    private int playerCounter;

    @Getter
    @Setter
    private Style currentStyle;

    public static enum Style {
        MAGIC, RANGE, UNKNOWN;
    }

    public static enum Attack {
        MAGIC, RANGE, PRAYER, LIGHTNING;
    }

    @Override
    protected void startUp() {
        CRYSTAL_DEPOSIT = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/mining.png");
        PHREN_ROOTS = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/woodcutting.png");
        FISHING_SPOT = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/fishing.png");
        GRYM_ROOT = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/herblore.png");
        LINUM_TIRINUM = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/farming.png");

        ATTACK_MAGE = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/magic.png");
        ATTACK_RANGE = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/ranged.png");
        ATTACK_PRAYER = ImageUtil.getResourceStreamFromClass(getClass(), "/skill_icons/prayer.png");

        playerCounter = 6;
        attacksLeft = 0;
        currentStyle = Style.UNKNOWN;
        projectiles.clear();

        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        playerCounter = 6;
        attacksLeft = 0;
        currentStyle = Style.UNKNOWN;
        projectiles.clear();

        overlayManager.remove(overlay);

        CRYSTAL_DEPOSIT = null;
        PHREN_ROOTS = null;
        FISHING_SPOT = null;
        GRYM_ROOT = null;
        LINUM_TIRINUM = null;

        ATTACK_MAGE = null;
        ATTACK_RANGE = null;
        ATTACK_PRAYER = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc == null || npc.getName() == null || !npc.getName().matches("(Crystalline|Corrupted) Hunllef"))
            return;

        playerCounter = 6;
        attacksLeft = 0;
        currentStyle = Style.UNKNOWN;
        projectiles.clear();
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc == null || npc.getName() == null || !npc.getName().matches("(Crystalline|Corrupted) Hunllef"))
            return;

        playerCounter = 6;
        attacksLeft = 0;
        currentStyle = Style.UNKNOWN;
        projectiles.clear();
    }

    public void doAttack(Attack style) {
        if (style == Attack.PRAYER) {
            if (config.alertPrayerDrain()) {
                client.playSoundEffect(SoundEffectID.MAGIC_SPLASH_BOING, SoundEffectVolume.MEDIUM_HIGH);
            }
            style = Attack.MAGIC;
        }

        if (style == Attack.LIGHTNING) {
            attacksLeft--;
        } else if (style == Attack.RANGE) {
            if (currentStyle != Style.RANGE) {
                currentStyle = Style.RANGE;
                attacksLeft = 3;
            } else {
                attacksLeft--;
            }
        } else if (style == Attack.MAGIC) {
            if (currentStyle != Style.MAGIC) {
                currentStyle = Style.MAGIC;
                attacksLeft = 3;
            } else {
                attacksLeft--;
            }
        }

        if (attacksLeft <= 0) {
            Style newStyle;

            switch (currentStyle) {
                case MAGIC:
                    attacksLeft = 4;
                    newStyle = Style.RANGE;
                    break;
                case RANGE:
                    attacksLeft = 4;
                    newStyle = Style.MAGIC;
                    break;
                default:
                    attacksLeft = 0;
                    newStyle = Style.UNKNOWN;
                    break;
            }

            currentStyle = newStyle;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();

        if (actor instanceof Player && this.inBoss()) {
            Player p = (Player) actor;
            if (p.getName().equals(client.getLocalPlayer().getName())) {
                int id = p.getAnimation();
                if (id != -1) {
                    int[] all_styles = new int[]{395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440, 426, 1167};
                    int[] wrong_style = new int[]{};

                    for (NPC npc : this.client.getNpcs()) {
                        if (npc != null && npc.getName() != null && npc.getName().matches("(Crystalline|Corrupted) Hunllef")) {
                            NPCComposition comp = npc.getComposition();
                            if (comp != null) {
                                HeadIcon prayer = comp.getOverheadIcon();
                                if (prayer != null) {
                                    switch (prayer) {
                                        case MELEE:
                                            wrong_style = new int[]{
                                                    395, // Axe Slash
                                                    401, // Axe Crush

                                                    400, // Pick Crush
                                                    401, // Pick Stab

                                                    386, // Harpoon Stab
                                                    390, // Harpoon Slash

                                                    422, // Unarmed Punch
                                                    423, // Unarmed Kick

                                                    401, // Crystal Scepter
                                                    428, // Crystal Halberd Jab & Fend
                                                    440 // Crystal Halberd Swipe
                                            };
                                            break;
                                        case RANGED:
                                            wrong_style = new int[]{
                                                    426 // Crystal Bow
                                            };
                                            break;
                                        case MAGIC:
                                            wrong_style = new int[]{
                                                    1167 // Crystal Staff
                                            };
                                            break;
                                        default:
                                            wrong_style = new int[]{};
                                            break;
                                    }

                                    break;
                                }
                            }
                        }
                    }

                    outerloop:
                    for (int action : all_styles) {
                        if (action == id) {
                            for (int wrong_action : wrong_style) {
                                if (action == wrong_action)
                                    break outerloop;
                            }

                            playerCounter--;
                            if (playerCounter <= 0) {
                                playerCounter = 6;
                            }

                            break outerloop;
                        }
                    }
                }
            }
        }

        if (actor instanceof NPC) {
            NPC npc = (NPC) actor;
            if (npc != null && npc.getName() != null && npc.getName().matches("(Crystalline|Corrupted) Hunllef")) {
                int id = npc.getAnimation();
                if (id == 8418) {
                    this.doAttack(Attack.LIGHTNING);
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Set<Projectile> newProjectiles = new HashSet<>();
        for (Projectile projectile : client.getProjectiles()) {
            newProjectiles.add(projectile);

            if (!projectiles.contains(projectile)) {
                int id = projectile.getId();
                if (id == 1707 || id == 1708) {
                    this.doAttack(Attack.MAGIC);
                } else if (id == 1713 || id == 1714) {
                    this.doAttack(Attack.PRAYER);
                } else if (id == 1711 || id == 1712) {
                    this.doAttack(Attack.RANGE);
                }
            }
        }

        projectiles.clear();
        projectiles = newProjectiles;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        onTileObject(event.getTile(), null, event.getGameObject());
    }

    @Subscribe
    public void onGameObjectChanged(GameObjectChanged event) {
        onTileObject(event.getTile(), event.getPrevious(), event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        onTileObject(event.getTile(), event.getGameObject(), null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        onTileObject(event.getTile(), null, event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectChanged(GroundObjectChanged event) {
        onTileObject(event.getTile(), event.getPrevious(), event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        onTileObject(event.getTile(), event.getGroundObject(), null);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        onTileObject(event.getTile(), null, event.getWallObject());
    }

    @Subscribe
    public void onWallObjectChanged(WallObjectChanged event) {
        onTileObject(event.getTile(), event.getPrevious(), event.getWallObject());
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event) {
        onTileObject(event.getTile(), event.getWallObject(), null);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        onTileObject(event.getTile(), null, event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectChanged(DecorativeObjectChanged event) {
        onTileObject(event.getTile(), event.getPrevious(), event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        onTileObject(event.getTile(), event.getDecorativeObject(), null);
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            resources.clear();
        }
    }

    private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject) {
        resources.remove(oldObject);

        if (newObject == null) {
            return;
        }

        int id = newObject.getId();

        int[] ids = {
                36068, // Fishing Spot (Harpoon)
                35967,
                36066, // Phren Roots (Axe)
                35969,
                36070, // Grym Root
                35971,
                36072, // Linum Tirinum
                35973,
                36064, // Crystal Deposit
                35975
        };

        for (int i : ids) {
            if (i == id) {
                resources.put(newObject, tile);
            }
        }
    }

    public boolean inBoss() {
        return client.getVarbitValue(client.getVarps(), 9177) == 1;
    }
}
