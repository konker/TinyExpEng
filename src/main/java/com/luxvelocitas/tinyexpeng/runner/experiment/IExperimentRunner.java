package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.StaleExperimentRunContextException;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;
import org.slf4j.Logger;

import java.util.List;


public interface IExperimentRunner extends IRunner {
    IExperimentRunner setTaskGroupRunners(List<ITaskGroupRunner> taskGroupRunners);

    void start(Logger logger, final ExperimentRunContext experimentRunContext, final Experiment experiment) throws StaleExperimentRunContextException;
}
