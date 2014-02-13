package client.blogic.testing.refinamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.util.ExprIterator;

import client.blogic.testing.refinamiento.FTCRLParser.DataStructContext;
import client.blogic.testing.refinamiento.FTCRLParser.DotSetOperContext;
import client.blogic.testing.refinamiento.FTCRLParser.INameContext;
import client.blogic.testing.refinamiento.FTCRLParser.LawsContext;
import client.blogic.testing.refinamiento.FTCRLParser.PLCodeContext;
import client.blogic.testing.refinamiento.FTCRLParser.PreambleContext;
import client.blogic.testing.refinamiento.FTCRLParser.RefinementContext;
import client.blogic.testing.refinamiento.FTCRLParser.RefinementSentenceContext;
import client.blogic.testing.refinamiento.FTCRLParser.SExprRefinementContext;
import client.blogic.testing.refinamiento.FTCRLParser.SNameContext;
import client.blogic.testing.refinamiento.FTCRLParser.UutContext;
import client.blogic.testing.refinamiento.basicrefinement.*;

public class FTCRLJavaVisitor extends FTCRLBaseVisitor<Value> {

	private String declarationList = "";
	private String assignmentList = "";
	private HashMap<String,String> zValuesMap = new HashMap<String,String>();
	private HashMap<String,String> zTypesMap = new HashMap<String,String>();

	public void printDeclaration(String line){
		declarationList = declarationList.concat(line + ";\n");
	}
	
	public void printAssignment(String line){
		assignmentList = assignmentList.concat(line + ";\n");
	}
	
	@Override
	public Value visitRefinementRule(FTCRLParser.RefinementRuleContext ctx){

		//Analizamos el preambulo
		PreambleContext preamble = ctx.preamble();
		this.visit(preamble);
		//Despues del preambulo, imprimimos la definicion de la clase main
		System.out.println("public class main {\n" +
				"public static void main(String[]) {\n" +
				"Init init = new Init();" + 
				"{System.out.print(\"Initialization error\"); System.exit(0);}");

		//Analizamos las laws
		LawsContext laws = ctx.laws();
		this.visit(laws);
		System.out.print(declarationList);
		System.out.print(assignmentList);

		//Analizamos la uut
		UutContext uut = ctx.uut();
		this.visit(uut);

		//Cerramos la clase Java y el metodo Main
		System.out.println("}\n}");

		return null;
	}	

	@Override
	public Value visitPreamble(FTCRLParser.PreambleContext ctx){

		List<PLCodeContext> list = ctx.pLCode();
		for (PLCodeContext pl : list) {
			System.out.println(pl.anychar().getText());
		}

		return null;
	}

	@Override
	public Value visitUut(FTCRLParser.UutContext ctx){


		List<INameContext> list = ctx.iName();
		int size = list.size();

		//El ultimo iname, luego de "MODULE", es el nombre de la clase java
		String javaClass = list.get(size-1).getText();
		System.out.println(javaClass + " test = new " + javaClass + "();" );

		//Imprimimos la llamada al método de la clase
		System.out.print("test." + list.get(0).getText() + "(");

		if (size > 2) {
			int i = 1;
			System.out.print(list.get(i).getText());
			for (i = 2; i < size -1; i++) {
				System.out.print("," + list.get(i).getText());
			}
		}

		System.out.println(");");
		return null;
	}

	@Override
	public Value visitRefinementLaw(FTCRLParser.RefinementLawContext ctx){

		//Obtenemos los nombres de las variables Z que se van a utilizar
		//y se van a pasar como argumento a las RefinementSentence
		List<String> zVars = new ArrayList<String>();
		Iterator<SNameContext> itZVars = ctx.sName().iterator();
		while(itZVars.hasNext()){
			zVars.add(itZVars.next().getText());
		}

		//Iteramos sobre las RefinementSentence para pasar como argumento las variables Z
		Iterator<RefinementSentenceContext> itRefinementSentence = ctx.refinementSentence().iterator();
		while (itRefinementSentence.hasNext()){
			this.visitRefinementSentence(itRefinementSentence.next(), zVars);
		}

		return null;
	}

