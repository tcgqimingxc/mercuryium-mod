package com.example.mercuryium;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public final class AsyncRenderingManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile ExecutorService chunkBuilderExecutor;
    private static volatile ExecutorService assetReloadExecutor;

    private AsyncRenderingManager() {}

    public static synchronized void initialize() {
        shutdown();

        if (ClientConfig.ENABLE_ASYNC_CHUNK_REBUILD.get()) {
            int desiredThreads = ClientConfig.CHUNK_BUILDER_THREADS.get();
            int threads = normalizeThreadCount(desiredThreads);
            chunkBuilderExecutor = newFixedThreadPool("mercuryium-chunk", threads);
            LOGGER.info("AsyncRendering: chunk builder threads = {} (requested: {})", threads, desiredThreads);
        } else {
            chunkBuilderExecutor = null;
            LOGGER.info("AsyncRendering: chunk builder disabled by config");
        }

        int desiredReload = ClientConfig.ASSET_RELOAD_PARALLELISM.get();
        int reloadThreads = Math.max(1, normalizeThreadCount(desiredReload));
        assetReloadExecutor = newFixedThreadPool("mercuryium-reload", reloadThreads);
        LOGGER.info("AsyncRendering: asset reload parallelism = {} (requested: {})", reloadThreads, desiredReload);
    }

    public static synchronized void shutdown() {
        shutdownExecutor(chunkBuilderExecutor, "chunk");
        chunkBuilderExecutor = null;
        shutdownExecutor(assetReloadExecutor, "reload");
        assetReloadExecutor = null;
    }

    public static ExecutorService getChunkBuilderExecutor() {
        return chunkBuilderExecutor;
    }

    public static ExecutorService getAssetReloadExecutor() {
        return assetReloadExecutor;
    }

    private static int normalizeThreadCount(int requested) {
        if (requested > 0) return requested;
        int cores = Runtime.getRuntime().availableProcessors();
        // Keep one core for main thread; cap to at least 1
        return Math.max(1, Math.max(1, cores - 1));
    }

    private static ExecutorService newFixedThreadPool(String prefix, int threads) {
        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName(prefix + "-" + System.identityHashCode(t));
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        };
        return Executors.newFixedThreadPool(threads, factory);
    }

    private static void shutdownExecutor(ExecutorService executor, String name) {
        if (executor == null) return;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
        LOGGER.info("AsyncRendering: {} executor shutdown", name);
    }
}


