package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class ThrottlingChunkTaskDispatcher extends ChunkTaskDispatcher {
    private final LongSet chunkPositionsInExecution = new LongOpenHashSet();
    private final int maxChunksInExecution;
    private final String executorSchedulerName;

    public ThrottlingChunkTaskDispatcher(TaskScheduler<Runnable> p_364785_, Executor p_366572_, int p_363868_) {
        super(p_364785_, p_366572_);
        this.maxChunksInExecution = p_363868_;
        this.executorSchedulerName = p_364785_.name();
    }

    @Override
    protected void onRelease(long p_362718_) {
        this.chunkPositionsInExecution.remove(p_362718_);
    }

    @Nullable
    @Override
    protected ChunkTaskPriorityQueue.TasksForChunk popTasks() {
        return this.chunkPositionsInExecution.size() < this.maxChunksInExecution ? super.popTasks() : null;
    }

    @Override
    protected void scheduleForExecution(ChunkTaskPriorityQueue.TasksForChunk p_369642_) {
        this.chunkPositionsInExecution.add(p_369642_.chunkPos());
        super.scheduleForExecution(p_369642_);
    }

    @VisibleForTesting
    public String getDebugStatus() {
        return this.executorSchedulerName
            + "=["
            + this.chunkPositionsInExecution.stream().map(p_362789_ -> p_362789_ + ":" + new ChunkPos(p_362789_)).collect(Collectors.joining(","))
            + "], s="
            + this.sleeping;
    }
}