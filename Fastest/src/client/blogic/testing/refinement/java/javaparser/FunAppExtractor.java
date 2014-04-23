package client.blogic.testing.refinement.java.javaparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import client.blogic.testing.refinement.SExpr;
import client.blogic.testing.refinement.java.javaparser.Java7Parser.FormalParameterDeclsContext;
import client.blogic.testing.refinement.java.javaparser.Java7Parser.NormalClassDeclarationContext;
import client.blogic.testing.refinement.java.javaparser.Java7Parser.NormalParameterDeclContext;

public class FunAppExtractor extends Java7ParserBaseVisitor{

	LinkedList<SExpr> argsTypes;
	String moduleName = "";
	String functionName = "";
	String functionType = "";

	public FunAppExtractor(String moduleName, String funName){
		this.argsTypes = new LinkedList<SExpr>();
		this.functionName = funName;
		this.moduleName = moduleName;
	}

	public String getFunType(){
		return this.functionType;
	}
	
	public LinkedList<SExpr> getArgsTypes(){
		return this.argsTypes;
	}
	
	@Override
	public Object visitNormalClassDeclaration(NormalClassDeclarationContext ctx){
		if (ctx.Identifier().getText().equals(moduleName)){
			return visitChildren(ctx);
		}
		return null;
	}
	
	@Override
	public Object visitMethodDeclaration(Java7Parser.MethodDeclarationContext ctx){

		if (ctx.Identifier().getText().equals(functionName)){
			//Buscamos los nombres (y tipos), de los argumentos
			FormalParameterDeclsContext params = ctx.formalParameters().formalParameterDecls();
			if (!params.normalParameterDecl().isEmpty()){
				Iterator<NormalParameterDeclContext> childs = params.normalParameterDecl().iterator();
				while (childs.hasNext()){
					NormalParameterDeclContext c = childs.next();
					String argName = c.Identifier().getText();
					String argType = c.type().getText(); 
					argsTypes.add(new SExpr(argName, argType));
				}
			}
			functionType = ctx.type().getText();
		}
		return null;
	}

}