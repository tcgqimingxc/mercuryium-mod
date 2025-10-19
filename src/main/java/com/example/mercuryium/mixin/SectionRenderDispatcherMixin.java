package com.example.mercuryium.mixin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.example.mercuryium.AsyncRenderingManager;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionRenderDispatcher")
public abstract class SectionRenderDispatcherMixin {
    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/concurrent/Executors;newFixedThreadPool(ILjava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ExecutorService;"
        )
    )
    private ExecutorService mercuryium$useCustomChunkExecutor(int nThreads, ThreadFactory factory) {
        ExecutorService custom = AsyncRenderingManager.getChunkBuilderExecutor();
        if (custom != null) {
            return custom;
        }
        return Executors.newFixedThreadPool(nThreads, factory);
    }
}


