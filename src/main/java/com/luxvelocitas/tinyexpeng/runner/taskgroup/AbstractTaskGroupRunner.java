package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.*;
import org.slf4j.Logger;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractTaskGroupRunner extends AbstractRunner implements ITaskGroupRunner, IRunner {
    protected Task mCurTask;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;

    protected AbstractTaskGroupRunner(Logger logger) {
        super(logger);
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
        mCurTask = getCurItem((TaskGroup)mCurRunnableItem);
        runContext.setCurrentTask(mCurTask);

        // Start the current Task
        mCurTask.start(runContext, mNumExecuted+1, mNumToExecute);
    }

    protected Task getCurItem(TaskGroup taskGroup) {
        return taskGroup.get(mIndex[mCurrentIndexPos]);
    }

    @Override
    public void init(final IRunContext runContext, final IRunnableItem item) {
        super.init(runContext, item);

        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                mNumExecuted++;
                runContext.setCurrentTask(null);

                nextStep(runContext);
            }
        };
        runContext.addRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);
    }

    @Override
    public void deinit(final IRunContext runContext) {
        runContext.removeRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);
    }
}
