package client.blogic.testing.refinamiento;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

import client.blogic.testing.refinamiento.java.FTCRLJavaVisitor;

public final class RefinementTable {
	
	public String stmt;
	public String t = "";
	public String c = "";
	public int size = 0;
	public LinkedList<String> columnName, columnType, columnSize; //Almacenan los datos de la tabla
	public String values[]; //Almacena los datos que se insertarán al finalizar el refinamiento del WITH
	                                 //Despues debe vaciarse y volver a empezar
	
	public RefinementTable(String t, String c, String p, String f, FTCRLJavaVisitor ftcrl) {
		this.t = t;
		this.c = c;
		columnName = new LinkedList<String>();
		columnType = new LinkedList<String>();
		columnSize = new LinkedList<String>();
		columnSize = new LinkedList<String>();
		stmt = ftcrl.newVarName("stmt");
		
		ftcrl.printDeclaration("Statement " + stmt + " = init." + c + ".createStatement()");
		ftcrl.printDeclaration(stmt + ".executeUpdate(\"delete " + t + "\")");
		ftcrl.openedTables.add(this);
		
		try {
			String path = p;
			if (path.equals(""))
				path = path.concat(f);
			else
				path = path.concat("/"+f);
			Scanner in = new Scanner(new FileReader(path));
			parseColums(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void parseColums(Scanner s){
		size = 0;
		while(s.hasNext()){
			String line = s.next();
			String[] args = line.split(":",3);
			columnName.add(args[0]);
			columnType.add(args[1]);
			columnSize.add(args[2]);
			size++;
		}
		values = new String[size];
	}
	
	public void saveValues(String value, String column){
		int pos = columnName.indexOf(column);
		this.values[pos] = value;
	}
	
	public String printValues(){
		String v = "";
		for (int i = 0; i < size; i++){
			if (i != 0)
				v = v.concat("," + values[i]);
			else
				v = v.concat(values[i]);
		}
		return v;
	}
	
	public void resetValues(){
		for (int i = 0; i < size; i++){
			values[i] = "";
		}
	}
	
	public String getColumnType(String column) {
		int pos = columnName.indexOf(column);
		if (pos >= 0){
			return columnType.get(pos);
		}
		return null;
	}
}
