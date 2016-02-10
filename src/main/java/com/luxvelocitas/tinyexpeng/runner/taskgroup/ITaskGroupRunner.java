package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;


/**
 * Run all the tasks in a group
 */
public interface ITaskGroupRunner {
    void start(final IRunContext runContext, final TaskGroup taskGroup);
}
