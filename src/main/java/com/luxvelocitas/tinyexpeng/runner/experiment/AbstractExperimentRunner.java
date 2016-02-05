package com.luxvelocitas.tinyexpeng.runner.experiment;

import java.util.List;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.StaleExperimentRunContextException;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;
import org.slf4j.Logger;


/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public abstract class AbstractExperimentRunner implements IExperimentRunner {
    protected static final int START_INDEX = -1;

    protected Logger mLogger;
    protected List<ITaskGroupRunner> mTaskGroupRunners;

    protected int mCurrentTaskGroupIndexPos;
    protected int[] mTaskGroupIndex;
    protected boolean mAutoStep;
    protected Experiment mCurExperiment;

    public AbstractExperimentRunner() {
        mAutoStep = true;
    }

    @Override
    public IExperimentRunner setTaskGroupRunners(final List<ITaskGroupRunner> taskGroupRunners) {
        mTaskGroupRunners = taskGroupRunners;

        return this;
    }

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final Experiment experiment) throws StaleExperimentRunContextException {
        mLogger = logger;
        mCurExperiment = experiment;
        mCurExperiment.start(experimentRunContext);
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
        // Get the current task group according to the index
        TaskGroup curTaskGroup = getCurTaskGroup(mCurExperiment);

        // Start the current TaskGroup
        curTaskGroup.start(experimentRunContext);

        // Get the appropriate TaskGroupRunner
        ITaskGroupRunner taskGroupRunner = getCurTaskGroupRunner();

        // Apply it to the current TaskGroup
        taskGroupRunner.start(mLogger, experimentRunContext, curTaskGroup);
    }

    public TaskGroup getCurTaskGroup(Experiment experiment) {
        return experiment.get(mTaskGroupIndex[mCurrentTaskGroupIndexPos]);
    }

    public ITaskGroupRunner getCurTaskGroupRunner() {
        //[TODO: is this a good way?]
        return mTaskGroupRunners.get(mTaskGroupIndex[mCurrentTaskGroupIndexPos]);
    }

    /** Initialize the index */
    protected abstract int[] initTaskGroupIndex(int numTasksGroupsToExecute);

    /** Set the next index value */
    protected int nextTaskGroupIndexPos(int currentTaskGroupIndexPos, int numTaskGroupsExecuted) {
        if (currentTaskGroupIndexPos== START_INDEX) {
            return 0;
        }
        return currentTaskGroupIndexPos + 1;
    }
}
