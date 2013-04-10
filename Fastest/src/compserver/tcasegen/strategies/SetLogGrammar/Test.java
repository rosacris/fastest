package compserver.tcasegen.strategies.SetLogGrammar;


import javax.swing.tree.DefaultMutableTreeNode;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class Test {

	
	
	public static void main(String[] args) {
		
		ANTLRInputStream input = new ANTLRInputStream("\\num \\pfun SENSOR");
        TypeManagerLexer lexer = new TypeManagerLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TypeManagerParser parser = new TypeManagerParser(tokens);
        parser.typeManage();
        DefaultMutableTreeNode root =  parser.getRoot();
        
        
		String s = "100";
		
        ConstantCreator cc = new ConstantCreator(s,root, null, null);
		String salida = cc.getCte();
		
		System.out.println("ENTRADA\n" + s);
		System.out.println("SALIDA\n" + salida);
	}

}