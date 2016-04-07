package com.luxvelocitas.tinyexpeng.data.csv;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CsvSubjectDataSink implements ISubjectDataSink {
    public static final String[] BASIC_FIELD_NAMES = {
        "Timestamp",
        "ExperimentUUID",
        "ExperimentId",
        "ExperimentName",
        "SubjectUUID",
        "SubjectId",
        "SubjectName",
        "RunId"
    };

    private IRunContext mRunContext;
    private Experiment mExperiment;

    private String mSubjectFileName;
    private CSVWriter mWriter;
    private String mDataDir;
    private String[] mCustomFieldNames;

    @Override
    public void init(String dataDir, IRunContext runContext, Experiment experiment, String[] customFieldNames) throws DataException {
        mDataDir = dataDir;
        mRunContext = runContext;
        mExperiment = experiment;

        if (customFieldNames != null) {
            mCustomFieldNames = customFieldNames;
        }
        else {
            mCustomFieldNames = new String[0];
        }

        // Create a writer with filename composed of args
        try {
            mSubjectFileName = getSubjectFileName(mDataDir);
            mWriter = new CSVWriter(new FileWriter(mSubjectFileName));

            // Write a header row if we can get one (Basically if customFieldNames are specified)
            String[] headerRow = getHeaderRow();
            if (headerRow != null) {
                mWriter.writeNext(headerRow);
            }
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public void writeSubject(Subject subject) throws DataException {
        // Write everything in the result
        String[] row = getSubjectRow(subject);
        mWriter.writeNext(row);
        mWriter.flushQuietly();
    }

    @Override
    public void close() throws DataException {
        try {
            mWriter.close();
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    protected String getSubjectFileName(String dataDir) {
        String fileName =
                    "subject-" +
                    mExperiment.getId() + "-" +
                    //mRunContext.getSubject().getId() + "-" +
                    mRunContext.getRunId() + "-" +
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

    protected String[] getSubjectRow(Subject subject) {
        List<String> row = new ArrayList<String>();

        // Timestamp
        row.add(String.valueOf((new Date()).getTime()));

        // Experiment data
        row.add(mExperiment.getUuid());
        row.add(String.valueOf(mExperiment.getId()));
        row.add(mExperiment.getName());

        // Subject data
        row.add(String.valueOf(subject.getUuid()));
        row.add(String.valueOf(subject.getId()));
        row.add(subject.getName());

        row.add(mRunContext.getRunId());

        // Custom data fields.
        // If we have custom fields specified, use those;
        // this will guarantee the order of the fields in the resulting data
        if (mCustomFieldNames != null) {
            for (String key : mCustomFieldNames) {
                row.add(String.valueOf(subject.getData().get(key)));
            }
        }
        else {
            for (String key : subject.getData().getKeySet()) {
                row.add(String.valueOf(subject.getData().get(key)));
            }
        }

        String[] ret = new String[row.size()];
        row.toArray(ret);

        return ret;
    }
}
