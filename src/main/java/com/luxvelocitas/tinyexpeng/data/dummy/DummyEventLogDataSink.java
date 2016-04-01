package com.luxvelocitas.tinyexpeng.data.dummy;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.DummyOutputStream;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.*;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IEventLogDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DummyEventLogDataSink implements IEventLogDataSink {
    public static final String[] BASIC_FIELD_NAMES = {
        "Timestamp",
        "RunId",
        "ExperimentUUID",
        "ExperimentId",
        "ExperimentName",
        "TaskGroupUUID",
        "TaskGroupId",
        "TaskGroupName",
        "TaskUUID",
        "TaskId",
        "TaskName",
        "FSMEvent",
        "Event"
    };

    private IRunContext mRunContext;
    private Experiment mExperiment;

    private String mEventLogFileName;
    private OutputStream mOutputStream;
    private String mDataDir;

    @Override
    public void init(String dataDir, IRunContext runContext, Experiment experiment) throws DataException {
        mDataDir = dataDir;
        mRunContext = runContext;
        mExperiment = experiment;

        try {
            mEventLogFileName = getEventLogFileName(mDataDir);
            mOutputStream = new DummyOutputStream();

            // Write a header row if we can get one (Basically if customFieldNames are specified)
            String[] headerRow = getHeaderRow();
            if (headerRow != null) {
                mOutputStream.write(headerRow.length);
            }
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public void writeEventLog(TinyEvent<ExperimentEvent, DataBundle> event) throws DataException {
        // Write everything in the result
        String[] row = getEventLogRow(event);
        try {
            mOutputStream.write(row.length);
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public void close() throws DataException {
        try {
            mOutputStream.close();
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    protected String getEventLogFileName(String dataDir) {
        String fileName =
                    "eventlog-" +
                    mExperiment.getId() + "-" +
                    //mRunContext.getSubject().getId() + "-" +
                    mRunContext.getRunId() + "-" +
                    (new Date()).getTime() +
                    ".csv";
        return (new File(mDataDir, fileName)).getAbsolutePath();
    }

    protected String[] getHeaderRow() {
        return BASIC_FIELD_NAMES;
    }

    protected String[] getEventLogRow(TinyEvent<ExperimentEvent, DataBundle> event) {
        List<String> row = new ArrayList<String>();

        // Basic data
        row.add(String.valueOf((new Date()).getTime()));
        row.add(mRunContext.getRunId());

        // Experiment data
        row.add(mExperiment.getUuid());
        row.add(String.valueOf(mExperiment.getId()));
        row.add(mExperiment.getName());

        // TaskGroup data
        TaskGroup curTaskGroup = mRunContext.getCurrentTaskGroup();
        if (curTaskGroup != null) {
            row.add(curTaskGroup.getUuid());
            row.add(String.valueOf(curTaskGroup.getId()));
            row.add(curTaskGroup.getName());
        }
        else {
            row.add(null);
            row.add(null);
            row.add(null);
        }

        // Task data
        Task curTask = mRunContext.getCurrentTask();
        if (curTask != null) {
            row.add(curTask.getUuid());
            row.add(String.valueOf(curTask.getId()));
            row.add(curTask.getName());
        }
        else {
            row.add(null);
            row.add(null);
            row.add(null);
        }

        // FSM event data
        if (event.getEventType().equals(ExperimentEvent.FSM_EVENT)) {
            row.add(event
                    .getEventData()
                    .get(Experiment.DATA_KEY_FSM_EVENT_STATE)
                    .toString());
        }

        // Event data
        row.add(event.getEventType().toString());

        String[] ret = new String[row.size()];
        row.toArray(ret);

        return ret;
    }
}
