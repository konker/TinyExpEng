package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;


public abstract class BaseSyncTaskGroupRunner extends AbstractTaskGroupRunner implements ITaskGroupRunner {
    @Override
    public void start(final IRunContext runContext, final TaskGroup taskGroup) {
        super.start(runContext, taskGroup);

        _init(runContext, taskGroup);

        // Start the TaskGroup
        mCurTaskGroup.start(runContext);

        // Process the Tasks
        nextStep(runContext);
    }

    @Override
    public void finalStep(IRunContext runContext) {
        runContext.removeRunContextEventListener(ExperimentEvent.TASK_END, mRunContextEventListener);

        // End of the TaskGroup
        mCurTaskGroup.end(runContext);
    }
}
