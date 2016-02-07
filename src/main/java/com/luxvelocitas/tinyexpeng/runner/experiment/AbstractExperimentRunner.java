package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.*;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;

import java.util.List;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractExperimentRunner extends AbstractRunner implements IExperimentRunner, IRunner {
    protected Experiment mCurExperiment;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;
    protected List<ITaskGroupRunner> mItemRunners;

    public AbstractExperimentRunner() {
        mAutoStep = true;
    }

    @Override
    public void start(final ExperimentRunContext experimentRunContext, final Experiment experiment) {
        mCurExperiment = experiment;
    }

    @Override
    public void execute(final ExperimentRunContext experimentRunContext) {
        // Get the current task group according to the index
        TaskGroup curTaskGroup = getCurItem(mCurExperiment);

        // Get the appropriate TaskGroupRunner
        ITaskGroupRunner taskGroupRunner = getCurItemRunner();

        // Apply it to the current TaskGroup
        taskGroupRunner.start(experimentRunContext, curTaskGroup);
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

    protected void _init(final ExperimentRunContext experimentRunContext, final Experiment experiment) {
        mRunContextEventListener = new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                mNumExecuted++;
            }
        };
        experimentRunContext.addRunContextEventListener(ExperimentEvent.TASK_GROUP_END, mRunContextEventListener);

        mNumToExecute = experiment.size();
        mNumExecuted = 0;
        mCurrentIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mIndex = initIndex(mNumToExecute);
    }
}
