package com.luxvelocitas.tinyexpeng.data;


import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Result;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;


public interface IResultDataSink {
    void init(String baseDir, IRunContext runContext, Experiment experiment) throws DataException;
    void writeResult(Result result) throws DataException;
    void close() throws DataException;
}
