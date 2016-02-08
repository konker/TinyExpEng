package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.SimpleTinyEventDispatcher;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Result;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class ExperimentRunContext implements IRunContext {
    protected Logger mLogger;
    protected String mRunId;
    protected final SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mEventDistpatcher;
    private Experiment mExperiment;

    protected final List<Subject> mSubjects;
    protected final Set<IResultDataSink> mResultDataSinks;
    protected final Set<ISubjectDataSink> mSubjectDataSinks;
    private boolean mEnded;

    public ExperimentRunContext() {
        mEventDistpatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();
        mResultDataSinks = new CopyOnWriteArraySet<IResultDataSink>();
        mSubjectDataSinks = new CopyOnWriteArraySet<ISubjectDataSink>();

        mSubjects = new ArrayList<Subject>();
    }

    @Override
    public IRunContext init(Logger logger, final Experiment experiment, final String runId) {
        mLogger = logger;
        mExperiment = experiment;
        mRunId = runId;
        mEnded = false;

        // Add a listener for the start and end of the experiment run
        addRunContextEventListener(ExperimentEvent.EXPERIMENT_START, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                _start();
            }
        });

        // Add a listener for the start and end of the experiment run
        addRunContextEventListener(ExperimentEvent.EXPERIMENT_END, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                _end();
            }
        });

        return this;
    }

    @Override
    public String getRunId() {
        return mRunId;
    }

    @Override
    public boolean isEnded() {
        return mEnded;
    }

    @Override
    public IRunContext addResultDataSink(IResultDataSink dataSink) throws DataException {
        mResultDataSinks.add(dataSink);

        return this;
    }

    @Override
    public void removeResultDataSink(IResultDataSink dataSink) {
        mResultDataSinks.remove(dataSink);
    }

    @Override
    public IRunContext addSubjectDataSink(ISubjectDataSink dataSink) throws DataException {
        mSubjectDataSinks.add(dataSink);

        return this;
    }

    @Override
    public void removeSubjectDataSink(ISubjectDataSink dataSink) {
        mSubjectDataSinks.remove(dataSink);
    }

    @Override
    public IRunContext addSubject(Subject subject) throws DataException {
        // Add to the list of subjects for this run
        mSubjects.add(subject);

        // Also write it to any Subject data sinks
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            dataSink.writeSubject(subject);
        }

        return this;
    }

    @Override
    public List<Subject> getSubjects() {
        return mSubjects;
    }

    @Override
    public IRunContext addResult(Result result) throws DataException {
        for (IResultDataSink dataSink : mResultDataSinks) {
            dataSink.writeResult(result);
        }

        return this;
    }

    @Override
    public IRunContext addRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.addListener(eventType, eventListener);

        return this;
    }

    @Override
    public IRunContext removeRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.removeListener(eventType, eventListener);

        return this;
    }

    @Override
    public IRunContext notifyRunContextEvent(ExperimentEvent eventType, DataBundle eventData) {
        // Also forward this event to the Experiment
        eventData.put(Experiment.DATA_KEY_CONTEXT, this);
        mExperiment.notifyEvent(eventType, eventData);

        // Dispatch to all our listeners
        mEventDistpatcher.notify(eventType, eventData);

        return this;
    }

    @Override
    public IRunContext closeDataSinks() throws DataException {
        // Close any result data sinks
        for (IResultDataSink dataSink : mResultDataSinks) {
            dataSink.close();
        }

        // Close any subject data sinks
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            dataSink.close();
        }

        return this;
    }

    protected void _start() {
        //[XXX: nothing at the moment]
    }

    protected void _end() {
        mEnded = true;
    }
}
