package nlg.designation;

import nlg.expr.base.ExprDescPlan;

public class SimpleDesignation extends TermDesignation {
	private String text;
	
	public SimpleDesignation(ExprDescPlan expr, String schName, String text) {
		this.expr = expr;
		this.schName = schName;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}