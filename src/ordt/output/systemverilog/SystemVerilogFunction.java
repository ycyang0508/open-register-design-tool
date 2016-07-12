package ordt.output.systemverilog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ordt.output.OutputLine;

public class SystemVerilogFunction {
   private String name;
   private String retType;
   private boolean isVirtual = false;
   private List<SVMethodIO> ioList = new ArrayList<SVMethodIO>();
   private List<String> comments = new ArrayList<String>();
   private List<String> statements = new ArrayList<String>();
   
   // constructor
   public SystemVerilogFunction(String retType, String name) {
		super();
		this.retType = retType;
		this.name = name;
   }
   
   // public methods
   
   /** set this method as virtual */
   public void setVirtual() {
	   this.isVirtual = true;
   }
   
   /** add an IO definition to this sv method */
   public void addIO(String dir, String type, String name, String defaultValue) {
	   ioList.add(new SVMethodIO(dir, type, name, defaultValue, false));
   }
   
   public void addIOArray(String dir, String type, String name, String defaultValue) {
	   ioList.add(new SVMethodIO(dir, type, name, defaultValue, true));
   }
   
   /** add an IO definition to this sv method */
   public void addIO(String type, String name) {
	   ioList.add(new SVMethodIO(type, name, false));
   }

   public void addIOArray(String type, String name) {
	   ioList.add(new SVMethodIO(type, name, true));
       
   }

   /** add a comment line to this sv method */
   public void addComment(String comment) {
	   comments.add(comment);
   }
   
   /** add a statement line to this sv method */
   public void addStatement(String stmt) {
	   statements.add(stmt);
   }

   /** generate sv string list for this method */
   public List<String> genSV() {
	   List<String> retList = new ArrayList<String>();
	   retList.add("");  // leading blank line
	   // add comments
	   String prefix = "/* ";
	   Iterator<String> it = comments.iterator();
	   while (it.hasNext()) {
		   String val = it.next();
		   String suffix = it.hasNext()? "" : " */"; 
		   retList.add(prefix + val + suffix);
		   prefix = " * ";
	   }
	   // add signature
	   retList.add(genSignature()); 
	   // add statements
	   it = statements.iterator();
	   while (it.hasNext()) {
		   retList.add("  " + it.next());
	   }
	   // add closing line
	   retList.add(genEnd()); 
	   
	   return retList;
   }
   
   /** generate OutputLine list for this method */
   public List<OutputLine> genOutputLines(int indentLvl) {
	   List<OutputLine> retList = new ArrayList<OutputLine>();
	   Iterator<String> it = genSV().iterator();
	   while (it.hasNext()) {
		   retList.add(new OutputLine(indentLvl, it.next()));
	   }	   
	   return retList;
   }
  
   // protected methods
   
   /** generate IO definition string for this method */
   protected String genIODefs(boolean showIODir) {
	   if (ioList.isEmpty()) return "()";
	   String retStr = "(";
	   Iterator<SVMethodIO> it = ioList.iterator();
	   while (it.hasNext()) {
		   retStr = retStr + it.next().getDef(showIODir);
		   String suffix = it.hasNext()? ", " : ")"; 
		   retStr = retStr + suffix;
	   }
	   return retStr;
   }
   
   /** generate signature string for this method (function) */
   protected String genSignature() {
	   boolean showIODir = false;
	   for (SVMethodIO io: ioList) if (!"input".equals(io.dir)) showIODir = true;
	   String retStr = isVirtual? "virtual function" : "function";
	   String suffix = " " + name + genIODefs(showIODir) + ";";
	   retStr = (retType == null)? retStr + suffix : retStr + " " + retType + suffix; 
	   return retStr;
   }
   
   /** generate closing string for this method (function) */
   protected String genEnd() {
	   return "endfunction: " + name;
   }
   
   // nested IO class
   private class SVMethodIO {
	   String dir;
	   String type;
	   String name;
	   String defaultValue;
	   boolean isArray;
	   
	   public SVMethodIO(String dir, String type, String name, String defaultValue, boolean isArray) {
			super();
			this.dir = dir;
			this.type = type;
			this.name = name;
			this.defaultValue = defaultValue;
			this.isArray = isArray;
	   }
	   
	   public SVMethodIO(String type, String name, boolean isArray) {
			super();
			this.dir = "input";
			this.type = type;
			this.name = name;
			this.defaultValue = null;
			this.isArray = isArray;
	   }
	   
	   public String getDef(boolean showIODir) {
		   String retStr = showIODir? dir + " " : "";
		   String defStr = (defaultValue != null)? " = " + defaultValue : "";
		   String nameStr = isArray? name + "[$]" : name;
		   retStr = retStr + type + " " + nameStr + defStr;
		   return retStr;
	   }

   }
}
