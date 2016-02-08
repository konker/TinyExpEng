package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;


public class Subject extends MetadataObject {
    protected DataBundle mData;

    public Subject() {
        _init();
    }

    public Subject(long id) {
        _init();
        setId(id);
    }

    public Subject(String name) {
        _init();
        setName(name);
    }

    private void _init() {
        setUuid();
        mData = new DataBundle();
    }

    public DataBundle getData() {
        return mData;
    }
}
