package client.blogic.testing.ttree.strategies;

import java.util.*;
import java.io.*;

import jline.ConsoleReader;

import client.blogic.management.Controller;
import client.presentation.ClientTextUI;
import client.presentation.commands.AddTacticCommand;
import client.presentation.commands.Command;
import client.presentation.commands.GenAllTTCommand;
import client.presentation.commands.PruneTreeCommand;

import net.sourceforge.czt.base.ast.Term;
import net.sourceforge.czt.z.ast.AxPara;
import net.sourceforge.czt.z.ast.ParaList;
import net.sourceforge.czt.z.ast.Sect;
import net.sourceforge.czt.z.ast.Spec;
import net.sourceforge.czt.z.ast.ZParaList;
import net.sourceforge.czt.z.ast.ZSect;
import net.sourceforge.czt.util.Visitor;

import common.z.SpecUtils;
import java.util.Collection;
import common.z.czt.visitors.AtomicPredExtractor;
import common.z.czt.visitors.SchemeUnfolder;


/**
 * Implementation of the strategy Standard Partitions (SP).
 */
public class SPStrategy{

	protected ClientTextUI clientTextUI;
	protected String unitToTestName;
	protected String option;
	protected String strategyName;

	/**
	 * Creates instances of SPStrategy.
	 */
	public SPStrategy() {
		this.strategyName = "SP";
	}

	/**
	 * Applies this strategy to the specified test class and returns the list with
	 * the generated test classes.
	 */
	public void applyStrategy(ClientTextUI clientTextUI, String unitToTestName, String option){

		this.clientTextUI = clientTextUI;
		this.unitToTestName = unitToTestName;
		this.option = option;

		Controller controller = clientTextUI.getMyController();
		//BufferedReader input = clientTextUI.getInput(); //Asi no imprime los chars cuando lee
		//Asi si
		ConsoleReader input = null;
		try {
			input = new ConsoleReader();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		input.setInput(new BufferedInputStream(System.in));

		PrintWriter output = clientTextUI.getOutput();

		//Buscamos las expresiones que se encuentran en la operacion unfoldeada,
		// y las almacenamos en expressions
		Spec spec = controller.getUnfoldedSpec();
		Collection<String> opNames = controller.getOpsToTestRep();
		Collection<String> schPredNames = controller.getSchemaPredicatesRep();
		spec = (Spec) spec.accept(new SchemeUnfolder(opNames,schPredNames));

		AxPara axPara = null;
		for (Sect sect : spec.getSect()) {
			if (sect instanceof ZSect) {
				ParaList paraList = ((ZSect) sect).getParaList();
				if (paraList instanceof ZParaList) {
					axPara = SpecUtils.axParaSearch(unitToTestName, (ZParaList) paraList);
				}
			}
		}

		Visitor<Map<Term, String>> searcher = new AtomicPredExtractor();
		Map<Term, String> expressions;
		expressions = axPara.accept(searcher);
		Iterator<Term> expressionsIt = expressions.keySet().iterator();

		Command addtactic = new AddTacticCommand();
		Command genalltt = new GenAllTTCommand();
		Command prunett = new PruneTreeCommand();

		boolean aplied = false;

		//Structura para evitar aplicaciones repetidas
		ArrayList<String> usedExprs = new ArrayList<String>();

		while (expressionsIt.hasNext()) {
			Term exp = expressionsIt.next();
			String expString = SpecUtils.termToLatex(exp);
			aplied = false;

			//Chequeamos si la expresion no fue utilizada, para evitar aplicaciones repetidas
			if (!usedExprs.contains(SpecUtils.termToLatex(exp))) {
				usedExprs.add(expString);

				if (option.equals("Full")) { //Si se desea aplicar de forma completa, no se pregunta para cada expr
					output.println("Applying: " + unitToTestName + " " + strategyName + " " + expressions.get(exp) + " " + expString);
					addtactic.run(clientTextUI, unitToTestName + " SP " + expressions.get(exp) + " " + expString);
					aplied = true;
				} else {
					output.println("Apply tactic: " + unitToTestName + " " + strategyName + " " + expressions.get(exp) + " " + expString + "? y/n" );
					try {
						option = input.readLine();
						if (option.equalsIgnoreCase("y") || option.equalsIgnoreCase("yes")){
							output.println("Applying: " + unitToTestName + " " + strategyName + " " + expressions.get(exp) + " " + expString);
							addtactic.run(clientTextUI, unitToTestName + " SP " + expressions.get(exp) + " " + expString);
							aplied = true;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (aplied) {
					genalltt.run(clientTextUI, "");
					//pruneamos
					prunett.run(clientTextUI, "");
				}
			}
		}
	}
}