package com.madai.mock.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CollectionUtil implements Serializable {

    public String getType(Map map) {

        return "map";
    }

    public String getType(List list) {

        return "list";
    }
}
