package compserver.tcasegen.strategies.setlog.setlogtoz;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;



public class IntExprMap {
	private HashMap<String, String> tipos;
	
	private  class Par{
		int x,y;
		public String toString(){
			return "(" + x + "," + y + ")";
		}
	}	
	
	private  Par parFromInt(int card2, int num){
		Par p = new Par();
		if(card2>0){
			p.y = num % card2;
			p.x = num / card2 + ((p.y >0)?1:0);
			p.y = ((p.y ==0)?card2:p.y);
			return p;
		}
		p.x = 1; 
		p.y = num;
		return p;
	}
	private int intFromPar(int x, int y, int card){
		return card*(x-1) + y;
	}
	
	private  String elemFromFreeType(String tipo, int i){
		String s;
		String aux[] = tipo.split(":");
		s = aux[2].substring(1, aux[2].length()-1);
		aux = s.split(",");
		return aux[i-1];
	}
	//devuelve el bit que corresponde a la posicion, ej bitFromFreeType(FT::=a|b|c , a)
	// = 100 en binario = 4 en decimal
	private  int numFromFreeType(String tipo,String elem){
		String s;
		String aux[] = tipo.split(":");
		s = aux[2].substring(1, aux[2].length()-1);
		aux = s.split(",");
		for(int i=0;i<aux.length;i++){
			if (aux[i].equals(elem))
				return i+1;
		}
		return 0;
	}
	private  int cardFromFreeType(String tipo){
		String aux[] = tipo.split(":");
		aux = aux[2].split(",");
		return aux.length;
	}
	
	//devuelve la posicion de los bits encendidos de la representacion binaria del entero,
	private  int[] posEncendidas(int n){
		int[] palabra = new int[Integer.SIZE];
		int res,resto,i,ni;
		res = n>>1;
		resto = n&1;
		i = 0;ni=1;
		while(res > 0 ){
			if(resto == 1){
				palabra[i] = ni;
				i++;
			}
			resto = res&1;
			res = res>>1;ni++;
		}
		palabra[i++] = ni*resto;
		return palabra;
	}

	public int cardinalidad(DefaultMutableTreeNode nodo){
		String ct = nodo.toString();
		int c=0;
		
		if (ct.equals("\\power")){
			c = cardinalidad((DefaultMutableTreeNode) nodo.getChildAt(0));
			c = (2<<(c-1));
		}
		else if (ct.equals("()")){
			c = cardinalidad((DefaultMutableTreeNode) nodo.getChildAt(0));
		}
		else if(ct.equals("\\cross")){
			c = cardinalidad((DefaultMutableTreeNode) nodo.getChildAt(0));
			c = c * cardinalidad((DefaultMutableTreeNode) nodo.getChildAt(1));
		}
		else
			c = cardFromFreeType(tipos.get(ct));
		return c;
	}
	
