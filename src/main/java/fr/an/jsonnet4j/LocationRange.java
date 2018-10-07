package fr.an.jsonnet4j;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class LocationRange {

    public String file;
    
    // [begin, end)
    public Location begin;
    public Location end;

    public LocationRange() {}
    
    /** This is useful for special locations, e.g. manifestation entry point. */
    public LocationRange(String file) {
    	this.file = file;
    }
    
    public LocationRange(String file, Location begin, Location end) {
		this.file = file;
		this.begin = begin;
		this.end = end;
	}

	public boolean isSet() {
        return begin.isSet();
    }
    
    public void print(PrintStream out) {
    	if (file != null && !file.isEmpty()) {
    		out.print(file);
    	}
    	if (isSet()) {
    		if (file.length() > 0)
    			out.print(":");
    		if (begin.line == end.line) {
    			if (begin.column == end.column - 1) {
    				out.print(begin);
    			} else {
    				out.print(begin + "-" + end.column);
    			}
    		} else {
    			out.print("(" + begin + ")-(" + end + ")");
    		}
    	}
    }

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		print(new PrintStream(out));
		return out.toString();
	}
    
}
