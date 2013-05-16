// Generated from SLog2Z.g4 by ANTLR 4.0

package compserver.tcasegen.strategies.setlog.setlogtoz;
	import java.util.HashMap;
	import java.util.ArrayList;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.util.Iterator;
	import javax.swing.tree.DefaultMutableTreeNode;
	import javax.swing.tree.DefaultTreeModel;
	import javax.swing.tree.TreeNode;
	import compserver.tcasegen.strategies.setlog.ztosetlog.ExprParser;
	import compserver.tcasegen.strategies.setlog.TypeManagerLexer;
	import compserver.tcasegen.strategies.setlog.TypeManagerParser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SLog2ZParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__16=1, T__15=2, T__14=3, T__13=4, T__12=5, T__11=6, T__10=7, T__9=8, 
		T__8=9, T__7=10, T__6=11, T__5=12, T__4=13, T__3=14, T__2=15, T__1=16, 
		T__0=17, NAME=18, CTE=19, NUM=20, NL=21, WS=22, SKIP=23;
	public static final String[] tokenNames = {
		"<INVALID>", "']'", "'int(0, 10000000000),'", "')'", "','", "'int(-10000000000, 10000000000),'", 
		"'list('", "'set('", "'['", "'-'", "'='", "'_CONSTR'", "'integer('", "'\\'", 
		"'{'", "'neq'", "'}'", "'|'", "NAME", "CTE", "NUM", "'\n'", "WS", "SKIP"
	};
	public static final int
		RULE_lineas = 0, RULE_constr = 1, RULE_restr = 2, RULE_seqIgual = 3, RULE_expr = 4;
	public static final String[] ruleNames = {
		"lineas", "constr", "restr", "seqIgual", "expr"
	};

	@Override
	public String getGrammarFileName() { return "SLog2Z.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }


		HashMap<String,StringPointer> slvars = new HashMap();	
		HashMap<String,String> zNames = new HashMap();
		HashMap<String,String> tipos = new HashMap();
		HashMap<String,String> zVars = new HashMap();
		HashMap<String,String> freeTypes = new HashMap();
		HashMap<String,String> valoresProhibidos = new HashMap();
		ConstantCreator cc;
		public HashMap<String,String> getZVars(){
			return zVars;
		}
		//devuelve un elemento que pertenece a la resta de dos conjuntos de String
		// e = "aaa,bbb,ccc,ddd", ne = "bbb,aaa,ddd" , devuelve ccc
		private static String getNotEqType(String e, String ne){
			String[] aux1 = e.split(",");
			int m = aux1.length;
			int i;
			String s; 
			for (i = 0; i < m; i++){
				s = aux1[i];
				if(!ne.contains(s))
					return s;
			
			}	
			
			return null;
		}
		//usa la estructura tipos, conjunto de valores posible de las variables enumeradas
		//y los valores prohibidos de la estructura valoresProhibidos, y pone un valor por variable en slvars
		private void putNotEqInSlvars(){
			Iterator<String> iterator = valoresProhibidos.keySet().iterator();  
			String key,value,e,cte;String[] aux;
			while (iterator.hasNext()) {  
			   key = iterator.next().toString();
			   e = tipos.get(key);
			   e = e.substring(1,e.length()-1);
			   cte = getNotEqType(e,valoresProhibidos.get(key));
			   slvars.put(key, new StringPointer(cte));
			} 
		}
		//llena la estructura freeTypes, la cual se usa para saber el tipo de una variabla
		//que no figura en slvars, a partir de un elemento que esta en desigualdad en contraint
		private HashMap<String,String> llenarFreeTypes(){
	    	HashMap<String,String> s = new HashMap<String,String>();
	    	Iterator<String> iterator = tipos.keySet().iterator();
	    	String key,valor,nomtipo;
	    	while (iterator.hasNext()) { 
	    		key = iterator.next().toString();
				valor = tipos.get(key);
				//EnumerationType:FT:{elem1,elem2}
				if (valor.startsWith("EnumerationType")){
					String[] aux = valor.split(":");
					nomtipo =  aux[1];
					//aux = ((String) (aux[2]).subSequence(1, (aux[2]).length()-1)).split(",");
					s.put(nomtipo, ((String) (aux[2]).subSequence(1, (aux[2]).length()-1)));
				}
	    	}
	    	
	    	return s;
	    }
	    
		public void loadTablas(HashMap<String,String> zVars, HashMap<String,String> tipos, HashMap<String,String> memory){
			zNames = invertMap(memory);
			this.tipos = tipos;
			this.zVars = zVars;
			
			
			System.out.println("\n");
			System.out.println("memory: "); 
			printHashMap(zNames);
			System.out.println("\n tipos: "); 
			printHashMap(tipos);
			System.out.println("\n");
			freeTypes = llenarFreeTypes();
			System.out.println("\n tipos Libres: "); 
			printHashMap(freeTypes);
			System.out.println("\n");
			cc = new ConstantCreator(tipos,zNames,slvars,valoresProhibidos);
			
		}
		private String getCte(String cte, String tipo) {
			ANTLRInputStream input = new ANTLRInputStream(tipo);
	        TypeManagerLexer lexer = new TypeManagerLexer(input);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        TypeManagerParser parser = new TypeManagerParser(tokens);
	        parser.typeManage();
	        DefaultMutableTreeNode root =  parser.getRoot();
	        
	        System.out.println("\narbol " + parser.printTree(root)); 
	        System.out.println("cte " + cte);
	        System.out.println("tipo " + tipo);
	        System.out.println("root " + root.toString());
	         
	        return cc.getCte(cte,root);
		}
		
		private String getTipoLibre(String elem){
	    	Iterator<String> iterator = freeTypes.keySet().iterator();  
			String key;	String value;
			while (iterator.hasNext()) { 
				key = iterator.next().toString();
				value = freeTypes.get(key);
				if(value !=null){
					if (value.contains(elem))
						return value;
				}
			}
	    	
	    	return "null";
	    }
		
		private HashMap<String,String> invertMap(HashMap<String,String> m){
			Iterator<String> iterator = m.keySet().iterator();  
	   		HashMap<String,String> s = new HashMap<String,String>();
	   		
			while (iterator.hasNext()) {  
			   String key = iterator.next().toString();  
			   String value = m.get(key).toString();  
			   s.put(value,key);
			} 	
			return s;
		}
		
		public void printHashMap(HashMap map){
			Iterator iterator = map.keySet().iterator();  
			String key,value;
			while (iterator.hasNext()) {  
			   key = iterator.next().toString();
			   if (map.get(key) == null)
				   value = "nullc";
			   else 
				   value = map.get(key).toString();
			   System.out.println(key + " = " + value);  
			} 
		}
		
		private void printHashMap2(HashMap<String,String[]> map){
			Iterator<String> iterator = map.keySet().iterator();  
			String key;	String[] value;
			while (iterator.hasNext()) {  
			   key = iterator.next().toString();
			   if (map.get(key) == null){
				   System.out.println(key + " = " + "nullc");
				   continue;
			   }
			   else{ 
				   
				   value = map.get(key);
				   System.out.print(key + " = "); 
				   for (int i = 0; i<value.length;i++) 
					   System.out.print(value[i] + ",");
				   System.out.println(); 
			   }
			} 
		}
		
		public void llenarZVars(){
			Iterator iterator = slvars.keySet().iterator();  
			String slname,zname,valor;
			while (iterator.hasNext()) {  
				slname = iterator.next().toString();
				if (slvars.get(slname)!=null){	
					valor = slvars.get(slname).toString();
					
					
					zname = zNames.get(slname);
					if (zVars.containsKey(zname)){
						zVars.put(zname,valor);
					} 	
				}  
			} 
		}
		

	public SLog2ZParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class LineasContext extends ParserRuleContext {
		public ConstrContext constr;
		public List<SeqIgualContext> seqIgual() {
			return getRuleContexts(SeqIgualContext.class);
		}
		public List<TerminalNode> NL() { return getTokens(SLog2ZParser.NL); }
		public SeqIgualContext seqIgual(int i) {
			return getRuleContext(SeqIgualContext.class,i);
		}
		public ConstrContext constr() {
			return getRuleContext(ConstrContext.class,0);
		}
		public TerminalNode NL(int i) {
			return getToken(SLog2ZParser.NL, i);
		}
		public LineasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lineas; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).enterLineas(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).exitLineas(this);
		}
	}

	public final LineasContext lineas() throws RecognitionException {
		LineasContext _localctx = new LineasContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_lineas);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10); ((LineasContext)_localctx).constr = constr();
			setState(11); match(NL);

						System.out.println("\n valoresProhibidos desigualdades: ");
						printHashMap(valoresProhibidos);
						System.out.println("asaaaaaaa" + "\n");
					
			setState(17); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(13); seqIgual();
				setState(15);
				_la = _input.LA(1);
				if (_la==NL) {
					{
					setState(14); match(NL);
					}
				}

				}
				}
				setState(19); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );

						System.out.println("\n**salida SLog2Z**: \n");
						System.out.println("constraint: " + (((LineasContext)_localctx).constr!=null?_input.getText(((LineasContext)_localctx).constr.start,((LineasContext)_localctx).constr.stop):null) );
						System.out.println("\nslvars:");
						printHashMap( slvars );
						llenarZVars();
						System.out.println("\nzVars vacias +++++++++:");
						printHashMap(zVars);
						System.out.println("\n**fin SLog2Z**");
						
					
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstrContext extends ParserRuleContext {
		public RestrContext restr(int i) {
			return getRuleContext(RestrContext.class,i);
		}
		public List<RestrContext> restr() {
			return getRuleContexts(RestrContext.class);
		}
		public ConstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).enterConstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).exitConstr(this);
		}
	}

	public final ConstrContext constr() throws RecognitionException {
		ConstrContext _localctx = new ConstrContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_constr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23); match(11);
			System.out.println("CONTATNANTOANTs");
			setState(25); match(10);
			setState(26); match(8);
			setState(35);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 7) | (1L << 8) | (1L << 9) | (1L << 12) | (1L << 14) | (1L << NAME) | (1L << CTE))) != 0)) {
				{
				setState(27); restr();
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(28); match(4);
					setState(29); restr();
					}
					}
					setState(34);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(37); match(1);
			setState(38); match(4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RestrContext extends ParserRuleContext {
		public StringPointer valor;;
		public ExprContext expr;
		public Token NAME;
		public TerminalNode NAME() { return getToken(SLog2ZParser.NAME, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RestrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_restr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).enterRestr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).exitRestr(this);
		}
	}

	public final RestrContext restr() throws RecognitionException {
		RestrContext _localctx = new RestrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_restr);
		((RestrContext)getInvokingContext(2)).valor =  new StringPointer();
		try {
			setState(66);
			switch (_input.LA(1)) {
			case 7:
				enterOuterAlt(_localctx, 1);
				{
				setState(40); match(7);
				setState(41); ((RestrContext)_localctx).expr = expr();
				setState(42); match(3);
				((RestrContext)getInvokingContext(2)).valor.setString("\\{\\}"); slvars.put((((RestrContext)_localctx).expr!=null?_input.getText(((RestrContext)_localctx).expr.start,((RestrContext)_localctx).expr.stop):null),((RestrContext)getInvokingContext(2)).valor);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 2);
				{
				setState(45); match(6);
				setState(46); ((RestrContext)_localctx).expr = expr();
				setState(47); match(3);
				((RestrContext)getInvokingContext(2)).valor.setString("\\langle\\rangle"); slvars.put((((RestrContext)_localctx).expr!=null?_input.getText(((RestrContext)_localctx).expr.start,((RestrContext)_localctx).expr.stop):null),((RestrContext)getInvokingContext(2)).valor);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 3);
				{
				setState(50); match(12);
				setState(51); ((RestrContext)_localctx).expr = expr();
				setState(52); match(3);
				((RestrContext)getInvokingContext(2)).valor.setString(cc.getNumber()); slvars.put((((RestrContext)_localctx).expr!=null?_input.getText(((RestrContext)_localctx).expr.start,((RestrContext)_localctx).expr.stop):null),((RestrContext)getInvokingContext(2)).valor);
				}
				break;
			case 8:
			case 9:
			case 14:
			case NAME:
			case CTE:
				enterOuterAlt(_localctx, 4);
				{
				setState(62);
				switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
				case 1:
					{
					setState(55); ((RestrContext)_localctx).NAME = match(NAME);
					setState(56); match(15);
					setState(57); ((RestrContext)_localctx).expr = expr();
					}
					break;

				case 2:
					{
					setState(58); ((RestrContext)_localctx).expr = expr();
					setState(59); match(15);
					setState(60); ((RestrContext)_localctx).NAME = match(NAME);
					}
					break;
				}

							String var = (((RestrContext)_localctx).NAME!=null?((RestrContext)_localctx).NAME.getText():null);
							String cte = (((RestrContext)_localctx).expr!=null?_input.getText(((RestrContext)_localctx).expr.start,((RestrContext)_localctx).expr.stop):null);
							String s = valoresProhibidos.get(var);
							 
							if (s!=null && !s.contains(cte)) 
								valoresProhibidos.put(var,s.concat("," + cte));
							else{
								s = new String(cte);
								valoresProhibidos.put(var, s);
								}
								
							char c = cte.charAt(0);
							s = valoresProhibidos.get(cte);
							if (Character.isUpperCase(c) || c == '_') {
								
								if (s!=null && !s.contains(var)) 
									valoresProhibidos.put(cte,s.concat("," + var));
								else{
									s = new String(var);
									valoresProhibidos.put(cte, s);
								}
							}
							
						
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SeqIgualContext extends ParserRuleContext {
		public StringPointer valor;;
		public Token v1;
		public ExprContext v2;
		public List<TerminalNode> NAME() { return getTokens(SLog2ZParser.NAME); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode NAME(int i) {
			return getToken(SLog2ZParser.NAME, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public SeqIgualContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_seqIgual; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).enterSeqIgual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).exitSeqIgual(this);
		}
	}

	public final SeqIgualContext seqIgual() throws RecognitionException {
		SeqIgualContext _localctx = new SeqIgualContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_seqIgual);
		((SeqIgualContext)getInvokingContext(3)).valor =  new StringPointer();
		try {
			int _alt;
			setState(87);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(68); match(NAME);
				setState(69); match(10);
				setState(70); match(5);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71); match(NAME);
				setState(72); match(10);
				setState(73); match(2);
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(81); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(74); ((SeqIgualContext)_localctx).v1 = match(NAME);
						slvars.put((((SeqIgualContext)_localctx).v1!=null?((SeqIgualContext)_localctx).v1.getText():null),((SeqIgualContext)getInvokingContext(3)).valor);
						setState(76); match(10);
						setState(77); ((SeqIgualContext)_localctx).v2 = expr();
						slvars.put((((SeqIgualContext)_localctx).v2!=null?_input.getText(((SeqIgualContext)_localctx).v2.start,((SeqIgualContext)_localctx).v2.stop):null),((SeqIgualContext)getInvokingContext(3)).valor);
						setState(79); match(4);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(83); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				} while ( _alt!=2 && _alt!=-1 );

							String zname = zNames.get((((SeqIgualContext)_localctx).v1!=null?((SeqIgualContext)_localctx).v1.getText():null));
							String tipo = tipos.get(zname);
							if (!tipo.startsWith("BasicType") && !tipo.startsWith("EnumerationType") )
							{
								String var = ((SeqIgualContext)_localctx).v2.valor;
								((SeqIgualContext)getInvokingContext(3)).valor.setString(getCte(var,tipo));
							}
							 	
						 
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public String valor;
		public Token CTE;
		public Token NAME;
		public ExprContext e;
		public TerminalNode CTE() { return getToken(SLog2ZParser.CTE, 0); }
		public TerminalNode NAME() { return getToken(SLog2ZParser.NAME, 0); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLog2ZListener ) ((SLog2ZListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_expr);
		int _la;
		try {
			setState(143);
			switch (_input.LA(1)) {
			case CTE:
				enterOuterAlt(_localctx, 1);
				{
				setState(89); ((ExprContext)_localctx).CTE = match(CTE);
				((ExprContext)_localctx).valor =  (((ExprContext)_localctx).CTE!=null?((ExprContext)_localctx).CTE.getText():null);
				}
				break;
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(91); ((ExprContext)_localctx).NAME = match(NAME);
				((ExprContext)_localctx).valor =  (((ExprContext)_localctx).NAME!=null?((ExprContext)_localctx).NAME.getText():null);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 3);
				{
				setState(93); match(14);
				((ExprContext)_localctx).valor =  "{";
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 4) | (1L << 8) | (1L << 9) | (1L << 14) | (1L << NAME) | (1L << CTE))) != 0)) {
					{
					{
					setState(97);
					_la = _input.LA(1);
					if (_la==4) {
						{
						setState(95); match(4);
						((ExprContext)_localctx).valor =  _localctx.valor + ",";
						}
					}

					setState(99); ((ExprContext)_localctx).e = expr();
					((ExprContext)_localctx).valor =  _localctx.valor + ((ExprContext)_localctx).e.valor;
					}
					}
					setState(106);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==13) {
					{
					{
					setState(107); match(13);
					setState(108); expr();
					}
					}
					setState(113);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(114); match(16);
				((ExprContext)_localctx).valor =  _localctx.valor + "}";
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 4);
				{
				setState(116); match(8);
				((ExprContext)_localctx).valor =  "[";
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 4) | (1L << 8) | (1L << 9) | (1L << 14) | (1L << NAME) | (1L << CTE))) != 0)) {
					{
					{
					setState(120);
					_la = _input.LA(1);
					if (_la==4) {
						{
						setState(118); match(4);
						((ExprContext)_localctx).valor =  _localctx.valor + ",";
						}
					}

					setState(122); ((ExprContext)_localctx).e = expr();
					((ExprContext)_localctx).valor =  _localctx.valor + ((ExprContext)_localctx).e.valor;
					}
					}
					setState(129);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==17) {
					{
					{
					setState(130); match(17);
					setState(131); expr();
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(137); match(1);
				((ExprContext)_localctx).valor =  _localctx.valor + "]";
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 5);
				{
				setState(139); match(9);
				setState(140); expr();
				((ExprContext)_localctx).valor =  "-" + _localctx.valor ;
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\31\u0094\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2"+
		"\3\2\5\2\22\n\2\6\2\24\n\2\r\2\16\2\25\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\7\3!\n\3\f\3\16\3$\13\3\5\3&\n\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5"+
		"\4A\n\4\3\4\3\4\5\4E\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\6\5T\n\5\r\5\16\5U\3\5\3\5\5\5Z\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\5\6d\n\6\3\6\3\6\3\6\7\6i\n\6\f\6\16\6l\13\6\3\6\3\6\7\6p\n\6\f\6"+
		"\16\6s\13\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6{\n\6\3\6\3\6\3\6\7\6\u0080\n\6"+
		"\f\6\16\6\u0083\13\6\3\6\3\6\7\6\u0087\n\6\f\6\16\6\u008a\13\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6\u0092\n\6\3\6\2\7\2\4\6\b\n\2\2\u00a3\2\f\3\2\2\2"+
		"\4\31\3\2\2\2\6D\3\2\2\2\bY\3\2\2\2\n\u0091\3\2\2\2\f\r\5\4\3\2\r\16\7"+
		"\27\2\2\16\23\b\2\1\2\17\21\5\b\5\2\20\22\7\27\2\2\21\20\3\2\2\2\21\22"+
		"\3\2\2\2\22\24\3\2\2\2\23\17\3\2\2\2\24\25\3\2\2\2\25\23\3\2\2\2\25\26"+
		"\3\2\2\2\26\27\3\2\2\2\27\30\b\2\1\2\30\3\3\2\2\2\31\32\7\r\2\2\32\33"+
		"\b\3\1\2\33\34\7\f\2\2\34%\7\n\2\2\35\"\5\6\4\2\36\37\7\6\2\2\37!\5\6"+
		"\4\2 \36\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#&\3\2\2\2$\"\3\2\2\2"+
		"%\35\3\2\2\2%&\3\2\2\2&\'\3\2\2\2\'(\7\3\2\2()\7\6\2\2)\5\3\2\2\2*+\7"+
		"\t\2\2+,\5\n\6\2,-\7\5\2\2-.\b\4\1\2.E\3\2\2\2/\60\7\b\2\2\60\61\5\n\6"+
		"\2\61\62\7\5\2\2\62\63\b\4\1\2\63E\3\2\2\2\64\65\7\16\2\2\65\66\5\n\6"+
		"\2\66\67\7\5\2\2\678\b\4\1\28E\3\2\2\29:\7\24\2\2:;\7\21\2\2;A\5\n\6\2"+
		"<=\5\n\6\2=>\7\21\2\2>?\7\24\2\2?A\3\2\2\2@9\3\2\2\2@<\3\2\2\2AB\3\2\2"+
		"\2BC\b\4\1\2CE\3\2\2\2D*\3\2\2\2D/\3\2\2\2D\64\3\2\2\2D@\3\2\2\2E\7\3"+
		"\2\2\2FG\7\24\2\2GH\7\f\2\2HZ\7\7\2\2IJ\7\24\2\2JK\7\f\2\2KZ\7\4\2\2L"+
		"M\7\24\2\2MN\b\5\1\2NO\7\f\2\2OP\5\n\6\2PQ\b\5\1\2QR\7\6\2\2RT\3\2\2\2"+
		"SL\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\b\5\1\2XZ\3\2\2\2"+
		"YF\3\2\2\2YI\3\2\2\2YS\3\2\2\2Z\t\3\2\2\2[\\\7\25\2\2\\\u0092\b\6\1\2"+
		"]^\7\24\2\2^\u0092\b\6\1\2_`\7\20\2\2`j\b\6\1\2ab\7\6\2\2bd\b\6\1\2ca"+
		"\3\2\2\2cd\3\2\2\2de\3\2\2\2ef\5\n\6\2fg\b\6\1\2gi\3\2\2\2hc\3\2\2\2i"+
		"l\3\2\2\2jh\3\2\2\2jk\3\2\2\2kq\3\2\2\2lj\3\2\2\2mn\7\17\2\2np\5\n\6\2"+
		"om\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2rt\3\2\2\2sq\3\2\2\2tu\7\22\2"+
		"\2u\u0092\b\6\1\2vw\7\n\2\2w\u0081\b\6\1\2xy\7\6\2\2y{\b\6\1\2zx\3\2\2"+
		"\2z{\3\2\2\2{|\3\2\2\2|}\5\n\6\2}~\b\6\1\2~\u0080\3\2\2\2\177z\3\2\2\2"+
		"\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0088"+
		"\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\7\23\2\2\u0085\u0087\5\n\6\2"+
		"\u0086\u0084\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089"+
		"\3\2\2\2\u0089\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c\7\3\2\2\u008c"+
		"\u0092\b\6\1\2\u008d\u008e\7\13\2\2\u008e\u008f\5\n\6\2\u008f\u0090\b"+
		"\6\1\2\u0090\u0092\3\2\2\2\u0091[\3\2\2\2\u0091]\3\2\2\2\u0091_\3\2\2"+
		"\2\u0091v\3\2\2\2\u0091\u008d\3\2\2\2\u0092\13\3\2\2\2\21\21\25\"%@DU"+
		"Ycjqz\u0081\u0088\u0091";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}