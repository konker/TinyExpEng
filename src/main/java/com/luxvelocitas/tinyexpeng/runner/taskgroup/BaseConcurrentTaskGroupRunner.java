package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.TaskThread;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public abstract class BaseConcurrentTaskGroupRunner extends AbstractTaskGroupRunner implements ITaskGroupRunner {
    protected Set<TaskThread> mThreadGroup;

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        super.start(logger, experimentRunContext, taskGroup);

        _init(experimentRunContext, taskGroup);

        // Create a Set to hold TaskThread references
        mThreadGroup = new CopyOnWriteArraySet<TaskThread>();

        createThreads(experimentRunContext, taskGroup);

        startThreads();

        joinThreads();

        // End of the TaskGroup
        experimentRunContext.removeRunContextEventListener(ExperimentEventType.TASK_END, mRunContextEventListener);

        mCurTaskGroup.complete(experimentRunContext);

        mThreadGroup.clear();
    }

    @Override
    public boolean hasStep() {
        return false;
    }

    @Override
    public void nextStep(ExperimentRunContext experimentRunContext) {
        /*[NOOP]*/
    }

    protected void createThreads(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        // Create a TaskThread for each task and add it to the group
        for (int i=0; i<mNumTasksToExecute; i++) {
            mCurrentTaskIndexPos = nextTaskIndexPos(mCurrentTaskIndexPos, mNumTasksExecuted);
            Task task = getCurTask(taskGroup);

            mThreadGroup.add(new TaskThread(experimentRunContext, taskGroup, task));
        }
    }

    protected void startThreads() {
        // Start all the TaskThreads
        for (TaskThread t : mThreadGroup) {
            t.start();
        }
    }

    protected void joinThreads() {
        // Join on all the TaskThreads
        for (TaskThread t : mThreadGroup) {
            try {
                //[FIXME: should we have a (user-configurable?) timeout here?]
                t.join();
            }
            catch (InterruptedException ex) {
                mLogger.error("Error joining Task threads", ex);
            }
        }
    }

}
