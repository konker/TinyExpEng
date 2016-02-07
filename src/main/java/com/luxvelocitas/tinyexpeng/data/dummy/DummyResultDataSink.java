package com.luxvelocitas.tinyexpeng.data.dummy;

import com.luxvelocitas.datautils.DummyOutputStream;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Result;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DummyResultDataSink implements IResultDataSink {
    public static final String[] BASIC_FIELD_NAMES = {
        "Timestamp",
        "RunId",
        "ExperimentUUID",
        "ExperimentId",
        "ExperimentName",
        "SubjectUUID",
        "SubjectId",
        "SubjectName",
        "TaskUUID",
        "TaskId",
        "TaskName"
    };

    private ExperimentRunContext mExperimentRunContext;
    private Experiment mExperiment;

    private String mResultFileName;
    private OutputStream mOutputStream;
    private String mDataDir;
    private final String[] mCustomFieldNames;

    public DummyResultDataSink(String[] customFieldNames) {
        mCustomFieldNames = customFieldNames;
    }

    public DummyResultDataSink() {
        mCustomFieldNames = new String[0];
    }

    @Override
    public void init(String dataDir, ExperimentRunContext experimentRunContext, Experiment experiment) throws DataException {
        mDataDir = dataDir;
        mExperimentRunContext = experimentRunContext;
        mExperiment = experiment;

        try {
            mResultFileName = getResultFileName(mDataDir);
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
    public void writeResult(Result result) throws DataException {
        // Write everything in the result
        String[] row = getResultRow(result);
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

    protected String getResultFileName(String dataDir) {
        String fileName =
                    "result-" +
                    mExperiment.getId() + "-" +
                    //mExperimentRunContext.getSubject().getId() + "-" +
                    mExperimentRunContext.getRunId() + "-" +
                    (new Date()).getTime() +
                    ".csv";
        return (new File(mDataDir, fileName)).getAbsolutePath();
    }

    protected String[] getHeaderRow() {
        String[] ret =
            new String[BASIC_FIELD_NAMES.length + mCustomFieldNames.length];
        System.arraycopy(BASIC_FIELD_NAMES, 0, ret, 0, BASIC_FIELD_NAMES.length);

        if (mCustomFieldNames != null) {
            System.arraycopy(mCustomFieldNames, 0, ret, BASIC_FIELD_NAMES.length, mCustomFieldNames.length);
        }
        return ret;
    }

    protected String[] getResultRow(Result result) {
        List<String> row = new ArrayList<String>();

        // Basic data
        row.add(String.valueOf(result.getTimestamp().getTime()));
        row.add(mExperimentRunContext.getRunId());

        // Experiment data
        row.add(mExperiment.getUuid());
        row.add(String.valueOf(mExperiment.getId()));
        row.add(mExperiment.getName());

        // Subject data
        row.add(result.getSubject().getUuid());
        row.add(String.valueOf(result.getSubject().getId()));
        row.add(result.getSubject().getName());

        /*[XXX: remove?]
        // TaskGroup data
        if (result.getTaskGroup() == null) {
            row.add(null);
            row.add(null);
            row.add(null);
        }
        else {
            row.add(result.getTaskGroup().getUuid());
            row.add(String.valueOf(result.getTaskGroup().getId()));
            row.add(result.getTaskGroup().getName());
        }
        */

        // Task data
        if (result.getTask() == null) {
            row.add(null);
            row.add(null);
            row.add(null);
        }
        else {
            row.add(result.getTask().getUuid());
            row.add(String.valueOf(result.getTask().getId()));
            row.add(result.getTask().getName());
        }

        // Custom data fields.
        // If we have custom fields specified, use those;
        // this will guarantee the order of the fields in the resulting data
        if (mCustomFieldNames != null) {
            for (String key : mCustomFieldNames) {
                row.add(String.valueOf(result.getData().get(key)));
            }
        }
        else {
            for (String key : result.getData().getKeySet()) {
                row.add(String.valueOf(result.getData().get(key)));
            }
        }

        String[] ret = new String[row.size()];
        row.toArray(ret);

        return ret;
    }
}
