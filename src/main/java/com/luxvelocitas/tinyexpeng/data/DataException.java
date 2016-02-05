package com.luxvelocitas.tinyexpeng.data;


public class DataException extends Exception {
    public DataException(String message) {
        super(message);
    }

    public DataException(Exception e) {
        super(e);
    }
}
