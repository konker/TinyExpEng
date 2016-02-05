package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.datautils.DataBundle;
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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class ExperimentRunContext {
    protected final Logger mLogger;
    protected final Subject mSubject;
    protected final String mRunId;
    protected final SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mEventDistpatcher;
    private final Experiment mExperiment;

    protected Set<IResultDataSink> mResultDataSinks;
    protected Set<ISubjectDataSink> mSubjectDataSinks;
    private boolean mEnded;

    public ExperimentRunContext(Logger logger, final Experiment experiment, final Subject subject, final String runId) {
        mLogger = logger;
        mExperiment = experiment;
        mSubject = subject;
        mRunId = runId;
        mEnded = false;

        mEventDistpatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();
        mResultDataSinks = new CopyOnWriteArraySet<IResultDataSink>();
        mSubjectDataSinks = new CopyOnWriteArraySet<ISubjectDataSink>();

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
    }

    public boolean isEnded() {
        return mEnded;
    }

    public Subject getSubject() {
        return mSubject;
    }

    public String getRunId() {
        return mRunId;
    }

    public void addResultDataSink(IResultDataSink dataSink) throws DataException {
        mResultDataSinks.add(dataSink);
    }

    public void removeResultDataSink(IResultDataSink dataSink) {
        mResultDataSinks.remove(dataSink);
    }

    public void addSubjectDataSink(ISubjectDataSink dataSink) throws DataException {
        mSubjectDataSinks.add(dataSink);
    }

    public void removeSubjectDataSink(ISubjectDataSink dataSink) {
        mSubjectDataSinks.remove(dataSink);
    }

    public void writeSubjectData() throws DataException {
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            dataSink.writeSubject(mSubject);
        }
    }

    public void addResult(Result result) throws DataException {
        for (IResultDataSink dataSink : mResultDataSinks) {
            dataSink.writeResult(result);
        }
    }

    public void addRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.addListener(eventType, eventListener);
    }

    public void removeRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.removeListener(eventType, eventListener);
    }

    public void notifyRunContextEvent(ExperimentEvent eventType, DataBundle eventData) {
        // Also forward this event to the Experiment
        eventData.put("experimentRunContext", this);
        mExperiment.notifyEvent(eventType, eventData);

        // Dispatch to all our listeners
        mEventDistpatcher.notify(eventType, eventData);
    }

    protected void _start() {
        //[XXX: nothing at the moment]
    }

    protected void _end() {
        mEnded = true;

        // Close any result data sinks
        for (IResultDataSink dataSink : mResultDataSinks) {
            try {
                dataSink.close();
            }
            catch (DataException ex) {
                mLogger.error("Error ending ExperimentRunContext", ex);
            }
        }

        // Close any subject data sinks
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            try {
                dataSink.close();
            }
            catch (DataException ex) {
                mLogger.error("Error ending ExperimentRunContext", ex);
            }
        }
    }
}
