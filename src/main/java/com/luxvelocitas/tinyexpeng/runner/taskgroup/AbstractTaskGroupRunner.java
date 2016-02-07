package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.*;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractTaskGroupRunner extends AbstractRunner implements ITaskGroupRunner, IRunner {
    protected TaskGroup mCurTaskGroup;
    protected Task mCurTask;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;

    public AbstractTaskGroupRunner() {
        mAutoStep = true;
    }

    @Override
    public void start(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        mCurTaskGroup = taskGroup;
    }

    @Override
    public void execute(final ExperimentRunContext experimentRunContext) {
        // Check that the previous Task has been finished before proceeding
        if (mCurTask != null) {
            if (!mCurTask.isEnded()) {
                throw new TaskNotEndedException("Attempt to start Task before the previous Task has ended");
            }
        }

        // Get the current Task according to the index and start it
        mCurTask = getCurItem(mCurTaskGroup);

        // Start the current Task
        mCurTask.start(experimentRunContext);
    }

    protected Task getCurItem(TaskGroup taskGroup) {
        return taskGroup.get(mIndex[mCurrentIndexPos]);
    }

    protected void _init(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                mNumExecuted++;
            }
        };
        experimentRunContext.addRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);

        mNumToExecute = taskGroup.size();
        mNumExecuted = 0;
        mCurrentIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mIndex = initIndex(mNumToExecute);
    }
}
