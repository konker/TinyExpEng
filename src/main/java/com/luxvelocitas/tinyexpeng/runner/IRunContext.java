package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Result;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import org.slf4j.Logger;

import java.util.List;


public interface IRunContext {
    IRunContext init(Logger logger, final Experiment experiment, final String runId);
    String getRunId();
    boolean isEnded();

    IRunContext addResultDataSink(IResultDataSink dataSink) throws DataException;
    void removeResultDataSink(IResultDataSink dataSink);

    IRunContext addSubjectDataSink(ISubjectDataSink dataSink) throws DataException;
    void removeSubjectDataSink(ISubjectDataSink dataSink);

    IRunContext addResult(Result result) throws DataException;
    IRunContext addSubject(Subject subject) throws DataException;
    List<Subject> getSubjects();

    IRunContext closeDataSinks() throws DataException;

    IRunContext addRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener);
    IRunContext removeRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener);
    IRunContext notifyRunContextEvent(ExperimentEvent eventType, DataBundle eventData);
}