	public Value visitRefinementSentence(FTCRLParser.RefinementSentenceContext ctx, List<String> zVars){

		//Si se utilizan nuevas variables Z, pasamos esas, y sino utilizamos zVars que nos llegaron del padre

		if (ctx.refinement() != null){ //Si es un refinement
			this.visitRefinement(ctx.refinement(), zVars, null, null);

		} else { //Si hay nuevas sName
			zVars = new ArrayList<String>();
			Iterator<SNameContext> itZVars = ctx.sName().iterator();
			while(itZVars.hasNext()){
				zVars.add(itZVars.next().getText());
			}

			this.visitRefinementSentence(ctx.refinementSentence(), zVars);
		}

		return null;
	}
	
	//@Override
	public Value visitAsRefinement(FTCRLParser.AsRefinementContext ctx, Replacement replaceValue, String record, String varName, SExpr zExpr, int position){
		
		DataStructContext dataStruct = ctx.dataStruct();
		
		//Si es una lista
		if (dataStruct.list() != null) {
			if (!ctx.refinement().isEmpty()) {
				//Si es una lista, no pasamos ningun record (por eso null), ya que no se utilizan "mas abajo" en el arbol
				String withVariable = refineWITH(ctx.refinement(), replaceValue, null);
				printAssignment(varName + ".add(" + withVariable + ")");
			} else {
				refineFromZToJava(zExpr, "LIST", new SExpr(varName, ""));
			}
			
		//Si es un record
		} else if (FTCRLUtils.isRecord(dataStruct.getText())) {
			//Si es un record, pasamos "mas abajo" en el árbol el record creado
			refineWITH(ctx.refinement(), replaceValue, record);
		
			//Si es un array
		} else if (FTCRLUtils.isArray(dataStruct.getText())) {
			if (!ctx.refinement().isEmpty()) {
				//Si es una array, no pasamos ningun record (por eso null), ya que no se utilizan "mas abajo" en el arbol
				String withVariable = refineWITH(ctx.refinement(), replaceValue, null);
				printAssignment(varName + "[" + position + "] = " + withVariable);
			} else {
				refineFromZToJava(zExpr, "ARRAY", new SExpr(varName, ""));
			}
		}
		
		return null;
	}
	
	//@Override
	public Value visitIExprRefinement(FTCRLParser.IExprRefinementContext ctx, Replacement replacement, String record, String varName, SExpr zExpr){

		if (ctx.asRefinement() != null) {
			//visito el asRefinement, pasando el nombre de la variable y el valor en Z a refinar
			visitAsRefinement(ctx.asRefinement(), replacement, record, varName, zExpr, -1);
		} else {
			refineFromZToJava(zExpr, "BASIC", new SExpr(varName, ""));
		}

		return null;
	}
	 
