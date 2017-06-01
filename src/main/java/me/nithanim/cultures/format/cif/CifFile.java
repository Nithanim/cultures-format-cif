package me.nithanim.cultures.format.cif;

import java.util.List;

public class CifFile {
    private final byte type;
    private final List<String> lines;

    public CifFile(byte type, List<String> lines) {
        this.type = type;
        this.lines = lines;
    }

    public byte getType() {
        return type;
    }

    public List<String> getLines() {
        return lines;
    }
}
