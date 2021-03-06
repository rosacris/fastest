package client.presentation.commands;

import java.io.*;
import client.presentation.ClientTextUI;
import java.util.Iterator;
import compserver.prunning.TheoremsControl;
import compserver.prunning.Theorem;
import compserver.prunning.TheoremsLoader;
import compserver.prunning.rewriting.rwrules.RWRulesControl;
import compserver.prunning.rewriting.rwrules.RWRulesLoader;
import compserver.prunning.rewriting.rwrules.RWRule;
/**
 * An instance of this class allows the load of the elimination theorems.
 */
public class LoadElimTheoremsCommand implements Command{
    
    /**
     * Runs this command.
     * @param clientTextUI
     * @param args
     */
    @Override
    public void run(ClientTextUI clientTextUI, String args){

	PrintWriter output = clientTextUI.getOutput();
	try{
		//URL url = LoadElimTheoremsCommand.class.getResource("LoadElimTheoremsCommand.class"); //MODIFICADO
		//String urlStr = url.toString(); //MODIFICADO
		//String currentDir = urlStr.substring(9,urlStr.indexOf("fastest.jar")); //MODIFICADO
		String currentDir = ""; //MODIFICADO
		String configurationFilePath = currentDir+"lib/conf/elimTheorems.tex";
		String operatorsFilePath =currentDir+"lib/conf/thmoperators.conf";
		String rwRulesFilePath = currentDir+"lib/conf/rwRules.tex";




		TheoremsControl theoremsControl = TheoremsControl.getInstance();
		Iterator<Theorem> theoremsIt = theoremsControl.iterator();
		while(theoremsIt.hasNext()){
			Theorem auxTheorem = theoremsIt.next();
			theoremsIt.remove();
		}

		RWRulesControl rwRulesControl = RWRulesControl.getInstance();
		Iterator<RWRule> itRules = rwRulesControl.iterator();
		while(itRules.hasNext()){
			RWRule rwRule = itRules.next();
			itRules.remove();
		}

		(new RWRulesLoader(rwRulesFilePath)).loadRWRules();
		(new TheoremsLoader(configurationFilePath,operatorsFilePath)).loadTheorems();
	}
	catch(Exception e){
		e.printStackTrace(System.out);
		output.println("Error while trying to load the configuration files");
	}

    }
}