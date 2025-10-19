package com.example.mercuryium;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_ASYNC_CHUNK_REBUILD = BUILDER
            .comment("Enable extra background threads to rebuild chunk meshes asynchronously")
            .define("enableAsyncChunkRebuild", true);

    public static final ModConfigSpec.IntValue CHUNK_BUILDER_THREADS = BUILDER
            .comment("Number of threads for chunk mesh building; 0=auto based on CPU cores")
            .defineInRange("chunkBuilderThreads", 0, 0, 64);

    public static final ModConfigSpec.IntValue ASSET_RELOAD_PARALLELISM = BUILDER
            .comment("Parallelism for client asset/model reload; 0=auto")
            .defineInRange("assetReloadParallelism", 0, 0, 64);

    static final ModConfigSpec SPEC = BUILDER.build();
}


