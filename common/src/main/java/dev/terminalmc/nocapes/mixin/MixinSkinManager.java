/*
 * Copyright 2025 TerminalMC
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
import net.minecraft.core.ClientAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(SkinManager.class)
public class MixinSkinManager {
    @WrapOperation(
            method = "registerTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager$TextureCache;getOrLoad(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)Ljava/util/concurrent/CompletableFuture;",
                    ordinal = 1
            )
    )
    private CompletableFuture<ClientAsset.Texture> wrapLoadCape(
            SkinManager.TextureCache instance, MinecraftProfileTexture texture,
            Operation<CompletableFuture<ClientAsset.Texture>> original) {
        return noCapes$wrapLoadTexture(instance, texture, original);
    }

    @WrapOperation(
            method = "registerTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/SkinManager$TextureCache;getOrLoad(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)Ljava/util/concurrent/CompletableFuture;",
                    ordinal = 2
            )
    )
    private CompletableFuture<ClientAsset.Texture> wrapLoadElytra(
            SkinManager.TextureCache instance, MinecraftProfileTexture texture,
            Operation<CompletableFuture<ClientAsset.Texture>> original) {
        return noCapes$wrapLoadTexture(instance, texture, original);
    }

    @Unique
    private CompletableFuture<ClientAsset.Texture> noCapes$wrapLoadTexture(
            SkinManager.TextureCache instance, MinecraftProfileTexture texture,
            Operation<CompletableFuture<ClientAsset.Texture>> original) {
        return original.call(instance, texture).thenApply((asset) -> {
            String hash = texture.getHash();
            if (!NoCapes.RESOURCE_CAPE_CACHE.containsKey(asset.texturePath())) {
                NoCapes.RESOURCE_CAPE_CACHE.put(asset.texturePath(), hash);
                NoCapes.checkInConfig(hash, "https://textures.minecraft.net/texture/" + hash);
            }
            return asset;
        });
    }
}