	//devuelve el tipo del proximo hijo que no es "()"
	private String tipoProximoHijo(TreeNode nodo){
		while(nodo.toString().equals("()"))
			nodo = nodo.getChildAt(0);
		return nodo.toString();
	}
	/* Dado un numero natural y un tipo devuelve una expresion terminal del tipo */
	private  String f(DefaultMutableTreeNode nodo, int num){
		String salida = null;
		String ct = nodo.toString();
		DefaultMutableTreeNode hijoIzq = null;
		if(!nodo.isLeaf())
			hijoIzq = (DefaultMutableTreeNode) nodo.getChildAt(0);
		
		if(ct.equals("\\pfun")||ct.equals("\\fun")||ct.equals("\\rel")){
			//se hace de esta manera por que add saca el nodo del arbol original
			DefaultMutableTreeNode cross = new DefaultMutableTreeNode("\\cross");
			cross.add((MutableTreeNode) nodo.getChildAt(0));
			cross.add((MutableTreeNode) nodo.getChildAt(0));
			nodo.setUserObject("\\power");
			nodo.add(cross);
			salida = f(nodo,num);
		}else if ( ct.equals("\\power") || ct.equals("\\seq")){
			String p1,p2;
			p1 = ct.charAt(1) == 'p'?"{":"[";
			p2 = ct.charAt(1) == 'p'?"}":"]";
			String ctHijo;
			int n[]=posEncendidas(num);
			int numi,i;
			i=0;
			numi= n[i];
			salida = p1;
			int aux;
			while(numi!=0){
				ctHijo = tipoProximoHijo(hijoIzq);
				//numi - 1 es el numero decimal que representa la constante
				//el cual es la posicion del bit encendido - 1
				aux = (ctHijo.equals("\\power")||ctHijo.equals("\\seq"))? numi-1 : numi;
				salida = salida  + f(hijoIzq,aux) + ",";
				i++;
				numi = n[i];
			}
			if(salida.charAt(salida.length()-1)==',')
				salida = salida.substring(0, salida.length()-1) + p2;
			else
				salida = salida + p2;
		}else if(ct.equals("()")){
			salida = f(hijoIzq,num);
		}else if(ct.equals("\\cross")){
			DefaultMutableTreeNode hijoDer = (DefaultMutableTreeNode) nodo.getChildAt(1);
			//Tipo t2 = (Tipo) ((DefaultMutableTreeNode) nodo.getChildAt(1)).getUserObject();
			int c = cardinalidad(hijoDer);
			//int c2 =t2.cardinalidad;
			if (num ==0)
				salida = "[]";
			else{
				Par p = parFromInt(c,num);
				
				String ctaux = tipoProximoHijo(hijoIzq);
				p.x = (ctaux.equals("\\power")||ctaux.equals("\\seq"))? p.x-1 : p.x;
				ctaux = tipoProximoHijo(hijoDer);
				p.y = (ctaux.equals("\\power")||ctaux.equals("\\seq"))? p.y-1 : p.y;
				
				salida = "["+f(hijoIzq,p.x) + "," + f(hijoDer,p.y) +"]";
			}
		}else /*si es tipo libre*/{
			String tipo = tipos.get(ct);
			if (num<1){
				salida ="{}";
			}
			salida = elemFromFreeType(tipo,num);	
		}
		return salida;
	}
	/* Dado un tipo y una expresion terminal, devuelve un numero natural
	 * Ej.{a,b,c}  
	 * 	   0 0 0  | 0 1 0 1 ... 1   
	 *     0 0 1  | 0 0 1 1 ... 1
	 *     0 1 0  | 0 0 0 0 ... 1
	 *     0 1 1  | 0 0 0 0 ... 1 
	 *     1 0 0  | 0 0 0 0 ... 1
	 *     1 0 1  | 0 0 0 0 ... 1
	 *     1 1 0  | 0 0 0 0 ... 1
	 *     1 1 1  | 0 0 0 0 ....1 
	 *     los valores del lado izquiero del "|" son todos los valores posibles para una variable del tipo {a,b,c}
	 *     los del lado derecho para \power {a,b,c} y asi */
	private  int f(DefaultMutableTreeNode nodo, String expr){
		int salida = -1;
		String ct = nodo.toString();
		DefaultMutableTreeNode hijoIzq = null;
		if(!nodo.isLeaf())
			hijoIzq = (DefaultMutableTreeNode) nodo.getChildAt(0);
		
		if(ct.equals("\\pfun")||ct.equals("\\fun")||ct.equals("\\rel")){
			//se hace de esta manera por que add saca el nodo del arbol original
			DefaultMutableTreeNode cross = new DefaultMutableTreeNode("\\cross");
			cross.add((MutableTreeNode) nodo.getChildAt(0));
			cross.add((MutableTreeNode) nodo.getChildAt(0));
			nodo.setUserObject("\\power");
			nodo.add(cross);
			salida = f(nodo,expr);
		}else if(ct.equals("\\power")||ct.equals("\\seq")){
			String ctHijo;
			ExprIterator elems = new ExprIterator(expr);
			String elem;
			int posbit;
			int ac = 0;
			int aux;
			while(elems.hasNext()){
				elem = elems.next();
				posbit = f((DefaultMutableTreeNode)nodo.getChildAt(0),elem);
				//crea el numero a partir de las posiciones de los bit encendidos los cuales
				//pertenecen a cada elemento del conjunto
				ctHijo = tipoProximoHijo(hijoIzq);
				aux = (ctHijo.equals("\\power")||ctHijo.equals("\\seq"))? posbit : posbit-1;
				ac |= 1<<aux; //acumula bits, cad auno corresponde a un elemento presente.
			}
			salida = ac;
		}else if(ct.equals("()")){
			return f(hijoIzq,expr);
		}else if(ct.equals("\\cross")){
			DefaultMutableTreeNode hijoDer = (DefaultMutableTreeNode) nodo.getChildAt(1);
			ExprIterator elems = new ExprIterator(expr);
			String s1 = elems.next();
			String s2 = elems.next();
			
			int f1 = f(hijoIzq,s1);
			int f2 = f(hijoDer,s2);
			
			String ctaux = tipoProximoHijo(hijoIzq);
			f1 = (ctaux.equals("\\power")||ctaux.equals("\\seq"))? f1+1 : f1;
			ctaux = tipoProximoHijo(hijoDer);
			f2 = (ctaux.equals("\\power")||ctaux.equals("\\seq"))? f2+1 : f2;
			
			return intFromPar( f1 , f2, cardinalidad(hijoDer));
		}
		else{
			salida = numFromFreeType(tipos.get(ct),expr);
		}
		return salida;
	}
	
	//Devuelve una expresion termino cte del tipo
	//modifica el nodo, lo normaliza ej: si es A \pfun B -> \power (A \cross B)
	public String toExpr(DefaultMutableTreeNode nodo,int num){
		String salida =  f(nodo,num);
		
		return salida;
	}
	
	//Devuelve un numero natural que corresponde a un valor terminal del tipo 
	//modifica el nodo, lo normaliza ej: si es A \pfun B -> \power (A \cross B)
	public int toNum(DefaultMutableTreeNode nodo,String expr){
		expr = expr.replaceAll("\\s+",""); 
		return f(nodo,expr);
	}
	
	//es una biyeccion entre naturales y expresiones terminales ctes dado un tipo.
	public IntExprMap(HashMap<String,String> tipos){
		this.tipos = tipos;
	}
}