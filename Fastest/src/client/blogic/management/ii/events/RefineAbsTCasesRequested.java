package client.blogic.management.ii.events;

import java.util.Collection;
import common.z.AbstractTCase;

/**
 * Represents the implicit invocation event that must be announced when
 * a set of abstract test cases need to be refined.
 * @author Hache
 */
public class RefineAbsTCasesRequested extends Event_{

	private Collection<AbstractTCase> absTCasesColl;
	private String opName;
	private String targetLanguaje;
	private String pathUUT;
	private String refRuleName;
	/**
	 * Creates instances of RefineAbsTCasesRequested.
	 */
	public RefineAbsTCasesRequested(String opName, Collection<AbstractTCase> set, String pathUUT, String targetLanguaje, String refRuleName){
		this.absTCasesColl = set;
		this.opName = opName;
		this.targetLanguaje = targetLanguaje;
		this.pathUUT = pathUUT;
		this.refRuleName = refRuleName;
		super.setEventName("refineAbsTCasesRequested");
	}

	public String getOpName(){
		return opName;
	}

	public String getTargetLanguaje(){
		return targetLanguaje;
	}

	public String getPathUUT(){
		return pathUUT;
	}

	public String getRefRuleName(){
		return refRuleName;
	}

	public Collection<AbstractTCase> getAbsTCasesColl(){
		return absTCasesColl;
	}

}
