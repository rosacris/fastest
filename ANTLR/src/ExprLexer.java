// Generated from Expr.g4 by ANTLR 4.0

	import java.util.HashMap;
	import java.util.ArrayList;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__40=1, T__39=2, T__38=3, T__37=4, T__36=5, T__35=6, T__34=7, T__33=8, 
		T__32=9, T__31=10, T__30=11, T__29=12, T__28=13, T__27=14, T__26=15, T__25=16, 
		T__24=17, T__23=18, T__22=19, T__21=20, T__20=21, T__19=22, T__18=23, 
		T__17=24, T__16=25, T__15=26, T__14=27, T__13=28, T__12=29, T__11=30, 
		T__10=31, T__9=32, T__8=33, T__7=34, T__6=35, T__5=36, T__4=37, T__3=38, 
		T__2=39, T__1=40, T__0=41, NAME=42, NUM=43, IN_FUN_P3=44, IN_FUN_P4=45, 
		PRE_GEN=46, NL=47, WS=48, SETSTART=49, SETEND=50, SKIP=51;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'\\lnot'", "','", "'\\rel'", "'('", "':'", "'<'", "'\\pfun'", "'false'", 
		"'\\lor'", "'\\dom'", "'\\end{schema}'", "'\\end{zed}'", "'\\upto'", "'\\in'", 
		"'}'", "'\\begin{schema}{'", "'\\begin{axdef}'", "'true'", "'\\notin'", 
		"'\\subset'", "'\\power'", "')'", "'\\iff'", "'\\land'", "'\\end{axdef}'", 
		"'@'", "'\\implies'", "'\\fun'", "'='", "'\\leq'", "';'", "'\\neq'", "'\\nat'", 
		"'>'", "'\\geq'", "'\\where'", "'\\begin{zed}'", "'\\num'", "'~'", "'|'", 
		"'\\mapsto'", "NAME", "NUM", "IN_FUN_P3", "IN_FUN_P4", "PRE_GEN", "NL", 
		"WS", "'\\{'", "'\\}'", "SKIP"
	};
	public static final String[] ruleNames = {
		"T__40", "T__39", "T__38", "T__37", "T__36", "T__35", "T__34", "T__33", 
		"T__32", "T__31", "T__30", "T__29", "T__28", "T__27", "T__26", "T__25", 
		"T__24", "T__23", "T__22", "T__21", "T__20", "T__19", "T__18", "T__17", 
		"T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", "T__9", 
		"T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", 
		"NAME", "NUM", "IN_FUN_P3", "IN_FUN_P4", "PRE_GEN", "NL", "WS", "SETSTART", 
		"SETEND", "SKIP"
	};


		String type;
		int varNumber = 0;
		HashMap memory = new HashMap();
		int modoSetExpression = 0; //0 = false, 1 = true
		String setExpressionDecl, setExpressionPred, setExpressionExpr;
		ArrayList setExpressionVars;

		public void print(String c) {
			if (modoSetExpression == 0) 
				System.out.println(c + " & ");
			else if (modoSetExpression == 1)
				setExpressionDecl = setExpressionDecl.concat(" & " + c);
			else if (modoSetExpression == 2)
				setExpressionPred = setExpressionPred.concat(" & " + c);
			else if (modoSetExpression == 3)
				setExpressionExpr = setExpressionExpr.concat(" & " + c);
		}


	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 47: WS_action((RuleContext)_localctx, actionIndex); break;

		case 50: SKIP_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip(); break;
		}
	}
	private void SKIP_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip(); break;
		}
	}

	public static final String _serializedATN =
		"\2\4\65\u01a1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36"+
		"\t\36\4\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4"+
		"(\t(\4)\t)\4*\t*\4+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62"+
		"\t\62\4\63\t\63\4\64\t\64\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3"+
		"\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3"+
		"\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3\""+
		"\3\"\3#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3&\3"+
		"&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3*\3*\3*\3*\3*\3*"+
		"\3+\3+\3+\3+\3+\6+\u015b\n+\r+\16+\u015c\3+\7+\u0160\n+\f+\16+\u0163\13"+
		"+\3,\6,\u0166\n,\r,\16,\u0167\3-\3-\3-\3-\3-\5-\u016f\n-\3.\3.\3.\3.\3"+
		".\3.\3.\3.\3.\3.\3.\3.\3.\5.\u017e\n.\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\5"+
		"/\u018a\n/\3\60\5\60\u018d\n\60\3\60\3\60\3\61\6\61\u0192\n\61\r\61\16"+
		"\61\u0193\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\64"+
		"\2\65\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f"+
		"\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26"+
		"\1+\27\1-\30\1/\31\1\61\32\1\63\33\1\65\34\1\67\35\19\36\1;\37\1= \1?"+
		"!\1A\"\1C#\1E$\1G%\1I&\1K\'\1M(\1O)\1Q*\1S+\1U,\1W-\1Y.\1[/\1]\60\1_\61"+
		"\1a\62\2c\63\1e\64\1g\65\3\3\2\5\4C\\c|\4--//\5\13\13\17\17\"\"\u01ad"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\3i\3\2\2\2\5o\3\2\2\2\7q\3\2\2"+
		"\2\tv\3\2\2\2\13x\3\2\2\2\rz\3\2\2\2\17|\3\2\2\2\21\u0082\3\2\2\2\23\u0088"+
		"\3\2\2\2\25\u008d\3\2\2\2\27\u0092\3\2\2\2\31\u009f\3\2\2\2\33\u00a9\3"+
		"\2\2\2\35\u00af\3\2\2\2\37\u00b3\3\2\2\2!\u00b5\3\2\2\2#\u00c5\3\2\2\2"+
		"%\u00d3\3\2\2\2\'\u00d8\3\2\2\2)\u00df\3\2\2\2+\u00e7\3\2\2\2-\u00ee\3"+
		"\2\2\2/\u00f0\3\2\2\2\61\u00f5\3\2\2\2\63\u00fb\3\2\2\2\65\u0107\3\2\2"+
		"\2\67\u0109\3\2\2\29\u0112\3\2\2\2;\u0117\3\2\2\2=\u0119\3\2\2\2?\u011e"+
		"\3\2\2\2A\u0120\3\2\2\2C\u0125\3\2\2\2E\u012a\3\2\2\2G\u012c\3\2\2\2I"+
		"\u0131\3\2\2\2K\u0138\3\2\2\2M\u0144\3\2\2\2O\u0149\3\2\2\2Q\u014b\3\2"+
		"\2\2S\u014d\3\2\2\2U\u015a\3\2\2\2W\u0165\3\2\2\2Y\u016e\3\2\2\2[\u017d"+
		"\3\2\2\2]\u0189\3\2\2\2_\u018c\3\2\2\2a\u0191\3\2\2\2c\u0197\3\2\2\2e"+
		"\u019a\3\2\2\2g\u019d\3\2\2\2ij\7^\2\2jk\7n\2\2kl\7p\2\2lm\7q\2\2mn\7"+
		"v\2\2n\4\3\2\2\2op\7.\2\2p\6\3\2\2\2qr\7^\2\2rs\7t\2\2st\7g\2\2tu\7n\2"+
		"\2u\b\3\2\2\2vw\7*\2\2w\n\3\2\2\2xy\7<\2\2y\f\3\2\2\2z{\7>\2\2{\16\3\2"+
		"\2\2|}\7^\2\2}~\7r\2\2~\177\7h\2\2\177\u0080\7w\2\2\u0080\u0081\7p\2\2"+
		"\u0081\20\3\2\2\2\u0082\u0083\7h\2\2\u0083\u0084\7c\2\2\u0084\u0085\7"+
		"n\2\2\u0085\u0086\7u\2\2\u0086\u0087\7g\2\2\u0087\22\3\2\2\2\u0088\u0089"+
		"\7^\2\2\u0089\u008a\7n\2\2\u008a\u008b\7q\2\2\u008b\u008c\7t\2\2\u008c"+
		"\24\3\2\2\2\u008d\u008e\7^\2\2\u008e\u008f\7f\2\2\u008f\u0090\7q\2\2\u0090"+
		"\u0091\7o\2\2\u0091\26\3\2\2\2\u0092\u0093\7^\2\2\u0093\u0094\7g\2\2\u0094"+
		"\u0095\7p\2\2\u0095\u0096\7f\2\2\u0096\u0097\7}\2\2\u0097\u0098\7u\2\2"+
		"\u0098\u0099\7e\2\2\u0099\u009a\7j\2\2\u009a\u009b\7g\2\2\u009b\u009c"+
		"\7o\2\2\u009c\u009d\7c\2\2\u009d\u009e\7\177\2\2\u009e\30\3\2\2\2\u009f"+
		"\u00a0\7^\2\2\u00a0\u00a1\7g\2\2\u00a1\u00a2\7p\2\2\u00a2\u00a3\7f\2\2"+
		"\u00a3\u00a4\7}\2\2\u00a4\u00a5\7|\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7"+
		"\7f\2\2\u00a7\u00a8\7\177\2\2\u00a8\32\3\2\2\2\u00a9\u00aa\7^\2\2\u00aa"+
		"\u00ab\7w\2\2\u00ab\u00ac\7r\2\2\u00ac\u00ad\7v\2\2\u00ad\u00ae\7q\2\2"+
		"\u00ae\34\3\2\2\2\u00af\u00b0\7^\2\2\u00b0\u00b1\7k\2\2\u00b1\u00b2\7"+
		"p\2\2\u00b2\36\3\2\2\2\u00b3\u00b4\7\177\2\2\u00b4 \3\2\2\2\u00b5\u00b6"+
		"\7^\2\2\u00b6\u00b7\7d\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7i\2\2\u00b9"+
		"\u00ba\7k\2\2\u00ba\u00bb\7p\2\2\u00bb\u00bc\7}\2\2\u00bc\u00bd\7u\2\2"+
		"\u00bd\u00be\7e\2\2\u00be\u00bf\7j\2\2\u00bf\u00c0\7g\2\2\u00c0\u00c1"+
		"\7o\2\2\u00c1\u00c2\7c\2\2\u00c2\u00c3\7\177\2\2\u00c3\u00c4\7}\2\2\u00c4"+
		"\"\3\2\2\2\u00c5\u00c6\7^\2\2\u00c6\u00c7\7d\2\2\u00c7\u00c8\7g\2\2\u00c8"+
		"\u00c9\7i\2\2\u00c9\u00ca\7k\2\2\u00ca\u00cb\7p\2\2\u00cb\u00cc\7}\2\2"+
		"\u00cc\u00cd\7c\2\2\u00cd\u00ce\7z\2\2\u00ce\u00cf\7f\2\2\u00cf\u00d0"+
		"\7g\2\2\u00d0\u00d1\7h\2\2\u00d1\u00d2\7\177\2\2\u00d2$\3\2\2\2\u00d3"+
		"\u00d4\7v\2\2\u00d4\u00d5\7t\2\2\u00d5\u00d6\7w\2\2\u00d6\u00d7\7g\2\2"+
		"\u00d7&\3\2\2\2\u00d8\u00d9\7^\2\2\u00d9\u00da\7p\2\2\u00da\u00db\7q\2"+
		"\2\u00db\u00dc\7v\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7p\2\2\u00de(\3\2"+
		"\2\2\u00df\u00e0\7^\2\2\u00e0\u00e1\7u\2\2\u00e1\u00e2\7w\2\2\u00e2\u00e3"+
		"\7d\2\2\u00e3\u00e4\7u\2\2\u00e4\u00e5\7g\2\2\u00e5\u00e6\7v\2\2\u00e6"+
		"*\3\2\2\2\u00e7\u00e8\7^\2\2\u00e8\u00e9\7r\2\2\u00e9\u00ea\7q\2\2\u00ea"+
		"\u00eb\7y\2\2\u00eb\u00ec\7g\2\2\u00ec\u00ed\7t\2\2\u00ed,\3\2\2\2\u00ee"+
		"\u00ef\7+\2\2\u00ef.\3\2\2\2\u00f0\u00f1\7^\2\2\u00f1\u00f2\7k\2\2\u00f2"+
		"\u00f3\7h\2\2\u00f3\u00f4\7h\2\2\u00f4\60\3\2\2\2\u00f5\u00f6\7^\2\2\u00f6"+
		"\u00f7\7n\2\2\u00f7\u00f8\7c\2\2\u00f8\u00f9\7p\2\2\u00f9\u00fa\7f\2\2"+
		"\u00fa\62\3\2\2\2\u00fb\u00fc\7^\2\2\u00fc\u00fd\7g\2\2\u00fd\u00fe\7"+
		"p\2\2\u00fe\u00ff\7f\2\2\u00ff\u0100\7}\2\2\u0100\u0101\7c\2\2\u0101\u0102"+
		"\7z\2\2\u0102\u0103\7f\2\2\u0103\u0104\7g\2\2\u0104\u0105\7h\2\2\u0105"+
		"\u0106\7\177\2\2\u0106\64\3\2\2\2\u0107\u0108\7B\2\2\u0108\66\3\2\2\2"+
		"\u0109\u010a\7^\2\2\u010a\u010b\7k\2\2\u010b\u010c\7o\2\2\u010c\u010d"+
		"\7r\2\2\u010d\u010e\7n\2\2\u010e\u010f\7k\2\2\u010f\u0110\7g\2\2\u0110"+
		"\u0111\7u\2\2\u01118\3\2\2\2\u0112\u0113\7^\2\2\u0113\u0114\7h\2\2\u0114"+
		"\u0115\7w\2\2\u0115\u0116\7p\2\2\u0116:\3\2\2\2\u0117\u0118\7?\2\2\u0118"+
		"<\3\2\2\2\u0119\u011a\7^\2\2\u011a\u011b\7n\2\2\u011b\u011c\7g\2\2\u011c"+
		"\u011d\7s\2\2\u011d>\3\2\2\2\u011e\u011f\7=\2\2\u011f@\3\2\2\2\u0120\u0121"+
		"\7^\2\2\u0121\u0122\7p\2\2\u0122\u0123\7g\2\2\u0123\u0124\7s\2\2\u0124"+
		"B\3\2\2\2\u0125\u0126\7^\2\2\u0126\u0127\7p\2\2\u0127\u0128\7c\2\2\u0128"+
		"\u0129\7v\2\2\u0129D\3\2\2\2\u012a\u012b\7@\2\2\u012bF\3\2\2\2\u012c\u012d"+
		"\7^\2\2\u012d\u012e\7i\2\2\u012e\u012f\7g\2\2\u012f\u0130\7s\2\2\u0130"+
		"H\3\2\2\2\u0131\u0132\7^\2\2\u0132\u0133\7y\2\2\u0133\u0134\7j\2\2\u0134"+
		"\u0135\7g\2\2\u0135\u0136\7t\2\2\u0136\u0137\7g\2\2\u0137J\3\2\2\2\u0138"+
		"\u0139\7^\2\2\u0139\u013a\7d\2\2\u013a\u013b\7g\2\2\u013b\u013c\7i\2\2"+
		"\u013c\u013d\7k\2\2\u013d\u013e\7p\2\2\u013e\u013f\7}\2\2\u013f\u0140"+
		"\7|\2\2\u0140\u0141\7g\2\2\u0141\u0142\7f\2\2\u0142\u0143\7\177\2\2\u0143"+
		"L\3\2\2\2\u0144\u0145\7^\2\2\u0145\u0146\7p\2\2\u0146\u0147\7w\2\2\u0147"+
		"\u0148\7o\2\2\u0148N\3\2\2\2\u0149\u014a\7\u0080\2\2\u014aP\3\2\2\2\u014b"+
		"\u014c\7~\2\2\u014cR\3\2\2\2\u014d\u014e\7^\2\2\u014e\u014f\7o\2\2\u014f"+
		"\u0150\7c\2\2\u0150\u0151\7r\2\2\u0151\u0152\7u\2\2\u0152\u0153\7v\2\2"+
		"\u0153\u0154\7q\2\2\u0154T\3\2\2\2\u0155\u015b\t\2\2\2\u0156\u0157\7^"+
		"\2\2\u0157\u0158\7a\2\2\u0158\u015b\7\"\2\2\u0159\u015b\7A\2\2\u015a\u0155"+
		"\3\2\2\2\u015a\u0156\3\2\2\2\u015a\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c"+
		"\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u0161\3\2\2\2\u015e\u0160\4\62"+
		";\2\u015f\u015e\3\2\2\2\u0160\u0163\3\2\2\2\u0161\u015f\3\2\2\2\u0161"+
		"\u0162\3\2\2\2\u0162V\3\2\2\2\u0163\u0161\3\2\2\2\u0164\u0166\4\62;\2"+
		"\u0165\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0165\3\2\2\2\u0167\u0168"+
		"\3\2\2\2\u0168X\3\2\2\2\u0169\u016f\t\3\2\2\u016a\u016b\7^\2\2\u016b\u016c"+
		"\7e\2\2\u016c\u016d\7w\2\2\u016d\u016f\7r\2\2\u016e\u0169\3\2\2\2\u016e"+
		"\u016a\3\2\2\2\u016fZ\3\2\2\2\u0170\u017e\7,\2\2\u0171\u0172\7^\2\2\u0172"+
		"\u0173\7f\2\2\u0173\u0174\7k\2\2\u0174\u017e\7x\2\2\u0175\u0176\7^\2\2"+
		"\u0176\u0177\7o\2\2\u0177\u0178\7q\2\2\u0178\u017e\7f\2\2\u0179\u017a"+
		"\7^\2\2\u017a\u017b\7e\2\2\u017b\u017c\7c\2\2\u017c\u017e\7r\2\2\u017d"+
		"\u0170\3\2\2\2\u017d\u0171\3\2\2\2\u017d\u0175\3\2\2\2\u017d\u0179\3\2"+
		"\2\2\u017e\\\3\2\2\2\u017f\u0180\7^\2\2\u0180\u0181\7f\2\2\u0181\u0182"+
		"\7q\2\2\u0182\u018a\7o\2\2\u0183\u0184\7^\2\2\u0184\u0185\7u\2\2\u0185"+
		"\u0186\7g\2\2\u0186\u018a\7s\2\2\u0187\u0188\7^\2\2\u0188\u018a\7%\2\2"+
		"\u0189\u017f\3\2\2\2\u0189\u0183\3\2\2\2\u0189\u0187\3\2\2\2\u018a^\3"+
		"\2\2\2\u018b\u018d\7\17\2\2\u018c\u018b\3\2\2\2\u018c\u018d\3\2\2\2\u018d"+
		"\u018e\3\2\2\2\u018e\u018f\7\f\2\2\u018f`\3\2\2\2\u0190\u0192\t\4\2\2"+
		"\u0191\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0191\3\2\2\2\u0193\u0194"+
		"\3\2\2\2\u0194\u0195\3\2\2\2\u0195\u0196\b\61\2\2\u0196b\3\2\2\2\u0197"+
		"\u0198\7^\2\2\u0198\u0199\7}\2\2\u0199d\3\2\2\2\u019a\u019b\7^\2\2\u019b"+
		"\u019c\7\177\2\2\u019cf\3\2\2\2\u019d\u019e\7^\2\2\u019e\u019f\7^\2\2"+
		"\u019f\u01a0\b\64\3\2\u01a0h\3\2\2\2\f\2\u015a\u015c\u0161\u0167\u016e"+
		"\u017d\u0189\u018c\u0193";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}