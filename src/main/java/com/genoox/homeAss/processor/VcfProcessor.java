package com.genoox.homeAss.processor;
import com.genoox.homeAss.model.Variant;

import java.util.List;
import java.util.stream.Stream;


public class VcfProcessor {
    private int start;
    private int end;
    private int minDP;
    private int limit;

    public VcfProcessor(int start, int end, int minDP, int limit) {
        this.start = start;
        this.end = end;
        this.minDP = minDP;
        this.limit = limit;
    }

    public List<Variant> process(Stream<String> lines) {
        // Filter variants and call API here
        return null;
    }
}