	//@Override
	public String visitRefinement(FTCRLParser.RefinementContext ctx, List<String> zVars, String record, Replacement replaceValue){

		String varName = "";
		//Calculamos el valor del lado izquierdo
		String replaceExp = "";
		if (ctx.sExprRefinement() != null) {
			//Si hay SExpr, calculamos su valor
			replaceExp = ctx.sExprRefinement().getText();
		} else {
			//Si no hay SExpr, debe obtener el valor de la variable Z
			replaceExp = zVars.get(0);
		}
		SExpr zExpr = FTCRLUtils.sExpr(replaceExp, replaceValue, zValuesMap, zTypesMap);

		//Observamos el lado derecho
		//Puede ser una variable o un tipo
		String refS = ctx.iExprRefinement().iName().getText();
		String recordType = FTCRLUtils.recordType(refS);
		String recordAtribute = FTCRLUtils.recordAtribute(refS);

		//Debo refinar element a refType
		if (Character.isLowerCase(refS.charAt(0))){ //Si es una variable, no debo crear un record
			varName = recordType;
		} else { //Si es un tipo, debo crear un elemento del mismo

			if (record == null) {
				//Elijo un nombre random
				varName = recordType.toLowerCase() + ((int) (Math.random()*100));
				printDeclaration(recordType + " " + varName);
				record = varName;
			} else {
				//Uso un record ya creado
				varName = record; //Esto no deberia ser null
			}
			//assignmentList = assignmentList.concat(varName + recordAtribute + " = " + element + ";\n");
		}
		
		
		//Si es un conjunto y ademas tiene WITH
		if (FTCRLUtils.isSet(zExpr.exp) && (ctx.iExprRefinement().asRefinement() != null) 
				&& !ctx.iExprRefinement().asRefinement().refinement().isEmpty()) {
			Iterator<String> itElements = new common.util.ExprIterator(zExpr.exp);
			//Iteramos sobre los elementos del conjunto
			int position = 0;
			while (itElements.hasNext()){
				String elem = itElements.next();
				String elemType = FTCRLUtils.getChildType(zExpr.type, 0);

				//Creamos el nuevo reemplazo
				replaceValue = new Replacement(replaceExp, elem, elemType);
				visitAsRefinement(ctx.iExprRefinement().asRefinement(), replaceValue, record, varName, zExpr, position);
				
				//Incrementamos la posición del nodo
				//Esto se usa en los array, para saber en que posición va
				position++;
			}
			//Si no es un conjunto pero tiene WITH
		} else if (!FTCRLUtils.isSet(zExpr.exp) && (ctx.iExprRefinement().asRefinement() != null) 
				&& !ctx.iExprRefinement().asRefinement().refinement().isEmpty()) {

			//Creamos el nuevo reemplazo
			replaceValue = new Replacement(replaceExp, zExpr.exp, zExpr.type);
			visitAsRefinement(ctx.iExprRefinement().asRefinement(), replaceValue, record, varName, zExpr, 0);

		//No tiene WITH
		} else {
			
			visitIExprRefinement(ctx.iExprRefinement(), null, record, varName + recordAtribute, zExpr);
			
		}

		return varName;
	}
	
	private SExpr visitDotSetOper(DotSetOperContext ctx, SExpr v) {
		//si es una tupla con (,), le pongo corchetes para que sea conjunto
		
		v.exp = (v.exp.charAt(0)=='(')?"{"+v.exp+"}":v.exp;
		ExprIterator itElements = new common.util.ExprIterator(v.exp);
				
		String s[];
		s = v.exp.split(",");
		String oper = ctx.getText();
		if(oper.contains("DOM")){
			String value = s[0].substring(2);
			String type = FTCRLUtils.getChildType(v.type, 0); //A \cross B --> A
			return new SExpr(value, type);
		}
		else if(oper.contains("RAN")){
			String value = s[1].substring(0,s[1].length()-2);
			String type = FTCRLUtils.getChildType(v.type, 1); //A \cross B --> B
			return new SExpr(value, type);
			
		}
		else if(oper.contains("#")){
			String value = String.valueOf(itElements.cardinalidad());
			String type = "\\num";
			return new SExpr(value, type);
		}
		else if (ctx.DIGIT() != null) { //Operador "."
			int atributeNumber = Integer.parseInt(ctx.DIGIT().getText());
			ExprIterator itElements2 = new common.util.ExprIterator(v.exp.substring(1, v.exp.length()-1));
			for (int i = 1; i < atributeNumber; i++)
				itElements2.next();
			String value = itElements2.next();
			String type = FTCRLUtils.getChildType(v.type, atributeNumber-1);
			return new SExpr(value, type);
		}
		return null;
	}
	
