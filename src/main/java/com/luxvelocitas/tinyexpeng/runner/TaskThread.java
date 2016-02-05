package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinyexpeng.Task;
import com.luxvelocitas.tinyexpeng.TaskGroup;


public class TaskThread extends Thread {
    private ExperimentRunContext mExperimentRunContext;
    private TaskGroup mTaskGroup;
    private final Task mTask;

    public TaskThread(ExperimentRunContext experimentRunContext, TaskGroup taskGroup, Task task) {
        mExperimentRunContext = experimentRunContext;
        mTaskGroup = taskGroup;
        mTask = task;
    }

    @Override
    public void run() {
        mTask.start(mExperimentRunContext, mTaskGroup);
    }
}
