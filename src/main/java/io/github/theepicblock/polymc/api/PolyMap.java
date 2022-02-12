/*
 * PolyMc
 * Copyright (C) 2020-2020 TheEpicBlock_TEB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.theepicblock.polymc.api;

import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.gui.GuiPoly;
import io.github.theepicblock.polymc.api.item.ItemLocation;
import io.github.theepicblock.polymc.api.item.ItemPoly;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.mixins.gui.GuiPolyImplementation;
import io.github.theepicblock.polymc.mixins.item.CreativeItemStackFix;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PolyMap {
    /**
     * Converts the serverside representation of an item into a clientside one that should be sent to the client.
     */
    ItemStack getClientItem(ItemStack serverItem, @Nullable ServerPlayerEntity player, @Nullable ItemLocation location);

    /**
     * Converts the serverside representation of a block into a clientside one that should be sent to the client.
     */
    default BlockState getClientBlock(BlockState serverBlock) {
        BlockPoly poly = this.getBlockPoly(serverBlock.getBlock());
        if (poly == null) return serverBlock;

        return poly.getClientBlock(serverBlock);
    }

    /**
     * Get the RawId of the client-state block
     */
    default int getClientStateRawId(BlockState state, ServerPlayerEntity playerEntity) {
        BlockState clientState = this.getClientBlock(state);
        return Block.STATE_IDS.getRawId(clientState);
    }

    /**
     * Get the integer rawId of the Item we'll be sending to the client
     */
    default int getClientItemRawId(Item item, ServerPlayerEntity playerEntity) {
        return Item.getRawId(item);
    }

    /**
     * Get the Item that is known by this integer rawId on the client
     */
    default Item reverseClientItemRawId(int rawId, ServerPlayerEntity playerEntity) {
        return Item.byRawId(rawId);
    }

    /**
     * @return the {@link ItemPoly} that this PolyMap associates with this {@link Item}.
     */
    ItemPoly getItemPoly(Item item);

    /**
     * @return the {@link BlockPoly} that this PolyMap associates with this {@link Block}.
     */
    BlockPoly getBlockPoly(Block block);

    /**
     * @return the {@link GuiPoly} that this PolyMap associates with this {@link ScreenHandlerType}.
     */
    GuiPoly getGuiPoly(ScreenHandlerType<?> serverGuiType);

    /**
     * @return the {@link EntityPoly} that this PolyMap associates with this {@link EntityType}.
     */
    <T extends Entity> EntityPoly<T> getEntityPoly(EntityType<T> entity);

    /**
     * Reverts the clientside item into the serverside representation.
     * This should be the reverse of {@link #getClientItem(ItemStack, ServerPlayerEntity, ItemLocation)}.
     * For optimization reasons, this method only needs to be implemented for items gained by players in creative mode.
     * @see CreativeItemStackFix
     */
    ItemStack reverseClientItem(ItemStack clientItem);

    /**
     * Specifies if this map is meant for vanilla-like clients
     * This is used to disable/enable miscellaneous patches
     * @see io.github.theepicblock.polymc.mixins.block.BlockBreakingPatch
     * @see io.github.theepicblock.polymc.mixins.CustomPacketDisabler
     * @see io.github.theepicblock.polymc.mixins.tag.SerializedMixin
     * @see io.github.theepicblock.polymc.mixins.block.ResyncImplementation
     * @see io.github.theepicblock.polymc.impl.mixin.CustomBlockBreakingCheck#needsCustomBreaking(ServerPlayerEntity, Block)
     * @see GuiPolyImplementation
     * @see io.github.theepicblock.polymc.mixins.item.CustomRecipeFix
     */
    boolean isVanillaLikeMap();

    @Nullable PolyMcResourcePack generateResourcePack(SimpleLogger logger);

    String dumpDebugInfo();
}