	private SExpr visitZExprSet(FTCRLParser.ZExprSetContext ctx,Replacement replacement, HashMap<String, String> zValuesMap, HashMap<String, String> zTypesMap) {
		
		if (ctx.sName() != null){
			SExpr v = null;
			String sName = ctx.sName().getText();
			// sName tiene que ser si o si o el remplazo o alguna variable del hashmap
			if (replacement != null && replacement.exp != null && replacement.exp.equals(sName))
				v = new SExpr(replacement.value, replacement.type);
			
			else if (zValuesMap != null && zValuesMap.get(sName) != null )
				v = new SExpr(zValuesMap.get(sName), zTypesMap.get(sName));
			
			return visitDotSetOper(ctx.dotSetOper(),v);
			
		}
		return null;
	}
	
	public SExpr visitSExprRefinement(FTCRLParser.SExprRefinementContext ctx,Replacement replacement,HashMap<String,String> zValuesMap, HashMap<String,String> zTypesMap){
		
		if (ctx.sName()!=null){
			String r = ctx.sName().getText();
			if (replacement != null && replacement.exp != null && replacement.exp.equals(r))
				return new SExpr(replacement.value, replacement.type);
			
			else if (zValuesMap != null && zValuesMap.get(r) != null ){
				return new SExpr(zValuesMap.get(r), zTypesMap.get(r));
				
			}
			return null;
		}
		
		else if(ctx.zExprSet()!=null){
			return visitZExprSet(ctx.zExprSet(),replacement,zValuesMap, zTypesMap);
		}
		else if(ctx.zExprNum()!=null){
			
		}
		return null;
		
	}

	private String refineWITH(List<RefinementContext> refinements, Replacement replace, String record) {

		Iterator<RefinementContext> it = refinements.iterator();
		while (it.hasNext()){

			record = this.visitRefinement(it.next(), null, record, replace);
		}

		return record;
	}

	//Refina el zValue a su valor correspondiente en Java
	//Debe refinar el valor de Z ZValue, al tipo toType. Y realizar lo que deba en typeVariable
	//Por ejemplo, si ZValue es una lista \langle 1, 2, 3 \rangle y toType es "LIST"
	//y typeVariable es l,
	//debera hacer las asignaciones: l.add(1); l.add(2); l.add(3) 
	public String refineFromZToJava(SExpr zExpr, String toType, SExpr javaExpr) {
		
		//En base al tipo en Z de sValue debo utilizar una determinada clase para refinarla
		if (zExpr.type.equals("\\num") || zExpr.type.equals("\\nat"))
			return NumRefinement.refine(zExpr, toType, javaExpr, this);
		else if (zExpr.type.startsWith("\\set"))
			return SetRefinement.refine(zExpr, toType, javaExpr, this);
		return "";
		
		/*
		//Si refinamos a una lista, debemos iterar sobre los elementos de ZValue
		//para ir agregandolos a la variable typeVariable
		if (toType.equals("LIST")){
			//Obtenemos los elementos del conjunto
			Iterator<String> itElements = new common.util.ExprIterator(sValue);
			while (itElements.hasNext()){
				assignmentList = assignmentList.concat(typeVariable + ".add(" + refineFromZToJava(itElements.next(), "BASIC", null) + ");\n");
			}
			return typeVariable;
		} else if (toType.equals("BASIC")){
			if (typeVariable != null) {
				assignmentList = assignmentList.concat(typeVariable + " = " + sValue + ";\n");
				return typeVariable;
			} else {
				return sValue;
			}
		} 
		return typeVariable;
		*/
	}

	//Este metodo se utiliza para determinar el caso de prueba que se utilizará
	//Almacena los valores de las variables Z en zValuesMap
	public void assignTCase(String tcase){

		this.zValuesMap = FTCRLUtils.createZValuesMap(tcase);
		this.zTypesMap = FTCRLUtils.createZTypesMap(tcase);
	}

}