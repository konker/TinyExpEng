package com.luxvelocitas.tinyexpeng.runner.taskgroup;


import com.luxvelocitas.tinyexpeng.runner.TaskThread;

public class StaggeredSequentialConcurrentTaskGroupRunner extends SequentialConcurrentTaskGroupRunner implements ITaskGroupRunner {
    final int mStaggerDelayMs;

    public StaggeredSequentialConcurrentTaskGroupRunner(int staggerDelayMs) {
        mStaggerDelayMs = staggerDelayMs;
    }

    @Override
    protected void startThreads() {
        // Start all the TaskThreads
        for (TaskThread t : mThreadGroup) {
            t.start();

            try {
                Thread.sleep(mStaggerDelayMs);
            }
            catch (InterruptedException ex) {
                mLogger.error("Error in staggering concurrent threads", ex);
            }
        }
    }
}
