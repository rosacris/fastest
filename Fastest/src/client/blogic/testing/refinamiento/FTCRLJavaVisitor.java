package client.blogic.testing.refinamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import common.util.ExprIterator;
import client.blogic.testing.refinamiento.FTCRLParser.DataStructContext;
import client.blogic.testing.refinamiento.FTCRLParser.DotSetOperContext;
import client.blogic.testing.refinamiento.FTCRLParser.LawContext;
import client.blogic.testing.refinamiento.FTCRLParser.LawsContext;
import client.blogic.testing.refinamiento.FTCRLParser.RefinementContext;
import client.blogic.testing.refinamiento.FTCRLParser.RefinementSentenceContext;
import client.blogic.testing.refinamiento.FTCRLParser.SNameContext;
import client.blogic.testing.refinamiento.FTCRLParser.UutContext;
import client.blogic.testing.refinamiento.basicrefinement.*;

public class FTCRLJavaVisitor extends FTCRLBaseVisitor<Value> {

	private String declarationList = ""; //String donde se acumulan las sentencias de declaracion
	private String assignmentList = "";  //String donde se acumulan las sentencias de asignacion
	//Map con los valores de las variables Z, obtenidos de la especificaciones
	private HashMap<String,String> zValuesMap = new HashMap<String,String>();
	//Map con los tipos de las variables Z, obtenidos de la especificaciones
	private HashMap<String,String> zTypesMap = new HashMap<String,String>();
	//Map con los tipos de las variables java, obtenidos del codigo enunciado en las reglas de refinamiento
	private HashMap<String,String> javaTypesMap = new HashMap<String,String>();
	//Module del UUT
	private String moduleName = "";
	//Argumentos del UUT
	private LinkedList<String> uutArgs= new LinkedList<String>();
	//Variable para dar nombre a las variables que se crean
	private static int varNumber = 0;
	//Map con los valores almacenados por los REF
	public static HashMap<String, String> references = new HashMap<String, String>();
	//Variables que deben ser almacenadas, porque van a ser referenciadas
	public static LinkedList<String> referencedVars = new LinkedList<String>();
	//Variables auxiliar para determinar si se debe guardar una referencia
	public static boolean isRef;

	public String getDeclarationList(){
		return declarationList;
	}
	
	public String getAssignementList(){
		return assignmentList;
	}
	
	public void printDeclaration(String line){
		declarationList = declarationList.concat(line + ";\n");
	}

	public void printAssignment(String line){
		assignmentList = assignmentList.concat(line + ";\n");
	}

	@Override
	public Value visitRefinementRule(FTCRLParser.RefinementRuleContext ctx){

		//Inicializamos los nombres que les daremos a las variables
		varNumber = 0;
		//Inicializamos las ref vars
		referencedVars = RefinementRules.getInstance().getReferencedVars();

		//Analizamos el preambulo
		//PreambleContext preamble = ctx.preamble();
		//this.visit(preamble);

		//Cargamos el codigo java
		extractJavaTypes(FTCRLUtils.getPreamble());
		//Despues del preambulo, imprimimos la definicion de la clase main
		//System.out.println("public class main {\n" +
		//		"public static void main(String[]) {\n" +
		//		"Init init = new Init();" + 
		//		"{System.out.print(\"Initialization error\"); System.exit(0);}");

		//Obtenemos en nombre del modulo del UUT
		UutContext uut = ctx.uut();
		moduleName = FTCRLUtils.extractModuleName(uut.getText());
		uutArgs = FTCRLUtils.extractUUTArgs(uut.getText());

		//Creamos la variable test, de la clase que vamos a testear
		printDeclaration(moduleName + " test = new " + moduleName + "()" );

		//Analizamos las laws
		LawsContext laws = ctx.laws();
		this.visit(laws);

		System.out.println("-----------------------------------------");
		System.out.print(declarationList);
		System.out.print(assignmentList);
		System.out.println("-----------------------------------------");

		//Analizamos la uut
		//this.visit(uut);

		//Cerramos la clase Java y el metodo Main
		//System.out.println("}\n}");

		return null;
	}	

