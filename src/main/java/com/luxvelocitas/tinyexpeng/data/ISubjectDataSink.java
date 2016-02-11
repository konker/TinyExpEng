package com.luxvelocitas.tinyexpeng.data;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;


public interface ISubjectDataSink {
    void init(String baseDir, IRunContext runContext, Experiment experiment, String[] customFieldNames) throws DataException;
    void writeSubject(Subject subject) throws DataException;
    void close() throws DataException;
}
