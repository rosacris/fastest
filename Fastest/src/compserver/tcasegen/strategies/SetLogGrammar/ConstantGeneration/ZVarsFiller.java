package compserver.tcasegen.strategies.SetLogGrammar.ConstantGeneration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import compserver.tcasegen.strategies.SetLogGrammar.ExprParser;
import compserver.tcasegen.strategies.SetLogGrammar.TypeManagerLexer;
import compserver.tcasegen.strategies.SetLogGrammar.TypeManagerParser;

public class ZVarsFiller {
	
	private ExprParser parser;
	private String setlogOutput;
	
	public ZVarsFiller(ExprParser parser,String setlogOutput){
		this.parser = parser;
		this.setlogOutput = setlogOutput;
	}
	
	
	private void printHashMap(HashMap map){
		Iterator<String> iterator = map.keySet().iterator();  
		String key,value;
		while (iterator.hasNext()) {  
			key = iterator.next().toString();
			if (map.get(key) == null)
				value = "nullc";
			else 
				value = map.get(key).toString();
			System.out.println(key + " = " + value);  
		} 
	}
	private void printHashMap2(HashMap<String,String[]> map){
		Iterator<String> iterator = map.keySet().iterator();  
		String key;	String[] value;
		while (iterator.hasNext()) {  
			key = iterator.next().toString();
			if (map.get(key) == null){
				System.out.println(key + " = " + "nullc");
				continue;
			}
			else{ 

				value = map.get(key);
				System.out.print(key + " = "); 
				for (int i = 0; i<value.length;i++) 
					System.out.print(value[i] + ",");
				System.out.println(); 
			}
		} 
	}

	private String getTipoLibre(String elem,HashMap<String,String> tiposLibres){
		Iterator<String> iterator = tiposLibres.keySet().iterator();  
		String key;	String value;
		while (iterator.hasNext()) { 
			key = iterator.next().toString();
			value = tiposLibres.get(key);
			if(value !=null){
				if (value.contains(elem))
					return key;
			}
		}

		return "null";
	}

	private HashMap<String,String> invertMap(HashMap<String,String> m){
		Iterator<String> iterator = m.keySet().iterator();  
		HashMap<String,String> s = new HashMap<String,String>();

		while (iterator.hasNext()) {  
			String key = iterator.next().toString();  
			String value = m.get(key).toString();  
			s.put(value,key);
		} 	
		return s;
	}

	

	private HashMap<String,String> llenarFreeTypes(HashMap<String,String> m){
		HashMap<String,String> s = new HashMap<String,String>();
		Iterator<String> iterator = m.keySet().iterator();
		String key,valor,nomtipo;
		while (iterator.hasNext()) { 
			key = iterator.next().toString();
			valor = m.get(key);
			//EnumerationType:FT:{elem1,elem2}
			if (valor.startsWith("EnumerationType")){
				String[] aux = valor.split(":");
				nomtipo =  aux[1];
				//aux = ((String) (aux[2]).subSequence(1, (aux[2]).length()-1)).split(",");
				s.put(nomtipo, ((String) (aux[2]).subSequence(1, (aux[2]).length()-1)));
			}
		}

		return s;
	}
	private HashMap<String,String> llenarZVars(ExprParser exprP, SLog2ZParser SL2ZP){
		HashMap<String, String> zVars = SL2ZP.getZVars();
		HashMap<String, String> sLogName = exprP.getMemory();
		HashMap<String,String> zNames = invertMap(exprP.getMemory());

		Iterator<String> iterator = zVars.keySet().iterator();  
		String key,valor;
		HashMap<String,String> tipos = exprP.getTypes();
		ConstantCreator cc = new ConstantCreator(tipos,zNames,null,null); 
		while (iterator.hasNext()) {  
			key = iterator.next().toString();
			valor = zVars.get(key);
			if (valor == null){

				String tipo = tipos.get(key);
				ANTLRInputStream input = new ANTLRInputStream(tipo);
				TypeManagerLexer lexer = new TypeManagerLexer(input);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				TypeManagerParser TMP = new TypeManagerParser(tokens);
				TMP.typeManage();
				DefaultMutableTreeNode root =  TMP.getRoot();

				valor =  cc.getCte(sLogName.get(key),root);
				zVars.put(key, valor);
			}  
		}
		return (HashMap<String, String>) zVars;
	}
	
	public HashMap<String, String> generar(){
		//traduccion de SLog a Z
				//setlogOutput = "CONSTR = [],\nINT = int(-10000000000, 10000000000),\nK1 = 2,\nA=X,\nX=Y,";
				ANTLRInputStream input = new ANTLRInputStream(setlogOutput);
				SLog2ZLexer lexer2 = new SLog2ZLexer(input);
				CommonTokenStream tokens = new CommonTokenStream(lexer2);
				SLog2ZParser SL2ZP = new SLog2ZParser(tokens);
				SL2ZP.loadTablas(parser);

				//tambien imprime en pantalla
				SL2ZP.lineas();
				HashMap<String, String> zVars = llenarZVars(parser,SL2ZP);
				System.out.println("\nzVars llenas****************\n");
				printHashMap((HashMap) zVars);

				System.out.println("\n FreeTipos ****************\n");
				HashMap mapaux = llenarFreeTypes(parser.getTypes());
				printHashMap(mapaux);

				String ssss = getTipoLibre("xxx1",mapaux);
				System.out.println("tipo de xxx1++++++++" + ssss);
				
				return zVars;
	}
}
