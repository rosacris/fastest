package client.blogic.testing.atcal;

import client.blogic.testing.atcal.z.ast.CZTTranslator;
import client.blogic.testing.atcal.z.ast.ZExpr;
import client.blogic.testing.atcal.z.ast.ZExprSchema;
import client.blogic.testing.atcal.z.ast.ZVar;
import com.google.common.io.Resources;
import common.z.AbstractTCase;
import common.z.SpecUtils;
import common.z.czt.UniqueZLive;
import net.sourceforge.czt.animation.eval.ZLive;
import net.sourceforge.czt.base.util.UnmarshalException;
import net.sourceforge.czt.parser.circus.ParseUtils;
import net.sourceforge.czt.parser.util.ParseException;
import net.sourceforge.czt.session.FileSource;
import net.sourceforge.czt.z.ast.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cristian on 3/31/15.
 */
public class Atcal {

    /**
     * Helper function to translate the abstract test case from CZT to ATCAL representation.
     *
     * @param atc a CZT abstract test case
     * @return an ATCAL abstract test case
     */
    public static ZExprSchema ATCToZExpr(AbstractTCase atc) {
        final ArrayList<ZVar> translatedVars = new ArrayList<ZVar>();

        // Instantiate the translator for each variable and translate the expressions
        for (Map.Entry<RefExpr, Expr> varExpr : atc.getVarExpMap().entrySet()) {
            final String zVarName = varExpr.getKey().getName().toString();
            final ZExpr expr = varExpr.getValue().accept(new CZTTranslator(zVarName));
            translatedVars.add(new ZVar(zVarName, expr));
        }
        return new ZExprSchema(translatedVars.toArray(new ZVar[translatedVars.size()]));
    }
}
