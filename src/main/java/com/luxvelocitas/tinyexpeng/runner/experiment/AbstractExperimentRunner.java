package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.StaleExperimentRunContextException;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.AbstractRunner;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.ISteppable;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;
import org.slf4j.Logger;

import java.util.List;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 */
public abstract class AbstractExperimentRunner extends AbstractRunner implements IExperimentRunner, IRunner, ISteppable {
    protected Experiment mCurExperiment;
    protected ITinyEventListener<ExperimentEvent, DataBundle> mRunContextEventListener;
    protected List<ITaskGroupRunner> mTaskGroupRunners;

    public AbstractExperimentRunner() {
        mAutoStep = true;
    }

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final Experiment experiment) throws StaleExperimentRunContextException {
        mLogger = logger;
        mCurExperiment = experiment;
    }

    @Override
    public void execute(final ExperimentRunContext experimentRunContext) {
        // Get the current task group according to the index
        TaskGroup curTaskGroup = getCurTaskGroup(mCurExperiment);

        // Get the appropriate TaskGroupRunner
        ITaskGroupRunner taskGroupRunner = getCurTaskGroupRunner();

        // Apply it to the current TaskGroup
        taskGroupRunner.start(mLogger, experimentRunContext, curTaskGroup);
    }

    @Override
    public IExperimentRunner setTaskGroupRunners(final List<ITaskGroupRunner> taskGroupRunners) {
        mTaskGroupRunners = taskGroupRunners;

        return this;
    }

    protected TaskGroup getCurTaskGroup(Experiment experiment) {
        return experiment.get(mIndex[mCurrentIndexPos]);
    }

    protected ITaskGroupRunner getCurTaskGroupRunner() {
        //[TODO: is this a good way?]
        return mTaskGroupRunners.get(mIndex[mCurrentIndexPos]);
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
