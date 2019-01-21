package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;

public class MemberExpr extends Expr{
  private Expr e;
  private String memberName;
  private List args;
  public MemberExpr(Expr e, String memberName, List args){
    this.e=e;
    this.memberName=memberName;
    this.args=args;
  }
  public Object evaluate(Scope ctx) throws Exception{
    Object o=e.evaluate(ctx);
    if(o instanceof LValue)o=((LValue)o).getValue();
    if(o==null)
      throw new NullPointerException("expr "+e+" evaluated to null");
    Method[] methods=o.getClass().getMethods();
    List methOk=new ArrayList(methods.length);
    for(int i=0;i<methods.length;i++){
      Method method=methods[i];
      if(method.getName().equals(memberName)&&
              method.getParameterTypes().length==args.size())methOk.add(method);
    }
    if(methOk.isEmpty())
      throw new RuntimeException("no such method: "+o.getClass().getName()+"."+memberName+" ("+args.size()+" args)");
    if(methOk.size()>1)
      throw new RuntimeException("ambiguous method: "+o.getClass().getName()+"."+memberName+" ("+args.size()+" args)");
    Method method=(Method)methOk.get(0);
    List argValues=new ArrayList(args.size());
    Iterator it=args.iterator();
    while(it.hasNext()){
      Expr arg=(Expr)it.next();
      Object value=arg.evaluate(ctx);
      if(o instanceof LValue)o=((LValue)o).getValue();
      argValues.add(o);
    }
    Object result=method.invoke(o,argValues.toArray());
    return result;
  }
  public String toString(){
    StringBuffer sb=new StringBuffer(e+"."+memberName+"(");
    Iterator it=args.iterator();
    boolean first=true;
    while(it.hasNext()){
      Expr expr=(Expr)it.next();
      if(!first)sb.append(",");
      first=false;
      sb.append(expr);
    }
    return sb.append(")").toString();
  }
}
