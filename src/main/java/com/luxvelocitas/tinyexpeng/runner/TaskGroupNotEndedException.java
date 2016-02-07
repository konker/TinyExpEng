package com.luxvelocitas.tinyexpeng.runner;


public class TaskGroupNotEndedException extends RuntimeException {
    public TaskGroupNotEndedException(String message) {
        super(message);
    }
}
