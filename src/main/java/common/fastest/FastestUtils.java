package common.fastest;

import java.util.*;
import java.io.*;
import net.sourceforge.czt.z.ast.AxPara;
import net.sourceforge.czt.z.ast.Expr;
import net.sourceforge.czt.z.ast.RefExpr;
import client.blogic.management.Controller;
import java.util.Collection;
import java.util.Iterator;
import client.blogic.testing.ttree.*;
import client.blogic.testing.ttree.visitors.TTreeNodeFinder;
import client.blogic.testing.ttree.tactics.Tactic;
import common.z.SpecUtils;

/**
 * Provides general utilities related to Fastest objects.
 */
public final class FastestUtils {

	public static String allNonBasicAxDefReplaced(AxPara axPara, Controller controller){
		//We check to see if all the non basic axDefs in the TClass has been replaced,
		//if not, the case cannot be generated
		String strPred = SpecUtils.termToLatex(SpecUtils.getAxParaPred(axPara));
		String strDecl = SpecUtils.termToLatex(SpecUtils.getAxParaListOfDecl(axPara));

		Set<String> axDefsRequired = controller.getAxDefsRequired().keySet();
		Iterator<String> axDefsRequiredIt = axDefsRequired.iterator();

		//We save first, all the already assign values
		Set<String> alreadyAssigned = new HashSet<String>();
		Map<RefExpr, Expr> axDefsAssignedValues = controller.getAxDefsValues();
		Iterator<RefExpr> it = axDefsAssignedValues.keySet().iterator();
		while (it.hasNext())
			alreadyAssigned.add(SpecUtils.termToLatex(it.next()));

		boolean containsAxDef = false;
		String axDef = null;
		while (axDefsRequiredIt.hasNext() && (!containsAxDef)) {
			axDef = axDefsRequiredIt.next();
			if (!alreadyAssigned.contains(axDef)) {
				if(strDecl.split("(^|\\W)"+axDef+"($|\\W)", 2).length == 1){

					if (strPred.split("(^|\\W)"+axDef+"($|\\W)", 2).length > 1) {
						containsAxDef = true;
						break;
					}
				}
			}
		}
		//Set<RefExpr> axDefsValues = controller.getAxDefsValues().keySet();
		//Iterator<RefExpr> axDefsValuesIt = axDefsValues.iterator();
		//while (axDefsValuesIt.hasNext() && (!containsAxDef)) {
		//	axDef = axDefsValuesIt.next().getZName().getWord();
		//	if(strDecl.split("(^|\\W)"+axDef+"($|\\W)", 2).length == 1){
		//		if (strPred.split("(^|\\W)"+axDef+"($|\\W)", 2).length > 1) {
		//			containsAxDef = true;
		//			break;
		//		}
		//	}
		//}

		if (containsAxDef)
			return axDef;

		return null;

	}


	/**
	 * Checks if a string is contained in the repository of loaded operations
	 * @param controller a reference to the Controller
	 * @param opName the string to search in the repository
	 * @return
	 */
	public static boolean isLoadedOperation(Controller controller, String opName) {
		Collection<String> loadedOpsRep = controller.getLoadedOpsRep();
		Iterator<String> it = loadedOpsRep.iterator();
		boolean hasFound = false;
		while (it.hasNext() && !hasFound) {
			if (it.next().equals(opName)) {
				hasFound = true;
			}
		}
		return hasFound;
	}


	/**
	 * Checks if a string is contained in the repository of schemes selected as
	 * predicates
	 * @param controller a reference to the Controller
	 * @param schemeName the string to search in the repository
	 * @return
	 */
	public static boolean isSelectedPred(Controller controller, String schemeName) {
		Collection<String> schemePredsRep = controller.getSchemaPredicatesRep();
		Iterator<String> schemePredsIt = schemePredsRep.iterator();
		boolean hasFound = false;
		while (schemePredsIt.hasNext() && !hasFound) {
			if (schemePredsIt.next().equals(schemeName)) {
				hasFound = true;
			}
		}
		return hasFound;
	}


	/**
	 * Checks if a string is contained in the repository of selected operations
	 * @param controller a reference to the Controller
	 * @param opName the string to search in the repository
	 * @return
	 */
	public static boolean isSelectedOperation(Controller controller, String opName) {
		Collection<String> selectedOpsRep = controller.getOpsToTestRep();
		Iterator<String> it = selectedOpsRep.iterator();
		boolean hasFound = false;
		while (it.hasNext() && !hasFound) {
			if (it.next().equals(opName)) {
				hasFound = true;
			}
		}
		return hasFound;
	}

