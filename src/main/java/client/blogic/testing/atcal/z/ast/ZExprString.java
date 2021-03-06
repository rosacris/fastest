package client.blogic.testing.atcal.z.ast;

/**
 * Created by cristian on 4/7/15.
 */
public class ZExprString implements ZExpr {
    private final String str;

    public ZExprString(String str) {
        this.str = str;
    }


    public static ZExprString fromZExpr(ZExpr zExpr) {
        if (zExpr instanceof ZExprString) {
            return (ZExprString)zExpr;
        }if (zExpr instanceof ZExprConst) {
            return new ZExprString(((ZExprConst)zExpr).getValue());
        } else if (zExpr instanceof ZExprNum) {
            return new ZExprString(Long.toString(((ZExprNum) zExpr).getNum()));
        } else {
            throw new RuntimeException("Cannot convert Z expression to string.");
        }
    }

    public String getStr() {
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZExprString that = (ZExprString) o;

        if (!str.equals(that.str)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }

    @Override
    public String toString() {
        return "\'" + str + '\'';
    }
}
