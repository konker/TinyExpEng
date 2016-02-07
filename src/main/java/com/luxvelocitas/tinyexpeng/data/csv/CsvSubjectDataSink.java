package com.luxvelocitas.tinyexpeng.data.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;


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

    private ExperimentRunContext mExperimentRunContext;
    private Experiment mExperiment;

    private String mSubjectFileName;
    private CSVWriter mSubjectWriter;
    private String mDataDir;
    private final String[] mCustomFieldNames;

    public CsvSubjectDataSink(String[] customFieldNames) {
        mCustomFieldNames = customFieldNames;
    }

    public CsvSubjectDataSink() {
        mCustomFieldNames = new String[0];
    }

    @Override
    public void init(String dataDir, ExperimentRunContext experimentRunContext, Experiment experiment) throws DataException {
        mDataDir = dataDir;
        mExperimentRunContext = experimentRunContext;
        mExperiment = experiment;

        // Create a writer with filename composed of args
        try {
            mSubjectFileName = getSubjectFileName(mDataDir);
            mSubjectWriter = new CSVWriter(new FileWriter(mSubjectFileName));

            // Write a header row if we can get one (Basically if customFieldNames are specified)
            String[] headerRow = getHeaderRow();
            if (headerRow != null) {
                mSubjectWriter.writeNext(headerRow);
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
        mSubjectWriter.writeNext(row);
    }

    @Override
    public void close() throws DataException {
        try {
            mSubjectWriter.close();
        }
        catch (IOException ex) {
            throw new DataException(ex);
        }
    }

    protected String getSubjectFileName(String dataDir) {
        String fileName =
                    "subject-" +
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

        row.add(mExperimentRunContext.getRunId());

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
