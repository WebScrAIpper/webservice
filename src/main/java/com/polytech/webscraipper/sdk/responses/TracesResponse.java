package com.polytech.webscraipper.sdk.responses;

import java.util.List;
import java.util.Map;

public class TracesResponse {
    private List<Map<String, Object>> data;
    private TraceMetaData meta;

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public TraceMetaData getMeta() {
        return meta;
    }

    public void setMeta(TraceMetaData meta) {
        this.meta = meta;
    }
}
