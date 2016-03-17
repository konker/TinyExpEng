package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.*;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;
import org.slf4j.Logger;

import java.util.List;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractExperimentRunner extends AbstractRunner implements IExperimentRunner, IRunner {
    //protected Experiment mCurExperiment;
    protected TaskGroup mCurTaskGroup;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mExperimentEventStarListener;
    protected List<ITaskGroupRunner> mItemRunners;

    protected AbstractExperimentRunner(Logger logger) {
        super(logger);
    }

    @Override
    public void execute(final IRunContext runContext) {
        // Check that the previous TaskGroup has been finished before proceeding
        if (mCurTaskGroup != null) {
            if (!mCurTaskGroup.isEnded()) {
                throw new TaskGroupNotEndedException("Attempt to start TaskGroup before the previous TaskGroup has ended");
            }
        }
        // Get the current task group according to the index
        mCurTaskGroup = getCurItem((Experiment)mCurRunnableItem);
        runContext.setCurrentTaskGroup(mCurTaskGroup);

        // Get the appropriate TaskGroupRunner
        ITaskGroupRunner taskGroupRunner = getCurItemRunner();

        // Apply it to the current TaskGroup
        taskGroupRunner.start(runContext, mCurTaskGroup);
    }

    @Override
    public IExperimentRunner setTaskGroupRunners(final List<ITaskGroupRunner> taskGroupRunners) {
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

    @Override
    public void init(final IRunContext runContext, final IRunnableItem item) {
        final Experiment experiment = (Experiment)item;

        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
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

    @Override
    public void deinit(final IRunContext runContext) {
        runContext.removeRunContextEventListener(ExperimentEvent.TASK_GROUP_END, mRunContextEventListener);
    }
}
