package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.AbstractRunner;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.TaskNotEndedException;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractTaskGroupRunner extends AbstractRunner implements ITaskGroupRunner, IRunner {
    protected TaskGroup mCurTaskGroup;
    protected Task mCurTask;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;

    @Override
    public void start(final IRunContext runContext, final TaskGroup taskGroup) {
        mCurTaskGroup = taskGroup;
    }

    @Override
    public void execute(final IRunContext runContext) {
        // Check that the previous Task has been finished before proceeding
        if (mCurTask != null) {
            if (!mCurTask.isEnded()) {
                throw new TaskNotEndedException("Attempt to start Task before the previous Task has ended: " + mCurTask.getName());
            }
        }

        // Get the current Task according to the index and start it
        mCurTask = getCurItem(mCurTaskGroup);
        runContext.setCurrentTask(mCurTask);

        // Start the current Task
        mCurTask.start(runContext);
    }

    protected Task getCurItem(TaskGroup taskGroup) {
        return taskGroup.get(mIndex[mCurrentIndexPos]);
    }

    protected void _init(final IRunContext runContext, final TaskGroup taskGroup) {
        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                mNumExecuted++;
                runContext.setCurrentTask(null);

                nextStep(runContext);
            }
        };
        runContext.addRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);

        mNumToExecute = taskGroup.size();
        mNumExecuted = 0;
        mCurrentIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mIndex = initIndex(mNumToExecute);
    }
}
