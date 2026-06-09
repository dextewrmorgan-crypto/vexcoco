package com.nnpg.glazed.modules.esp;

import com.nnpg.glazed.GlazedAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class BetterStorageESP extends Module {
    // ... (existing code remains unchanged)

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (this.mc.world == null || this.mc.player == null) return;
        
        Vec3d playerPos = this.mc.player.getEyePos(event.tickDelta);
        BlockPos playerChunkPos = this.mc.player.getChunkPos();
        int viewDistChunks = this.mc.options.getViewDistance().getValue();
        double s = 0.15;

        for (int x = playerChunkPos.x - viewDistChunks; x <= playerChunkPos.x + viewDistChunks; x++) {
            for (int z = playerChunkPos.z - viewDistChunks; z <= playerChunkPos.z + viewDistChunks; z++) {
                WorldChunk chunk = this.mc.world.getChunk(x, z);
                if (chunk == null) continue;

                for (BlockPos pos : chunk.getBlockEntities()) {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    if (blockEntity == null) continue;

                    SettingColor settingColor = getBlockEntityColor(blockEntity);
                    if (settingColor == null) continue;

                    Color color = new Color(settingColor.r, settingColor.g, settingColor.b, this.alpha.get());
                    
                    try (var renderer = event.renderer) {
                        renderer.box(
                            pos.getX() + s, pos.getY() + s, pos.getZ() + s,
                            pos.getX() + 1 - s, pos.getY() + 1 - s, pos.getZ() + 1 - s,
                            color, color, this.shapeMode.get(), 0
                        );
                        
                        if (!this.tracers.get()) continue;
                        
                        Vec3d blockCenter = Vec3d.ofCenter(pos);
                        Vec3d startPos;
                        
                        if (this.mc.options.getPerspective().isFirstPerson()) {
                            Vec3d lookDirection = this.mc.player.getRotationVector();
                            startPos = playerPos.add(lookDirection.multiply(0.5));
                        } else {
                            startPos = playerPos.withY(playerPos.y + this.mc.player.getStandingEyeHeight());
                        }
                        
                        renderer.line(startPos, blockCenter, color);
                    }
                }
            }
        }
    }

    private SettingColor getBlockEntityColor(BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity && this.chests.get()) {
            return this.chestColor.get();
        } else if (blockEntity instanceof EnderChestBlockEntity && this.enderChests.get()) {
            return this.enderChestColor.get();
        } else if (blockEntity instanceof ShulkerBoxBlockEntity && this.shulkerBoxes.get()) {
            return this.shulkerBoxColor.get();
        } else if (blockEntity instanceof MobSpawnerBlockEntity && this.spawners.get()) {
            return this.spawnerColor.get();
        } else if (blockEntity instanceof AbstractFurnaceBlockEntity && this.furnaces.get()) {
            return this.furnaceColor.get();
        } else if (blockEntity instanceof BarrelBlockEntity && this.barrels.get()) {
            return this.barrelColor.get();
        } else if (blockEntity instanceof EnchantingTableBlockEntity && this.enchantingTables.get()) {
            return this.enchantColor.get();
        } else if (blockEntity instanceof PistonBlockEntity && this.pistons.get()) {
            return this.pistonColor.get();
        } else if (blockEntity instanceof HopperBlockEntity && this.hoppers.get()) {
            return this.hopperColor.get();
        }
        return null;
    }
}
