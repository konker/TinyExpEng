package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.StaleExperimentRunContextException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import org.slf4j.Logger;

/**
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public abstract class BaseSyncExperimentRunner extends AbstractExperimentRunner implements IExperimentRunner {

    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, Experiment experiment) throws StaleExperimentRunContextException {
        super.start(logger, experimentRunContext, experiment);

        _init(experimentRunContext, experiment);

        // Start the Experiment
        mCurExperiment.start(experimentRunContext);

        // Process the TaskGroups
        nextStep(experimentRunContext);
    }

    @Override
    public void finalStep(ExperimentRunContext experimentRunContext) {
        experimentRunContext.removeRunContextEventListener(ExperimentEvent.TASK_GROUP_END, mRunContextEventListener);

        // End of the Experiment
        mCurExperiment.complete(experimentRunContext);
    }
}
