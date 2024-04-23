package com.minenash.pickblockstate.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow public ClientPlayerEntity player;

	@Redirect(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPickStack(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"))
	public ItemStack getPickStack(Block block, WorldView world, BlockPos pos, BlockState state) {
		ItemStack stack = block.getPickStack(world, pos, state);

		if (!player.getAbilities().creativeMode || !Screen.hasControlDown() || state.hasBlockEntity())
			return stack;

		BlockStateComponent component = BlockStateComponent.DEFAULT;
		for (var p : state.getProperties())
			component = component.with(p, state);

		stack.applyComponentsFrom( ComponentMap.builder().add(DataComponentTypes.BLOCK_STATE, component).build() );
		return stack;

	}

}
