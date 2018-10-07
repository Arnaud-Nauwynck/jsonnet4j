package fr.an.jsonnet4j;

public class FmtOpts {
	
    char stringStyle = 's';
    char commentStyle = 's';
    int indent = 2;
    int maxBlankLines = 2;
    boolean padArrays;
    boolean padObjects = true;
    boolean stripComments;
    boolean stripAllButComments;
    boolean stripEverything;
    boolean prettyFieldNames = true;
    boolean sortImports = true;
    
    public FmtOpts() {
    }

}
