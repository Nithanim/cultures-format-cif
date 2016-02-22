package me.nithanim.cultures.format.cif;

import java.util.List;

public class CifFile {
    private final List<String> lines;

    public CifFile(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }
}
