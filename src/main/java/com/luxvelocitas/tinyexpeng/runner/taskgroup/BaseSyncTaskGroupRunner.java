package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import org.slf4j.Logger;


public abstract class BaseSyncTaskGroupRunner extends AbstractTaskGroupRunner implements ITaskGroupRunner {
    @Override
    public void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup) {
        super.start(logger, experimentRunContext, taskGroup);

        _init(experimentRunContext, taskGroup);

        nextStep(experimentRunContext);
    }

    @Override
    public boolean hasStep() {
        return mNumTasksExecuted < mNumTasksToExecute;
    }

    @Override
    public void nextStep(ExperimentRunContext experimentRunContext) {
        // Get the
        if (hasStep()) {
            // Start the next task group
            mCurrentTaskIndexPos = nextTaskIndexPos(mCurrentTaskIndexPos, mNumTasksExecuted);
            execute(experimentRunContext);

            if (isAutoStep()) {
                nextStep(experimentRunContext);
            }
        }
        else {
            // End of the TaskGroup
            experimentRunContext.removeRunContextEventListener(ExperimentEventType.TASK_END, mRunContextEventListener);

            mCurTaskGroup.complete(experimentRunContext);
        }
    }
}
