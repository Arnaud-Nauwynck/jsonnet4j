package fr.an.jsonnet4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.an.jsonnet4j.AST.Identifier;

public class Allocator {

    Map<String, Identifier > internedIdentifiers;
    List<AST> allocated = new ArrayList<>();

//   public <T,Args> T make(Args... args) {
//	   auto r = new T(std::forward<Args>(args)...);
//	   allocated.add(r);
//	   return r;
//   }
//
//    public <T> T clone(T ast) {
//        auto r = new T(*ast);
//        allocated.add(r);
//        return r;
//    }
    
    /** Returns interned identifiers.
     *
     * The location used in the Identifier AST is that of the first one parsed.
     */
    Identifier makeIdentifier(String name) {
    	Identifier it = internedIdentifiers.get(name);
        if (it != null) {
            return it;
        }
        Identifier r = new Identifier(name);
        internedIdentifiers.put(name, r);
        return r;
    }
    
//    public void dispose()  {
//        for (AST x : allocated) {
//            x.dispose();
//        }
//        allocated.clear();
//        for (Identifier x : internedIdentifiers) {
//            x.dispose();
//        }
//        internedIdentifiers.clear();
//    }
    
}
