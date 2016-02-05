package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import org.slf4j.Logger;


/**
 * Run all the tasks in a group
 */
public interface ITaskGroupRunner {
    void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup);

    boolean hasStep();
    void nextStep(ExperimentRunContext experimentRunContext);

    boolean isAutoStep();
    void setAutoStep(boolean autoStep);
}
