package compserver.tcasegen.strategies.SetLogGrammar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import compserver.tcasegen.strategies.SetLogGrammar.StringPointer;


public class ConstantCreator {

	private DefaultMutableTreeNode arbol;
	private String expr;
	private HashMap<String,String> tipos;
	private HashMap<String,String> zNames;
	private HashMap<String,String> notEquals;
	private HashMap<String,StringPointer> slVars;
	private List<String> basicTypes;
	
	private static int postfijo;
	private String getNumber(){
		return String.valueOf(postfijo++);
	}
	//estructura de datos para la recursion
	private class scte{
		String s;
		int i;
		public scte(String s, int i){
			this.s = s; this.i = i;
		}
	}
	
	private String printTree(DefaultMutableTreeNode tree){
		if (tree.isLeaf()) 
			return (String) tree.getUserObject();
		else if (tree.getChildCount() == 1)
			if ( ((String) tree.getUserObject()).equals("()")) //REVISAR
				return "(" + printTree((DefaultMutableTreeNode) tree.getChildAt(0)) + ")";
			else
				return (String) tree.getUserObject() + printTree((DefaultMutableTreeNode) tree.getChildAt(0));
		else //tiene dos hijos
			return printTree((DefaultMutableTreeNode) tree.getChildAt(0)) + ((String) tree.getUserObject()) + printTree((DefaultMutableTreeNode) tree.getChildAt(1));
	}
	
	//llena la estructura freeTypes, la cual se usa para saber el tipo de una variabla
	//que no figura en slvars, a partir de un elemento que esta en desigualdad en contraint
	private List<String> llenarBasicTypes(){
    	List<String> s = new LinkedList();
    	Iterator<String> iterator = tipos.keySet().iterator();
    	String key,valor,nomtipo;
    	while (iterator.hasNext()) { 
    		key = iterator.next().toString();
			valor = tipos.get(key);
			//EnumerationType:FT:{elem1,elem2}
			if (valor.startsWith("BasicType")){
				String[] aux = valor.split(":");
				nomtipo =  aux[1];
				s.add(nomtipo);
			}
    	}
    	
    	return s;
    }
	
	
	
	private String fullCte(DefaultMutableTreeNode nodo, String var){
		String salida = null;
		String tipo;
		
		//si no tiene desigualdades
		if(notEquals ==null)
			return  cteCanonica(nodo,var);
		
		if( notEquals.get(var)==null)
			return  cteCanonica(nodo,var);
		
		tipo = printTree(nodo);
		//si tiene tipo num o nat, entonces seguro genero distinto
		if(tipo.contains("num") || tipo.contains("nat"))
			return cteCanonica(nodo,var);
		
		// si tiene tipo basico, entonces sseguro genero distinto
		if (this.basicTypes == null)
			llenarBasicTypes();
		if (basicTypes!=null){
		Iterator<String> it = basicTypes.iterator();
			while(it.hasNext()){
				if (tipo.contains(it.next()))
					return cteCanonica(nodo,var);
			}
		}
		return salida = "ssss";
	}
	
	private String cteCanonica(TreeNode nodo,String var) {
		String ct = nodo.toString();
		if (ct.equals("\\num") || ct.equals("\\nat") ) {
			return this.getNumber();
		} else if(ct == "()"){
			return cteCanonica(nodo.getChildAt(0),var);
		} else if (ct.equals("\\pfun") || ct.equals("\\fun") || ct.equals("\\rel")) {
			return "\\{ "+"(" + cteCanonica(nodo.getChildAt(0),var) + " \\mapsto " + cteCanonica(nodo.getChildAt(1),var) + ")" + " \\}";
		} else if(ct.equals("\\cross")){
			return cteCanonica(nodo.getChildAt(0),var) + " \\mapsto " + cteCanonica(nodo.getChildAt(1),var)  ;
		} else if (ct.equals("\\power")) {
			return "\\{ " + cteCanonica(nodo.getChildAt(0),var) + " \\}";
		} else if (ct.equals("\\seq")) {
			return "\\langle " + cteCanonica(nodo.getChildAt(0),var) + " \\rangle";
		}else { 
			String nodoType = tipos.get(ct);
			String cte;
			//si es EnumerationType
			if (nodoType.startsWith("EnumerationType")){
				//si nodoType == EnumerationType:FT:{elem1,elem2}
				// aux = EnumerationType:FT: , elem1 , elem2}
				String[] aux = nodoType.split("[{,]");
				cte =  aux[1];
			}else{
				//si es basicType
				//para cuando se llama con la lista de zName vacia, cuando var ya es una variable Z.
				cte = ct.toLowerCase();
				if(zNames == null)
					cte = cte + var;
				else
				{
					String zname = zNames.get(var);
					if (zname == null)
						cte = cte + this.getNumber();
					else
						cte = cte + zname.replace("?","");
				}
				
				
			}
			return cte;
		}
		
	}

