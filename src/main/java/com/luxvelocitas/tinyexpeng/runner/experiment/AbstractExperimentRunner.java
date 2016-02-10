package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.AbstractRunner;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.TaskGroupNotEndedException;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;

import java.util.List;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractExperimentRunner extends AbstractRunner implements IExperimentRunner, IRunner {
    protected Experiment mCurExperiment;
    protected TaskGroup mCurTaskGroup;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;
    protected List<ITaskGroupRunner> mItemRunners;

    @Override
    public void start(final IRunContext runContext, final Experiment experiment) {
        mCurExperiment = experiment;
    }

    @Override
    public void execute(final IRunContext runContext) {
        // Check that the previous Task has been finished before proceeding
        if (mCurTaskGroup != null) {
            if (!mCurTaskGroup.isEnded()) {
                throw new TaskGroupNotEndedException("Attempt to start TaskGroup before the previous TaskGroup has ended");
            }
        }
        // Get the current task group according to the index
        mCurTaskGroup = getCurItem(mCurExperiment);
        runContext.setCurrentTaskGroup(mCurTaskGroup);

        // Get the appropriate TaskGroupRunner
        ITaskGroupRunner taskGroupRunner = getCurItemRunner();

        // Apply it to the current TaskGroup
        taskGroupRunner.start(runContext, mCurTaskGroup);
    }

    @Override
    public IExperimentRunner setItemRunners(final List<ITaskGroupRunner> taskGroupRunners) {
        mItemRunners = taskGroupRunners;

        return this;
    }

    protected TaskGroup getCurItem(Experiment experiment) {
        return experiment.get(mIndex[mCurrentIndexPos]);
    }

    protected ITaskGroupRunner getCurItemRunner() {
        //[TODO: is this a good way?]
        return mItemRunners.get(mIndex[mCurrentIndexPos]);
    }

    protected void _init(final IRunContext runContext, final Experiment experiment) {
        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                mNumExecuted++;
                runContext.setCurrentTaskGroup(null);

                nextStep(runContext);
            }
        };
        runContext.addRunContextEventListener(ExperimentEvent.TASK_GROUP_END, mRunContextEventListener);

        mNumToExecute = experiment.size();
        mNumExecuted = 0;
        mCurrentIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mIndex = initIndex(mNumToExecute);
    }
}
