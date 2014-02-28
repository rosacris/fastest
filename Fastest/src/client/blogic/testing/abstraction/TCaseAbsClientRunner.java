package client.blogic.testing.abstraction;

import java.util.*;
import java.io.*;
import client.blogic.testing.refinamiento.ConcreteTCase;
import compserver.abstraction.AbstractionLaw;
import compserver.abstraction.capture.XMLParser;
import compserver.abstraction.capture.CapturedVar;
import common.z.AbstractTCase;
import net.sourceforge.czt.z.ast.AxPara;
import compserver.abstraction.tcasegeneration.*;

import client.blogic.management.ii.events.TCaseAbstracted;
import client.blogic.management.ii.EventAdmin;

/**
 * Instances of this class has the responsibility of request, to a computation 
 * server, for the addition of the necessary code to capture the output of the
 * monitored variables
 */
public class TCaseAbsClientRunner implements Runnable
{

    /**
     * Creates new instances of TCaseAbsClientRunner
     * @param tClass
     */
	public TCaseAbsClientRunner(ConcreteTCase cTCase, AbstractionLaw absLaw, AxPara originalAxPara){
		this.cTCase = cTCase;
		this.absLaw = absLaw;
		this.originalAxPara = originalAxPara;
	}

    /**
     * Requests the abstraction of a test case either to the client 
     * itself or to a computation server server. After a response arrives, it
     * announces a ScriptModified event.
     */
	public void run(){

	try{
	// We parse the XML file generated by the concrete case
	//URL url = TCaseAbsClientRunner.class.getResource("TCaseAbsClientRunner.class"); //MODIFICADO
	//String urlStr = url.toString(); //MODIFICADO
	//String currentDir = urlStr.substring(9,urlStr.indexOf("fastest.jar")); //MODIFICADO
	String currentDir = ""; //MODIFICADO
	String scriptName = cTCase.getConcreteTCaseName();
	String fileName = "xmlCapture"+scriptName;

	XMLParser parser = new XMLParser(currentDir+fileName+".xml");
	List<CapturedVar> vars = parser.parse();

	// We remove the XML File
	File fileToRemove = new File(currentDir+fileName+".xml");
	boolean removed = fileToRemove.delete();

	// Finally, we try to obtain the abstractedCase
	ASTAbstraction traductor = new ASTAbstraction(absLaw,vars);
	traductor.setOriginalAxPara(originalAxPara);
	AbstractTCase tCase = traductor.abstractConcreteTCase(cTCase);

	AbstractTCase originalCase = cTCase.getAbstractTCase();
	String brotherName = originalCase.getSchName();
	EventAdmin eventAdmin = EventAdmin.getInstance();
	String opName = cTCase.getOpName();
	TCaseAbstracted event = new TCaseAbstracted(tCase,brotherName,opName);
	eventAdmin.announceEvent(event);

	}
	catch(Exception e){
		e.printStackTrace(System.out);
	}

	}
	private ConcreteTCase cTCase;
	private AbstractionLaw absLaw;
	private AxPara originalAxPara;
}
