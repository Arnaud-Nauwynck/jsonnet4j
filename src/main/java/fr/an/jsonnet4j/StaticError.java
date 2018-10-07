package fr.an.jsonnet4j;

import java.io.PrintStream;

public final class StaticError extends RuntimeException {

	LocationRange location;
    String msg;
    
    public StaticError(String msg) {
    	this.msg = msg;
    }
    
    public StaticError(String filename, Location location, String msg) {
    	this(new LocationRange(filename, location, location.successor()), msg); 
    }
    
    public StaticError(LocationRange location, String msg) {
		this.location = location;
		this.msg = msg;
	}

    public String toString() {
    	String res = "";
    	if (location.isSet()) {
            res += location + ":";
        }
        res += " " + msg;
        return res;
    }

    public void print(PrintStream out) {
    	out.print(toString());
    }

}
