package com.luxvelocitas.tinyexpeng.runner;


public class TaskNotEndedException extends RuntimeException {
    public TaskNotEndedException(String message) {
        super(message);
    }
}
