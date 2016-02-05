package com.luxvelocitas.tinyexpeng.data;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.Subject;


public interface ISubjectDataSink {
    void init(String baseDir, ExperimentRunContext experimentRunContext, Experiment experiment) throws DataException;
    void writeSubject(Subject subject) throws DataException;
    void close() throws DataException;
}
