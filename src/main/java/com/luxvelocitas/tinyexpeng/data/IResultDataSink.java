package com.luxvelocitas.tinyexpeng.data;


import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.Result;


public interface IResultDataSink {
    void init(String baseDir, ExperimentRunContext experimentRunContext, Experiment experiment) throws DataException;
    void writeResult(Result result) throws DataException;
    void close() throws DataException;
}
