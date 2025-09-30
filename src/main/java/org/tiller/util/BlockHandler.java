package org.tiller.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.tiller.Tiller;

import java.util.function.Consumer;

public class BlockHandler {
    private final Tiller plugin;
    private final boolean isFolia;

    public BlockHandler (Tiller plugin, boolean isFolia) {
        this.plugin = plugin;
        this.isFolia = isFolia;
    }

    public void handleBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // If player does not have permission, cancel auto replanting
        if (!player.hasPermission("tiller.use")) return;

        // Check if block is a plant
        if (!(block.getBlockData() instanceof Ageable cropBlock)) return;

        // If player is not in survival or adventure, return
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        // Check if the correct tool is used, or if the player has the ignore tool restrictions permission
        if (!plugin.getConfigManager().getAllowedItems().contains(tool.getType()) &&
                !player.hasPermission("tiller.ignore_tool_restrictions")) return;

        // Check player replant-state
        if (!plugin.getPlayerManager().getPlayerState(player.getUniqueId(), "auto-replant")) return;

        handleDrops(event);
        handleReplant(event, cropBlock);
        boolean cropIsUnripe = cropBlock.getAge() < cropBlock.getMaximumAge();
        handleToolDamage(player, tool, cropIsUnripe);
    }

    private void handleDrops(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!plugin.getConfigManager().get("use-fortune", Boolean.class)) {
            // Disable the default drops
            event.setDropItems(false);

            // Drop the default amount of items, ignoring the Fortune enchantment
            for (ItemStack drop : event.getBlock().getDrops(new ItemStack(Material.AIR))) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            }
        }
    }

    private void handleReplant(BlockBreakEvent event, Ageable cropBlock) {
        Block block = event.getBlock();
        boolean cropIsUnripe = cropBlock.getAge() < cropBlock.getMaximumAge();

        // Replant ripe crops or all crops if replanting unripe crops are enabled
        if (!cropIsUnripe || plugin.getConfigManager().get("replant-unripe-crops", Boolean.class)) {
            // Disable drops when replanting unripe crops
            if (cropIsUnripe) {
                event.setDropItems(false);
            }

            Material blockType = block.getType();

            Runnable replantTask = () -> {
                block.setType(blockType);
                cropBlock.setAge(0);
                block.setBlockData(cropBlock);
            };

            if (isFolia) {
                try {
                    // Reflectively get RegionScheduler
                    Object regionScheduler = Bukkit.getServer().getClass()
                            .getMethod("getRegionScheduler")
                            .invoke(Bukkit.getServer());

                    // Call runDelayed(Plugin, Location, Consumer<Task>, long)
                    regionScheduler.getClass()
                            .getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, long.class)
                            .invoke(regionScheduler, plugin, block.getLocation(), (Consumer<Object>) task -> replantTask.run(), 1L);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // Fallback to Bukkit/Paper scheduler
                plugin.getServer().getScheduler().runTaskLater(plugin, replantTask, 1);
            }
        }
    }

    private void handleToolDamage(Player player, ItemStack tool, boolean cropIsUnripe) {
        boolean shouldApplyItemDamage =
                !player.hasPermission("tiller.bypass_item_durability") &&
                        plugin.getConfigManager().get("item-damage", Boolean.class) &&
                        (!cropIsUnripe || plugin.getConfigManager().get("unripe-crop-item-damage", Boolean.class));

        if (shouldApplyItemDamage && tool.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + 1);
            tool.setItemMeta(damageable);

            if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.getInventory().setItemInMainHand(null);
            }
        }
    }
}
