/*
 * Copyright 2024 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.nocapes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.terminalmc.nocapes.NoCapes;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static dev.terminalmc.nocapes.config.Config.options;

@Mixin(WingsLayer.class)
public class MixinWingsLayer {
    @WrapOperation(
            method = "getPlayerElytraTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/PlayerSkin;capeTexture()Lnet/minecraft/resources/ResourceLocation;"
            )
    )
    private static @Nullable ResourceLocation wrapCapeTexture(PlayerSkin instance, Operation<ResourceLocation> original) {
        ResourceLocation texture = original.call(instance);
        if (options().hideElytra && NoCapes.blockCape(texture)) return null;
        return texture;
    }
}
