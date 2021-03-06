package nlg.base.expression;


/**
 * Mapping
 * ExprMapsTo x y -> x ↦ y
 */
public class ExprMapsTo implements ExprZ {
	private ExprZ left;
	private ExprZ right;
	
	public ExprMapsTo(){
		
	}
	
	public ExprMapsTo(ExprZ left, ExprZ right) {
		this.left = left;
		this.right = right;
	}
	
	public ExprZ getLeft() {
		return left;
	}
	public void setLeft(ExprZ left) {
		this.left = left;
	}
	public ExprZ getRight() {
		return right;
	}
	public void setRight(ExprZ right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExprMapsTo other = (ExprMapsTo) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
	@Override
	public <X> X accept(ExprZVisitor<X> visitor) {
		return visitor.visitExprMapsTo(this);
	}
}
