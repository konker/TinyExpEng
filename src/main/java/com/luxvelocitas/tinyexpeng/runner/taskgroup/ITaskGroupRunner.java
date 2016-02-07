package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;


/**
 * Run all the tasks in a group
 */
public interface ITaskGroupRunner {
    void start(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup);
}
