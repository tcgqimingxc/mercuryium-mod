package com.example.mercuryium.mixin;

import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.example.mercuryium.AsyncRenderingManager;

@Mixin(targets = "net.minecraft.server.packs.resources.ReloadableResourceManager")
public abstract class ReloadableResourceManagerMixin {
    @ModifyArg(
        method = "createReload(IZLjava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Lnet/minecraft/server/packs/resources/ReloadInstance;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ReloadInstance;of(ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Lnet/minecraft/server/packs/resources/PreparableReloadListener$ReloadStateFactory;)Lnet/minecraft/server/packs/resources/ReloadInstance;"
        ),
        index = 1
    )
    private Executor mercuryium$useCustomPrepareExecutor(Executor original) {
        Executor exec = AsyncRenderingManager.getAssetReloadExecutor();
        return exec != null ? exec : original;
    }

    @ModifyArg(
        method = "createReload(IZLjava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Lnet/minecraft/server/packs/resources/ReloadInstance;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/resources/ReloadInstance;of(ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Lnet/minecraft/server/packs/resources/PreparableReloadListener$ReloadStateFactory;)Lnet/minecraft/server/packs/resources/ReloadInstance;"
        ),
        index = 2
    )
    private Executor mercuryium$useCustomApplyExecutor(Executor original) {
        Executor exec = AsyncRenderingManager.getAssetReloadExecutor();
        return exec != null ? exec : original;
    }
}


