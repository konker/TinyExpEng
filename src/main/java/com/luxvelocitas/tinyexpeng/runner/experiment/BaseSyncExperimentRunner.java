package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.StaleExperimentRunContextException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import org.slf4j.Logger;

/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public abstract class BaseSyncExperimentRunner extends AbstractExperimentRunner implements IExperimentRunner {
    protected ITinyEventListener<ExperimentEventType, DataBundle> mRunContextEventListener;
    protected int mNumTaskGroupsToExecute;
    protected int mNumTaskGroupsExecuted;

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, Experiment experiment) throws StaleExperimentRunContextException {
        super.start(logger, experimentRunContext, experiment);

        mRunContextEventListener = new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                mNumTaskGroupsExecuted++;
            }
        };
        experimentRunContext.addRunContextEventListener(ExperimentEventType.TASK_GROUP_END, mRunContextEventListener);

        mNumTaskGroupsToExecute = experiment.size();
        mNumTaskGroupsExecuted = 0;
        mCurrentTaskGroupIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mTaskGroupIndex = initTaskGroupIndex(mNumTaskGroupsToExecute);

        nextStep(experimentRunContext);
    }

    @Override
    public boolean hasStep() {
        return mNumTaskGroupsExecuted < mNumTaskGroupsToExecute;
    }

    @Override
    public void nextStep(ExperimentRunContext experimentRunContext) {
        // Get the
        if (hasStep()) {
            // Start the next task group
            mCurrentTaskGroupIndexPos = nextTaskGroupIndexPos(mCurrentTaskGroupIndexPos, mNumTaskGroupsExecuted);
            execute(experimentRunContext);

            if (isAutoStep()) {
                nextStep(experimentRunContext);
            }
        }
        else {
            // End of the Experiment
            experimentRunContext.removeRunContextEventListener(ExperimentEventType.TASK_GROUP_END, mRunContextEventListener);

            mCurExperiment.complete(experimentRunContext);
        }
    }
}
