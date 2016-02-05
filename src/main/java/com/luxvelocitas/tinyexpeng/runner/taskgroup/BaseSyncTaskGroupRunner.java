package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import org.slf4j.Logger;


public abstract class BaseSyncTaskGroupRunner extends AbstractTaskGroupRunner implements ITaskGroupRunner {
    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        super.start(logger, experimentRunContext, taskGroup);

        _init(experimentRunContext, taskGroup);

        // Start the TaskGroup
        mCurTaskGroup.start(experimentRunContext);

        // Process the Tasks
        nextStep(experimentRunContext);
    }

    @Override
    public void finalStep(ExperimentRunContext experimentRunContext) {
        experimentRunContext.removeRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);

        // End of the TaskGroup
        mCurTaskGroup.complete(experimentRunContext);
    }
}