	public scte cte(TreeNode nodo, int iexpr) {
		//obtengo la proxima variable o constante en aux
		Pattern patron = Pattern.compile("(\\w+)");
		Matcher m = patron.matcher(expr);
		m.find(iexpr);
		String aux;
		try{
			aux = m.group();
		}
		catch (Exception e) {
			aux = "";
		}
		char c = expr.charAt(iexpr);
		String ct = nodo.toString();
		scte salida;
		// si es variable auxiliar de {log} genero
		if (Character.isUpperCase(c) || c == '_') {
			String cte = null;
			StringPointer sp;
			
			if (slVars!=null){
				sp = slVars.get(aux);
				//no cambiar el orden de la conjuncion, para que pueda chequear el lado derecho, el izquierdo debe ser falso
				if (sp != null && sp.toString() != null)
					cte = sp.toString();
				else
					cte = fullCte((DefaultMutableTreeNode) nodo,aux);
			}
			else
				cte = fullCte((DefaultMutableTreeNode) nodo,aux);
			
			if (slVars!=null)
				slVars.put(aux, new StringPointer(cte));
			salida = new scte(cte,iexpr + aux.length());//la longitud que va es la original, no la inventada
			//porque siempre me muevo en el string expr original
			return salida;
		}
		// si es constante la meto
		else if (Character.isLowerCase(c) || Character.isDigit(c) || c == '-') {
			String s;
			if (c == '-') 
				s = "\\neg " + aux;
			else 
				s = aux;
			salida = new scte(s,iexpr + aux.length());
			return salida;
		} 
		else if (ct.equals("()")){
			salida = cte(nodo.getChildAt(0),iexpr);
			return salida;
		}
		else if (ct.equals("\\fun") ||ct.equals("\\pfun")|| ct.equals("\\rel")) {
			//puede venir X ó {X} ó {[X,X]}

			//por que c esta en "{"
			iexpr++; 
			if(	expr.charAt(iexpr)=='}'){
				return new scte("\\{\\}",iexpr+2);
			}
			c = expr.charAt(iexpr);
			scte si1,si2;
			if (c=='[')	{
				si1 = cte(nodo.getChildAt(0), iexpr + 1);
				si2 = cte(nodo.getChildAt(1), si1.i + 1); 
				si2.i ++;
			}
			else{
				si1 = cte(nodo.getChildAt(0), iexpr );
				si2 = cte(nodo.getChildAt(1), iexpr );
			}
			salida = new scte("\\{ " + "(" + si1.s + " \\mapsto " + si2.s + ")",si2.i);
			iexpr = salida.i;
			c = expr.charAt(iexpr);
			while (c==','){
				iexpr++;c = expr.charAt(iexpr);
				if (c=='[')	{
					si1 = cte(nodo.getChildAt(0), iexpr + 1);
					si2 = cte(nodo.getChildAt(1), si1.i + 1);
					si2.i++;
				}
				else{
					si1 = cte(nodo.getChildAt(0), iexpr );
					si2 = cte(nodo.getChildAt(1), iexpr );
				}
				salida.s = salida.s+ "," +  " ("+ si1.s + " \\mapsto " + si2.s + ")";
				iexpr = si2.i;
				c = expr.charAt(iexpr);
			}
			salida.s = salida.s + " \\}";
			salida.i = iexpr+1;
			return salida;

		} 
		else if (ct.equals("\\cross")){
			//caso [X,Y]
			scte s1 = cte(nodo.getChildAt(0), iexpr + 1);
			scte s2 = cte(nodo.getChildAt(1), s1.i + 1); 
			salida = new scte( s1.s + " \\mapsto " + s2.s ,s2.i+1);
			return salida;
		}
		else if (ct.equals("\\power") ) {
			//es por que el tipo \\powe(\\num\\pfun\\A) es equivalente a \\seq A
			if(c == '['){
				DefaultMutableTreeNode naux = new DefaultMutableTreeNode("\\seq");
				// powe->()->x->A        (der)
				//			  ->NUM|NAT  (izq)
				DefaultMutableTreeNode nauxHijo = (DefaultMutableTreeNode) ((nodo.getChildAt(0)).getChildAt(0)).getChildAt(1);
				naux.add(nauxHijo);
				salida = cte(naux,iexpr);
			}
			else{
					//pinta {X,X,X}
					if(	expr.charAt(iexpr+1)=='}'){
						return new scte("\\{\\}",iexpr+2);
					}
					scte s1 = cte(nodo.getChildAt(0), iexpr+1);
					salida = new scte("\\{ " + s1.s ,s1.i);
	
					iexpr = salida.i;
					c = expr.charAt(iexpr);
					while (c == ','){
						s1 = cte(nodo.getChildAt(0), iexpr+1);
						salida.s = salida.s + "," + s1.s;
						iexpr = s1.i;
						c = expr.charAt(iexpr);
					}
					salida.s = salida.s + " \\}";
					salida.i = iexpr+1;
				}
			return salida;
			
		}

		else if (ct.equals("\\seq") ) {
			//pinta [X,X,X]
			if(	expr.charAt(iexpr+1)==']'){
				return new scte("\\langle \\rangle",iexpr+2);
			}
			scte s1 = cte(nodo.getChildAt(0), iexpr+1);
			salida = new scte("\\langle " + s1.s ,s1.i);

			iexpr = salida.i;
			c = expr.charAt(iexpr);
			while (c == ','){
				s1 = cte(nodo.getChildAt(0), iexpr+1);
				salida.s = salida.s + "," + s1.s;
				iexpr = s1.i;
				c = expr.charAt(iexpr);
			}
			salida.s = salida.s + " \\rangle";
			salida.i = iexpr+1;
			return salida;
		}
		return new scte("errpr",0);
	}

	public ConstantCreator(HashMap<String, String> tipos,HashMap<String, String> znames,HashMap<String,StringPointer> slvars,HashMap<String,String> notEquals) {
		this.tipos = tipos;
		this.zNames = znames;
		this.slVars = slvars;
		this.notEquals = notEquals;
		this.basicTypes = null;
		ConstantCreator.postfijo = 1;
	}

	public String getCte(String expr, DefaultMutableTreeNode root){
		this.arbol = root;
		this.expr = expr;
		return cte(arbol, 0).s;
	}
	
}