	/**
	 * Checks if a string is the name of a test class in a testing tree and returns the 
	 * name of the operation that generates this testing tree or null
	 * @param controller a reference to the Controller
	 * @param tClassName the string to search in the testing trees
	 * @return the name of the operation
	 */
	public static String getOpAssociated(Controller controller, String tClassName) {
		Map<String, TClassNode> opTTreeMap = controller.getOpTTreeMap();
		Set<Map.Entry<String, TClassNode>> set = opTTreeMap.entrySet();
		Iterator<Map.Entry<String, TClassNode>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, TClassNode> mapEntry = iterator.next();
			String opTTreeName = mapEntry.getKey();
			TClassNode tTree = mapEntry.getValue();
			TTreeNode tTreeNode = tTree.acceptVisitor(new TTreeNodeFinder(tClassName));
			if (tTreeNode != null) {
				return opTTreeName;
			}
		}
		return null;
	}

	/**
	 * Returns the TTreeNode corresponding to a node name or returns null if the string
	 * that is passed as argument is not the name of a test class or a test case
	 * @param controller a reference to the Controller
	 * @param tNodeName the string to search in the testing trees
	 * @return the node in the testing tree
	 */
	public static TTreeNode getTTreeNode(Controller controller, String tNodeName) {
		Map<String, TClassNode> opTTreeMap = controller.getOpTTreeMap();
		Set<Map.Entry<String, TClassNode>> set = opTTreeMap.entrySet();
		Iterator<Map.Entry<String, TClassNode>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, TClassNode> mapEntry = iterator.next();
			//String opTTreeName = mapEntry.getKey();
			TClassNode tTree = mapEntry.getValue();
			TTreeNode tTreeNode = tTree.acceptVisitor(new TTreeNodeFinder(tNodeName));
			if (tTreeNode != null) {
				return tTreeNode;
			}
		}
		return null;
	}

	/**
	 * Clear in the list of tactics the maps used to assign names to the test classes
	 */
	public static void resetTacticsNumbersMap() {
		try {
			List<String> tacticNameList = new ArrayList<String>();

			//            URL url = FastestUtils.class.getResource("FastestUtils.class");  //MODIFICADO
			//            String urlStr = url.toString();  //MODIFICADO 
			//            String currentDir = urlStr.substring(9, urlStr.indexOf("fastest.jar")); //MODIFICADO
			String currentDir = ""; //MODIFICADO
			File file = new File(currentDir + "lib/conf/listoftactics.conf");
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null) {
				tacticNameList.add(line);
			}

			for (int i = 0; i < tacticNameList.size(); i++) {
				String tacticName = tacticNameList.get(i);
				Class tacticClass = Class.forName("client.blogic.testing.ttree.tactics." + tacticName + "Tactic");
				Object object = tacticClass.newInstance();
				if (object instanceof Tactic) {
					Tactic tactic = (Tactic) object;
					tactic.resetNumbersMap();
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * Returns an AbstractTCase stored in the controller whose name is the string
//	 * that is passed as argument, if it exists, or null otherwise
//	 * @param controller A reference to the controller
//	 * @param tCaseName the string to search
//	 */
//	public static AbstractTCase getAbstractTCase(Controller controller, String tCaseName) {
//		// We look for the abstract case whose name is tCaseName
//		AbstractTCase abstractTCase = null;
//		Map<String, TClassNode> opTTreeMap = controller.getOpTTreeMap();
//		Set<Map.Entry<String, TClassNode>> set = opTTreeMap.entrySet();
//		Iterator<Map.Entry<String, TClassNode>> iterator = set.iterator();
//		boolean tcaFounded = false;
//		while (iterator.hasNext() && !tcaFounded) {
//			Map.Entry<String, TClassNode> mapEntry = iterator.next();
//			String opName = mapEntry.getKey();
//			TClassNode opTTreeRoot = mapEntry.getValue();
//			Map<String, AbstractTCase> tcaMap = opTTreeRoot.acceptVisitor(new TCaseNodeFinder());
//			Set<String> keySet = tcaMap.keySet();
//			if (keySet.contains(tCaseName)) {
//				tcaFounded = true;
//				abstractTCase = tcaMap.get(tCaseName);
//			}
//		}
//		return abstractTCase;
//	}

	

	/**
	 * Returns the name of the operation related to a class name. This method exploits the particular way of
	 * naming classes used in Fastest. If this way of generates names changes, this method become obsolete.
	 * @param tClassName The name of the class
	 * @return The name of the operation
	 */
	public static String getOperationName(String tClassName) {
		String opName = tClassName.replaceAll("[_][^_]+[_]\\d+\\z", "");
		// We consider the case that involved the VIS
		if (opName.equals(tClassName)) {
			opName = tClassName.replaceAll("[_]VIS\\z", "");
		}
		return opName;
	}
}
