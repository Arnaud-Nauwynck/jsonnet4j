package fr.an.jsonnet4j;

import java.io.PrintStream;

public final class Location {

	public final long line;
    
	public final long column;
    
	public Location() {
		this(0, 0);
    }
    
    public Location(long line, long column) {
    	this.line = line;
    	this.column = column;
    }
    
    public boolean isSet() {
        return line != 0;
    }
    
    public Location successor() {
        return new Location(line, column + 1);
    }

	public void print(PrintStream out) {
	    out.print(line + ":" + column);
	}

	@Override
	public String toString() {
		return line + ":" + column;
	}
	
}
