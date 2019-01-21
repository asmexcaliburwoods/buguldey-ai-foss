package org.east.javadoc;

import org.east.concepts.Concept;
import org.east.concepts.JavaLanguageTextualContext;
import org.east.concepts.Name;
import org.east.concepts.utility.MeaningAllocator;

public class PublicProtectedPrivatePackageModifier extends Concept{
  public final static PublicProtectedPrivatePackageModifier PUBLIC=resolve("public");
  public final static PublicProtectedPrivatePackageModifier PROTECTED=resolve("protected");
  public final static PublicProtectedPrivatePackageModifier PRIVATE=resolve("private");
  public final static PublicProtectedPrivatePackageModifier PACKAGE=resolve("package");
  private PublicProtectedPrivatePackageModifier(){}
  private static PublicProtectedPrivatePackageModifier resolve(String name){
    return (PublicProtectedPrivatePackageModifier)Name.define(name,
            JavaLanguageTextualContext.getInstance(),
            PublicProtectedPrivatePackageModifier.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new PublicProtectedPrivatePackageModifier();
              }
            }).getConcept();
  }
  public static void define(){
    //static initializers will do the job
  }
}
