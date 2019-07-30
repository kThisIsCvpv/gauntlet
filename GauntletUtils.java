package net.runelite.client.plugins.gauntlet;

import net.runelite.api.Client;
import net.runelite.api.NPC;

public class GauntletUtils {

    public static final int[] MELEE_ANIMATIONS = new int[]{
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

    public static final int[] RANGE_ANIMATIONS = new int[]{
            426 // Crystal Bow
    };

    public static final int[] MAGE_ANIMATIONS = new int[]{
            1167 // Crystal Staff
    };

    public static final int[] PLAYER_ANIMATIONS = concatIntArray(MELEE_ANIMATIONS, RANGE_ANIMATIONS, MAGE_ANIMATIONS);

    public static final int[] CRYSTAL_DEPOSIT = new int[]{
            36064, // Normal Mining Spot
            35975 // Corrupted Mining Spot
    };

    public static final int[] PHREN_ROOTS = new int[]{
            36066, // Normal Woodcutting Spot
            35969 // Corrupted Woodcutting Spot
    };

    public static final int[] FISHING_SPOTS = new int[]{
            36068, // Normal Fishing Spot
            35967 // Corrupted Fishing Spot
    };
    public static final int[] GRYM_ROOTS = new int[]{
            36070, // Normal Herblore Spot
            35971 // Corrupted Herblore Spot
    };

    public static final int[] LINUM_TIRINUM = new int[]{
            36072, // Normal Farming Spot
            35973, // Corrupted Farming Spot
    };

    public static final int[] RESOURCE_IDS = concatIntArray(CRYSTAL_DEPOSIT, PHREN_ROOTS, FISHING_SPOTS, GRYM_ROOTS, LINUM_TIRINUM);

    public static final int[] PROJECTILE_MAGIC = new int[]{1707, 1708};
    public static final int[] PROJECTILE_RANGE = new int[]{1711, 1712};
    public static final int[] PROJECTILE_PRAYER = new int[]{1713, 1714};

    public static final int VARP_BOSS_ROOM = 9177;
    public static final int VARP_RAID_ROOM = 9178;

    public static final int BOSS_ANIMATION_LIGHTNING = 8418;

    public static final int[] TORNADO_IDS = new int[]{9025, 9039};
    public static final int TORNADO_TICKS = 20;

    /**
     * Concatenate all integers in the array into a single array.
     *
     * @param arrays array of int arrays
     * @return int array
     */
    public static int[] concatIntArray(int[]... arrays) {
        int length = 0;

        for (int[] array : arrays) {
            length += array.length;
        }

        int[] returnArray = new int[length];
        int currentIndex = 0;

        for (int[] array : arrays) {
            for (int value : array) {
                returnArray[currentIndex] = value;
                currentIndex++;
            }
        }

        return returnArray;
    }

    /**
     * Determines whether or not an integer array contains a specific value.
     *
     * @param intArray int[]
     * @param value    int
     * @return boolean
     */
    public static boolean arrayContainsInteger(int[] intArray, int value) {
        for (int i : intArray)
            if (i == value)
                return true;
        return false;
    }

    /**
     * Returns whether or not a NPC is a tornado.
     *
     * @param npc NPC
     * @return boolean
     */
    public static boolean isTornado(NPC npc) {
        return npc != null && arrayContainsInteger(TORNADO_IDS, npc.getId());
    }

    /**
     * Returns whether or not a NPC is the boss.
     *
     * @param npc NPC
     * @return boolean
     */
    public static boolean isBoss(NPC npc) {
        return npc != null && npc.getName() != null && npc.getName().matches("(Crystalline|Corrupted) Hunllef");
    }

    /**
     * Returns whether or not the player is current in the Gauntlet.
     *
     * @param client Client
     * @return boolean
     */
    public static boolean inRaid(Client client) {
        return client.getVarbitValue(client.getVarps(), VARP_RAID_ROOM) == 1;
    }

    /**
     * Returns whether or not the player is currently in the boss room.
     *
     * @param client Client
     * @return boolean
     */
    public static boolean inBoss(Client client) {
        return client.getVarbitValue(client.getVarps(), VARP_BOSS_ROOM) == 1;
    }
}
