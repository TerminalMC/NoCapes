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
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import dev.terminalmc.nocapes.NoCapes;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Mixin(SkinManager.class)
public class MixinSkinManager {
    @WrapOperation(
            method = "registerTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager$TextureCache;getOrLoad(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<ResourceLocation> wrapPlayerSkinInit(
            SkinManager.TextureCache instance, MinecraftProfileTexture texture, 
            Operation<CompletableFuture<ResourceLocation>> original) {
        return original.call(instance, texture).thenApply((location) -> {
            String hash = texture.getHash();
            if (
                    !NoCapes.CAPE_CACHE.containsKey(location) 
                    && Arrays.asList(NoCapes.CAPES).contains(hash)
            ) {
                NoCapes.CAPE_CACHE.put(location, hash);
            }
            return location;
        });
    }
}