	@Override
	public Value visitLaws(FTCRLParser.LawsContext ctx){
		Iterator<LawContext> it = ctx.law().iterator();
		while (it.hasNext()){
			isRef = false;
			this.visit(it.next());
		}
		
		return null;
	}
	
	//	@Override
	//	public Value visitPreamble(FTCRLParser.PreambleContext ctx){
	//
	//		List<PLCodeContext> list = ctx.pLCode();
	//		for (PLCodeContext pl : list) {
	//			String javaCode = pl.anychar().getText();
	//			extractJavaTypes(javaCode);
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	public Value visitUut(FTCRLParser.UutContext ctx){
	//
	//
	//		List<INameContext> list = ctx.iName();
	//		int size = list.size();
	//
	//		//El ultimo iname, luego de "MODULE", es el nombre de la clase java
	//		String javaClass = list.get(size-1).getText();
	//		//System.out.println(javaClass + " test = new " + javaClass + "();" );
	//
	//		//Imprimimos la llamada al método de la clase
	//		System.out.print("test." + list.get(0).getText() + "(");
	//
	//		if (size > 2) {
	//			int i = 1;
	//			System.out.print(list.get(i).getText());
	//			for (i = 2; i < size -1; i++) {
	//				System.out.print("," + list.get(i).getText());
	//			}
	//		}
	//
	//		System.out.println(");");
	//		return null;
	//	}

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
	public String visitRefinement(FTCRLParser.RefinementContext ctx, List<String> zVars, String record, Replacement replaceValue){

		String varName = ""; //java
		String varType = ""; //java
		boolean isPrivate = false; //si la variable es privada

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
		varType = FTCRLUtils.getJavaType(refS, javaTypesMap);

		//Debo refinar zExpr a varType

		//Si es una variable que luego será referenciada, lo indico
		if (referencedVars.contains(recordType))
			isRef = true;

		//Primero vemos si debo crear una variable nueva
		//Si refS es una variable, no debo crear un record 
		//ya que es un argumento del metodo de la clase a testear.
		if (Character.isLowerCase(refS.charAt(0))){ 
			varName = recordType; //Saco la parte de los atributos
			//Si es una variable que será parte del argumento de la función, debo crearla
			if (uutArgs.contains(varName)){
				printDeclaration(varType + " " + varName); //La declaro
			} else { //Sino, es un atributo de la clase a testear
				//Veo primero si es una variable privada
				if (FTCRLUtils.isPrivate(moduleName + "." + varName))
					isPrivate = true;

				varType = FTCRLUtils.getJavaType(moduleName + "." + varName, javaTypesMap);
				recordAtribute = "." + varName;
				varName = "test";
			}

			record = varName;

		} else { //Si no es una variable, debo crear un elemento con ese tipo

			if (record == null) {
				//Elijo un nombre random
				varName = newVarName(recordType.toLowerCase());
				printDeclaration(recordType + " " + varName);
				record = varName;
			} else {
				//Uso un record ya creado
				varName = record; //Esto no deberia ser null
			}
		}

		//Si es "private" en java, debo usar reflection
		String privateFieldVar = "";
		if (FTCRLUtils.isPrivate(refS) || isPrivate){
			isPrivate = true;
			//Usamos una variable para el atributo de la clase
			privateFieldVar = newVarName(varName.toLowerCase());
			String field = recordAtribute.substring(1);
			printDeclaration("Field " + privateFieldVar + " = " + record + ".getClass().getDeclaredField(\"" + field + "\")");
			printDeclaration(privateFieldVar + ".setAccessible(true)");
			//Como es private debo crear una nueva variable a usar
			varName = newVarName(varName.toLowerCase());
			printDeclaration(varType + " " + varName);
			recordAtribute = "";
		}

		//Si es un conjunto y ademas tiene WITH
		if ((FTCRLUtils.isSet(zExpr.type) || FTCRLUtils.isSeq(zExpr.type)) && (ctx.iExprRefinement().asRefinement() != null) 
				&& !ctx.iExprRefinement().asRefinement().refinement().isEmpty()) {
			Iterator<String> itElements = new common.util.ExprIterator(zExpr.exp);

			String elemType = FTCRLUtils.getChildType(zExpr.type, 0);
			//Si es una lista, debo modificar el tipo del elemento transformandolo en una tupla
			elemType = FTCRLUtils.convertToSeq(zExpr.type, elemType);

			//Iteramos sobre los elementos del conjunto
			int position = 0;
			while (itElements.hasNext()){
				String elem = itElements.next();
				//Si es una lista, debo modificar el elemento transformandolo en una tupla
				if (FTCRLUtils.isSeq(zExpr.type)){
					elem = "(" + (position+1) + "," + elem + ")";
				}

				//Creamos el nuevo reemplazo
				replaceValue = new Replacement(replaceExp, elem, elemType);
				visitAsRefinement(ctx.iExprRefinement().asRefinement(), replaceValue, record, new SExpr(varName+recordAtribute, varType), zExpr, position);

				//Incrementamos la posición del nodo
				//Esto se usa en los array, para saber en que posición va
				position++;
			}
			//Si no es un conjunto pero tiene WITH
		} else if (!FTCRLUtils.isSet(zExpr.type) && (ctx.iExprRefinement().asRefinement() != null) 
				&& !ctx.iExprRefinement().asRefinement().refinement().isEmpty()) {

			//Creamos el nuevo reemplazo
			replaceValue = new Replacement(replaceExp, zExpr.exp, zExpr.type);
			visitAsRefinement(ctx.iExprRefinement().asRefinement(), replaceValue, record, new SExpr(varName+recordAtribute, varType), zExpr, 0);

			//No tiene WITH
		} else {
			visitIExprRefinement(ctx.iExprRefinement(), null, record, new SExpr(varName + recordAtribute, varType), zExpr);
		}

		//Si la variable es privada debemos usar reflection
		if (isPrivate){
			printAssignment(privateFieldVar + ".set(" + record + ", " + varName + ")");
			FTCRLUtils.saveReference(record, varName, references, isRef);
			return record;
		}

		return varName;
	}

	//@Override
	public Value visitAsRefinement(FTCRLParser.AsRefinementContext ctx, Replacement replaceValue, String record, SExpr javaExpr, SExpr zExpr, int position){

		DataStructContext dataStruct = ctx.dataStruct();

		//Si es una lista
		if (dataStruct.list() != null) {
			if (!ctx.refinement().isEmpty()) { //Si tiene WITH
				//Si es una lista, no pasamos ningun record (por eso null), ya que no se utilizan "mas abajo" en el arbol
				String withVariable = refineWITH(ctx.refinement(), replaceValue, null);
				printAssignment(javaExpr.exp + ".add(" + withVariable + ")");
				FTCRLUtils.saveReference(javaExpr.exp + "[" + position + "]", withVariable, references, isRef);
			} else {
				refineFromZToJava(zExpr, "LIST", javaExpr);
			}

			//Si es un record
		} else if (dataStruct.getText().equals("RECORD")) {
			//Si es un record, pasamos "mas abajo" en el árbol el record creado
			refineWITH(ctx.refinement(), replaceValue, record);

			//Si es un array
		} else if (dataStruct.getText().equals("ARRAY")) {
			if (!ctx.refinement().isEmpty()) { //Si tiene WITH
				//Si es una array, no pasamos ningun record (por eso null), ya que no se utilizan "mas abajo" en el arbol
				String withVariable = refineWITH(ctx.refinement(), replaceValue, null);
				printAssignment(javaExpr.exp + "[" + position + "] = " + withVariable);
				FTCRLUtils.saveReference(javaExpr.exp + "[" + position + "]", withVariable, references, isRef);
			} else {
				refineFromZToJava(zExpr, "ARRAY", javaExpr);
			}

			//Si es una referencia
		} else if (dataStruct.reference2() != null) {
			String reference = FTCRLUtils.findReference(zExpr.exp, dataStruct.reference2().iName().getText(), references, uutArgs);
			if (reference != null){
				printAssignment(javaExpr.exp + " = " + reference);
				FTCRLUtils.saveReference(javaExpr.exp, reference, references, isRef);
			}

			//Si es una tabla
		} else if (dataStruct.table() != null) {
			if (!ctx.refinement().isEmpty()) { //Si tiene WITH
				//Simplemente visito los refinements
				String withVariable = refineWITH(ctx.refinement(), replaceValue, null);
			} else {
				//En este caso debo crear un if, segun marca la pagina 46.
				//Es medio complicado
			}
		}

		return null;
	}

	//Este metodo permite visitar un SExprRefinement para obtener su valor y su tipo.
	public SExpr visitSExprRefinement(FTCRLParser.SExprRefinementContext ctx,Replacement replacement,HashMap<String,String> zValuesMap, HashMap<String,String> zTypesMap){

		//Si simplemente es un SName, debe estar en el replacement o en el Map de Z
		if (ctx.sName()!=null)
			return visitSName(ctx.sName(),replacement,zValuesMap, zTypesMap);
		//Si es un ZExprSet, lo visito y devuelvo su SExpr
		else if(ctx.zExprSet()!=null)
			return visitZExprSet(ctx.zExprSet(),replacement,zValuesMap, zTypesMap);
		else
			return null;
	}

	//@Override
	public Value visitIExprRefinement(FTCRLParser.IExprRefinementContext ctx, Replacement replacement, String record, SExpr javaExpr, SExpr zExpr){

		if (ctx.asRefinement() != null) {
			//visito el asRefinement, pasando el nombre de la variable y el valor en Z a refinar
			visitAsRefinement(ctx.asRefinement(), replacement, record, javaExpr, zExpr, -1);
		} else {
			refineFromZToJava(zExpr, "BASIC", javaExpr);
		}

		return null;
	}

	//Este metodo permite visitar un ZExprSet para obtener su valor y su tipo.
	private SExpr visitZExprSet(FTCRLParser.ZExprSetContext ctx,Replacement replacement, HashMap<String, String> zValuesMap, HashMap<String, String> zTypesMap) {

		//En el primer caso, el ZExprSet se conforma de un SName, y quizas un dotSetOperator
		if (ctx.sName() != null){
			//Del sName debo obtener el SExpr correspondiente
			SExpr s = visitSName(ctx.sName(), replacement, zValuesMap, zTypesMap);
			//Y luego visito los DotSetOper, pero antes veo si debo hacer el replace
			String sName = ctx.sName().getText();
			List<DotSetOperContext> dot = ctx.dotSetOper();
			int replacePos = -1;
			if (replacement != null && !dot.isEmpty()) {

				//En la primer pasada intento hacer el replace
				for (replacePos++; replacePos < dot.size(); replacePos++){
					//Primero veo si debo hacer el replace
					sName = sName.concat("." + dot.get(replacePos).getText());
					if (sName.equals(replacement.exp)){
						s = new SExpr(replacement.value, replacement.type);
						break;
					}
				}
				//Si di la vuelta completa, "reseteo"
				if (replacePos == dot.size())
					replacePos = -1;
			}
			//Luego visito los DotSetOper que no fueron utilizados en el replace
			//donde se aplican los operadores a las expresiones
			for (replacePos++; replacePos < dot.size(); replacePos++){
				s = visitDotSetOper(dot.get(replacePos), s);
			}

			return s;

		} else
			return null;
	}

	//Este metodo permite visitar un SName para obtener su valor y su tipo.
	public SExpr visitSName(FTCRLParser.SNameContext ctx,Replacement replacement,HashMap<String,String> zValuesMap, HashMap<String,String> zTypesMap){

		String s = ctx.getText();
		//Si el replacement es el SName, devuelvo directamente su valor y tipo
		if (replacement != null && replacement.exp != null && replacement.exp.equals(s))
			return new SExpr(replacement.value, replacement.type);
		//Y sino lo busco en el Map de Z
		else if (zValuesMap != null && zValuesMap.get(s) != null )
			return new SExpr(zValuesMap.get(s), zTypesMap.get(s));
		//No deberia devolver null
		else
			return null;
	}

	//Este metodo permite visitar un DotSetOper para obtener su valor y su tipo.
	//Se agrega el argumento SName, que se utilizará cuando haya más de un DotSetOper
	//para llevar la cuenta de cuando hay que hacer el replace.
	private SExpr visitDotSetOper(DotSetOperContext ctx, SExpr v) {

		//si es una tupla con (,), le pongo corchetes para que sea conjunto y asi poder usar su iterador
		v.exp = (v.exp.charAt(0)=='(')?"{"+v.exp.substring(1, v.exp.length()-1)+"}":v.exp;
		ExprIterator itElements = new common.util.ExprIterator(v.exp);

		String oper = ctx.getText();
		if(oper.contains("DOM")){ //Operador DOM
			String value = itElements.next();
			String type = FTCRLUtils.getChildType(v.type, 0); //A \cross B --> A
			return new SExpr(value, type);
		}
		else if(oper.contains("RAN")){ //Operador RAN
			itElements.next();
			String value = itElements.next();
			String type = FTCRLUtils.getChildType(v.type, 1); //A \cross B --> B
			return new SExpr(value, type);

		}
		else if(oper.contains("#")){ //Operador Cardinalidad
			String value = String.valueOf(itElements.cardinalidad());
			String type = "\\num";
			return new SExpr(value, type);
		}
		else if (ctx.digit() != null) { //Operador "."
			int atributeNumber = Integer.parseInt(ctx.digit().getText());
			for (int i = 1; i < atributeNumber; i++)
				itElements.next();
			String value = itElements.next();
			String type = FTCRLUtils.getChildType(v.type, atributeNumber-1);
			return new SExpr(value, type);
		}
		//si era un conjunto o una tupla devuelvo o un conjunto o un elemento
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
		else if (FTCRLUtils.isSet(zExpr.type))
			return SetRefinement.refine(zExpr, toType, javaExpr, this);
		else if (FTCRLUtils.isSeq(zExpr.type)){
			//Si es una lista refino como si fuese un conjunto
			zExpr.type = "\\power(" + FTCRLUtils.getChildType(zExpr.type,0) + ")";
			zExpr.exp = zExpr.exp.replaceFirst("^\\\\langle", "\\\\{");
			zExpr.exp = zExpr.exp.replaceFirst("\\\\rangle$", "\\\\}");
			return SetRefinement.refine(zExpr, toType, javaExpr, this);
		} else {
			//Es un tipo basico o enumerado
			return GivenTypeRefinement.refine(zExpr, toType, javaExpr, this);
		}
		//return "";
	}

	//Este metodo se utiliza para determinar el caso de prueba que se utilizará
	//Almacena los valores de las variables Z en zValuesMap, y sus tipos en ZTypesMap
	public void assignTCase(String tcase){

		this.zValuesMap = FTCRLUtils.createZValuesMap(tcase);
		this.zTypesMap = FTCRLUtils.createZTypesMap(tcase);
	}

	//Este metodo se utiliza para determinar los tipos de las variables java
	//Almacena los tipos de las variables Z en javaTypesMap
	public void extractJavaTypes(String javaCode){

		this.javaTypesMap = FTCRLUtils.createJavaTypesMap(javaCode);
	}

	private String newVarName(String name) {
		name += varNumber;
		varNumber++;
		return name;
	}

}