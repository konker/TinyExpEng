package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import org.slf4j.Logger;


public abstract class AbstractTaskGroupRunner implements ITaskGroupRunner {
    protected static final int START_INDEX = -1;

    protected Logger mLogger;
    protected int mCurrentTaskIndexPos;
    protected int[] mTaskIndex;
    protected boolean mAutoStep;

    protected TaskGroup mCurTaskGroup;
    protected ITinyEventListener<ExperimentEventType, DataBundle> mRunContextEventListener;
    protected int mNumTasksExecuted;
    protected int mNumTasksToExecute;

    public AbstractTaskGroupRunner() {
        mAutoStep = true;
    }

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        mLogger = logger;
        mCurTaskGroup = taskGroup;
    }

    @Override
    public boolean isAutoStep() {
        return mAutoStep;
    }

    @Override
    public void setAutoStep(boolean autoStep) {
        mAutoStep = autoStep;
    }

    protected void execute(final ExperimentRunContext experimentRunContext) {
        // Get the current Task according to the index and start it
        Task curTask = getCurTask(mCurTaskGroup);

        // Start the current Task
        curTask.start(experimentRunContext, mCurTaskGroup);
    }

    protected Task getCurTask(TaskGroup taskGroup) {
        return taskGroup.get(mTaskIndex[mCurrentTaskIndexPos]);
    }

    protected void _init(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        mRunContextEventListener = new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                mNumTasksExecuted++;
            }
        };
        experimentRunContext.addRunContextEventListener(ExperimentEventType.TASK_END, mRunContextEventListener);

        mNumTasksToExecute = taskGroup.size();
        mNumTasksExecuted = 0;
        mCurrentTaskIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mTaskIndex = initTaskIndex(mNumTasksToExecute);
    }

    /** Initialize the index */
    protected abstract int[] initTaskIndex(int numTasksToExecute);

    /** Set the next index value */
    protected int nextTaskIndexPos(int currentTaskIndexPos, int numTasksExecuted) {
        if (currentTaskIndexPos== START_INDEX) {
            return 0;
        }
        return currentTaskIndexPos + 1;
    }
}
