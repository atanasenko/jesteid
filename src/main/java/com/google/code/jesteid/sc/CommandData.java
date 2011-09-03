package com.google.code.jesteid.sc;

public class CommandData {
    
    private final int cla;
    private final int ins;
    private final int p1;
    private final int p2;
    private final byte[] data;
    private final int offset;
    private final int limit;
    private final int le;
    
    public CommandData(int cla, int ins, int p1, int p2) {
        this(cla, ins, p1, p2, -1);
    }
    
    public CommandData(int cla, int ins, int p1, int p2, int le) {
        this(cla, ins, p1, p2, null, le);
    }
    
    public CommandData(int cla, int ins, int p1, int p2, byte[] data) {
        this(cla, ins, p1, p2, data, -1);
    }
    
    public CommandData(int cla, int ins, int p1, int p2, byte[] data, int le) {
        this(cla, ins, p1, p2, data, 0, data == null ? 0 : data.length, le);
    }

    public CommandData(int cla, int ins, int p1, int p2, byte[] data, int offset, int limit) {
        this(cla, ins, p1, p2, data, offset, limit, -1);
    }
    
    public CommandData(int cla, int ins, int p1, int p2, byte[] data, int offset, int limit, int le) {
        this.cla = cla;
        this.ins = ins;
        this.p1 = p1;
        this.p2 = p2;
        this.data = data;
        this.offset = offset;
        this.limit = limit;
        this.le = le;
    }

    public int getCla() {
        return cla;
    }

    public int getIns() {
        return ins;
    }

    public int getP1() {
        return p1;
    }

    public int getP2() {
        return p2;
    }

    public byte[] getData() {
        return data;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
    
    public int getLe() {
        return le;
    }
    
}
