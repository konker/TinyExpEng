package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.ISteppable;


/**
 * Run all the tasks in a group
 */
public interface ITaskGroupRunner extends ISteppable {
    void start(final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup);
}
