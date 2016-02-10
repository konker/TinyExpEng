package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;

/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public abstract class BaseSyncExperimentRunner extends AbstractExperimentRunner implements IExperimentRunner {

    @Override
    public void start(final IRunContext runContext, Experiment experiment) {
        super.start(runContext, experiment);

        _init(runContext, experiment);

        // Start the Experiment
        mCurExperiment.start(runContext);

        // Process the TaskGroups
        nextStep(runContext);
    }

    @Override
    public void finalStep(IRunContext runContext) {
        runContext.removeRunContextEventListener(ExperimentEvent.TASK_GROUP_END, mRunContextEventListener);

        // End of the Experiment
        mCurExperiment.end(runContext);
    }
}
