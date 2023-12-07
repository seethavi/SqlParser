package com.paras.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.format.MatchStrength;
import com.paras.ast.AccessReferencer;
import com.paras.ast.Column;
import com.paras.ast.Constraint;
import com.paras.ast.Constraint.ConstraintType;
import com.paras.ast.Container;
import com.paras.ast.Function;
import com.paras.ast.Package;
import com.paras.ast.Parameter;
import com.paras.ast.ParseableObject;
import com.paras.ast.Procedure;
import com.paras.ast.Schema;
import com.paras.ast.SqlSource;
import com.paras.ast.Table;
import com.paras.ast.TableAccess.AccessType;
import com.paras.ast.Trigger;
import com.paras.ast.View;
import com.paras.io.DirectoryScanner;
import com.paras.io.OutputWriter;
import com.paras.io.UmlModelBuilder;
import com.paras.parser.Token.TokenType;

public class Parser {

    private static final String KWD_FILE = "keywords.txt";
    private static final String NUM_REGEX = "[0-9]*$?[0-9]+";
    private static final String MSG = "---> Adding reference to procedure#paramcount:";
    private Reader in;
    private OutputWriter out;
    private ParseStack stack;
    private SqlSource sqlSource;
    private Pattern numPattern;
    private Set<String> procExclusionSet;
    private Set<String> packageSet;
    private String workingDir;
    private boolean verbose;
    private static Logger logger = Logger.getLogger("com.paras.parser.Parser");

    public Parser(File inputFile, String resourcesDir, String outputDir) throws Exception {
        String fileName = inputFile.getAbsolutePath();
        String fileNameNoExt = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
        FileReader reader = new FileReader(inputFile);
        if(this.verbose) {
            FileWriter writer = new FileWriter(outputDir + File.separator + inputFile.getName() + ".out");
            this.out = new OutputWriter(writer);
        }
        else {
            this.out = new OutputWriter(new StringWriter());
        }

        this.in = reader;

        this.stack = new ParseStack();
        this.sqlSource = new SqlSource(fileNameNoExt);
        sqlSource.setSourceFile(inputFile);
        this.procExclusionSet = new HashSet<>();
        this.packageSet = new HashSet<>();
        this.numPattern = Pattern.compile(NUM_REGEX);
        this.workingDir = resourcesDir;
        initProcExclusionList();
        if (verbose) {
            out.writeln("+++++++++++++++++++>Pushing: " + fileName);
        }
        sqlSource.incBeginCount();
        stack.push(sqlSource);
        this.verbose = true;

    }

    public Set<String> getPackageSet() {
        return this.packageSet;
    }

    public void setPackageSet(Set<String> packageSet) {
        this.packageSet = packageSet;
    }

    public void setVerbose(boolean val) {
        verbose = val;
    }

    public SqlSource getParseTree() {
        return sqlSource;
    }

    private void initProcExclusionList() throws IOException {
        InputStream is = new FileInputStream(workingDir + "/" + KWD_FILE);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String str = null;
        while ((str = r.readLine()) != null) {
            procExclusionSet.add(str);
        }
    }

    private boolean isProcExcluded(String str) {
        return procExclusionSet.contains(str.toLowerCase());
    }

    private void parsePackage(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            String pkgName = token.getStrVal();
            Package pkg = new Package(pkgName);
            pkg.incBeginCount();
            SqlSource source = (SqlSource) stack.peek();
            source.addPackage(pkg);
            if (verbose) {
                out.writeln("+++++++++++++++++>Pushing: " + pkg.getName());
            }
            stack.push(pkg);
        }
    }

    private boolean checkProcedureCall(String currentToken, Tokeniser tk) throws Exception {
        boolean likelyProcCall = true;
        Token la = tk.lookAhead();
        TokenType tknVal = la.getTknVal();
        if (tknVal == TokenType.COLON) { // if the symbol is found on the left side of an := then it unlikely
            // to be a function or a procedure invocation
            return false;
        } else if (tknVal == TokenType.ID) { // if the following symbol is also an identifier, then it is likely
            //that this is a declaration and not a procedure / function reference
            return false;
        }
        if (isProcExcluded(currentToken)) {
            return false;
        }
        return likelyProcCall;
    }

    private List<Parameter> parseParameters(Tokeniser tk, TokenType exitTkn) throws Exception {
        List<Parameter> parameterList = null;
        if (tk.lookAhead().getTknVal() == TokenType.SEMI) {
            return null;
        }
        if (skipUntil(tk, TokenType.OPEN_BRACK, exitTkn)) {
            parameterList = new ArrayList<>();
            while (tk.hasMoreTokens()) {

                String name = tk.nextToken().getStrVal();
                StringBuilder modifiability = new StringBuilder(""); //  default to in, if no modifiability is specified

                Token lookAhead = tk.lookAhead();
                //String la = lookAhead.getVal();
                TokenType tknVal = lookAhead.getTknVal();
                if (tknVal == TokenType.IN || tknVal == TokenType.OUT || tknVal == TokenType.NOCOPY) {
                    modifiability.append(tk.nextToken().getStrVal());
                    tknVal = tk.lookAhead().getTknVal();
                    if (tknVal == TokenType.IN || tknVal == TokenType.OUT || tknVal == TokenType.NOCOPY) {
                        modifiability.append(tk.nextToken().getStrVal()); // account for cases such as "param in out nocopy type"
                        tknVal = tk.lookAhead().getTknVal();
                        if (tknVal == TokenType.IN || tknVal == TokenType.OUT || tknVal == TokenType.NOCOPY) {
                            modifiability.append(tk.nextToken().getStrVal()); // account for cases such as "param in out type"
                        }
                    }
                }
                String type = tk.nextToken().getStrVal();
                Parameter param = new Parameter(name, type, modifiability.toString());
                parameterList.add(param);
                if (!skipUntil(tk, TokenType.COMMA, TokenType.CLOSE_BRACK)) {
                    break;
                }
                /*
                else {
                while(tk.lookAhead().getTknVal() != TokenType.ID) { // skip any extraneous tokens in the middle until the ID token is reached.
                tk.nextToken();
                }
                }
                 * 
                 */
            }
        }
        return parameterList;
    }

    private void parseProcedure(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            String procName = tk.nextToken().getStrVal();
            List<Parameter> paramList = parseParameters(tk, TokenType.IS);
            if (tk.lookAhead().getTknVal() != TokenType.SEMI) {//if it is not a semicolon then it is a procedure definition.
                // if it is a semicolon then it is a procedure forward declaration and we are not interested in that.
                Procedure proc = new Procedure(procName);
                Container procContainer = (Container) stack.peek();
                proc.setParameterList(paramList); // set the parameterlist for the procedure
                procContainer.addProcedure(proc); // add the procedure
                if (verbose) {
                    out.writeln("++++++++++++++++>Pushing: " + proc.getName());
                }
                stack.push(proc);
            }
        }
    }

    private void parseFunction(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            String funcName = tk.nextToken().getStrVal();
            List<Parameter> paramList = parseParameters(tk, TokenType.RETURN);
            String returnType = null;
            if (tk.hasMoreTokens()) {
                Token token = tk.nextToken();
                TokenType tkn = token.getTknVal();
                if (tkn == TokenType.RETURN) {
                    returnType = tk.nextToken().getStrVal();
                } else {
                    returnType = token.getStrVal();
                }
            }
            Token la = tk.lookAhead();
            if (la.getTknVal() != TokenType.SEMI) {//if semicolon then it is a function definition, so include else
                // it is a function declaration, which we are not interested
                Function func = new Function(funcName);
                Container source = (Container) stack.peek();
                func.setParameterList(paramList); // set the parameterlist for the function
                source.addFunction(func); // add the function to its parent
                func.setReturnType(returnType);
                if (verbose) {
                    out.writeln("++++++++++++++++>Pushing: " + func.getName());
                }
                stack.push(func);
            }
        }
    }

    private void skipUntil(Tokeniser tk, TokenType tkn) throws Exception {
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            if (token.getTknVal() == tkn) {
                return;
            }
        }
    }

    private void skipAndCheckUntil(Tokeniser tk, TokenType tkn) throws Exception {
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            parseToken(tk, token);
            if (token.getTknVal() == tkn) {
                return;
            }
        }
    }

    private int countTknAndCheckProcedureCall(Map<String, Procedure> procSet, Tokeniser tk, TokenType tknType, TokenType endTkn) throws Exception {
        tk.nextToken(); // consume the OPEN_BRACK token as the callers would have used this to collect the arguments for the call
        if (tk.lookAhead().getTknVal() == TokenType.CLOSE_BRACK) { // if there are no arguments then return 0
            return 0;
        }
        int count = 1;
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            
            if (token.getTknVal() == TokenType.ID && checkProcedureCall(token.getStrVal(), tk)) {
                if (tk.lookAhead().getTknVal() == TokenType.OPEN_BRACK) {
                    int paramCount = countTknAndCheckProcedureCall(procSet, tk, TokenType.COMMA, TokenType.CLOSE_BRACK); // recurse
                    tk.nextToken(); // consume the end token
                    StringBuilder invocation = new StringBuilder();
                    invocation.append(token.getStrVal()).append("#").append(paramCount);
                    if (verbose) {
                        out.writeln( MSG + invocation.toString());
                    }
                    procSet.put(invocation.toString(), null);
                } else {
                    StringBuilder invocation = new StringBuilder();
                    invocation.append(token.getStrVal()).append("#0");
                    if (verbose) {
                        out.writeln(MSG + invocation.toString());
                    }
                    procSet.put(invocation.toString(), null);
                }
            } else if (token.getTknVal() == tknType) {
                count++;
            }
            if (tk.lookAhead().getTknVal() == endTkn) { // break before the end token is consumed. 
                // so that the next token read by the caller will be the end token
                break;
            }
        }
        return count;
    }

    private Map<String, Procedure> skipCheckProcedureCall(Tokeniser tk, TokenType endTkn) throws Exception {
        Map<String, Procedure> procSet = new HashMap<>();
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            if (token.getTknVal() == TokenType.ID && checkProcedureCall(token.getStrVal(), tk)) {
                if (tk.lookAhead().getTknVal() == TokenType.OPEN_BRACK) {
                    int paramCount = countTknAndCheckProcedureCall(procSet, tk, TokenType.COMMA, TokenType.CLOSE_BRACK); // recurse
                    StringBuilder invocation = new StringBuilder();
                    invocation.append(token.getStrVal()).append("#").append(paramCount);
                    if (verbose) {
                        out.writeln(MSG + invocation.toString());
                    }
                    procSet.put(invocation.toString(), null);
                } else {
                    StringBuilder invocation = new StringBuilder();
                    invocation.append(token.getStrVal()).append("#0");
                    if (verbose) {
                        out.writeln(MSG + invocation.toString());
                    }
                    procSet.put(invocation.toString(), null);
                }
            }
            if (tk.lookAhead().getTknVal() == endTkn) { // break before the end token is consumed. 
                // so that the next token read by the caller will be the end token
                break;
            }
            
        }
        return procSet;
    }

    private boolean skipUntil(Tokeniser tk, TokenType tkn, TokenType exitTkn) throws Exception {
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            TokenType tkVal = token.getTknVal();
            if (tkVal == tkn) {
                return true;
            } else if (tkVal == exitTkn) {
                return false;
            }
        }
        return false;
    }

    private boolean skipUntilSeq(Tokeniser tk, TokenType[] tknSeq, Set<TokenType> endSet) throws Exception {
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            TokenType tkVal = token.getTknVal();
            if (tkVal == tknSeq[0] && tk.lookAhead(1).getTknVal() == tknSeq[1]) {
                return true;
            } else if (endSet.contains(tkVal)) {
                return false;
            }
        }
        return false;
    }

    private boolean isNumeric(String str) {
        Matcher matcher = numPattern.matcher(str.replace('.', '$'));
        return matcher.matches();
    }

    private boolean isSeparator(Tokeniser tk, Token tok) throws IOException {
        if (tok.getTknVal() != TokenType.SEPARATOR) {
            return false;
        }
        String str = tk.lookAhead().getStrVal();
        TokenType nextTok = tk.lookAhead().getTknVal();
        return !(nextTok == TokenType.ID || isNumeric(str)); // if the token is numeric or is an identifier, then this is a
        // middle of an expression and not a true statement separator
    }

    private void parseTableAccess(Tokeniser tk, Token tok) throws Exception {
        TokenType tkn = tok.getTknVal();
        switch (tkn) {

            case SELECT: {
                AccessReferencer source = (AccessReferencer) stack.peek();
                if (tk.getCurrentToken().getTknVal() != TokenType.FROM) {
                    Map<String, Procedure> procMap = skipCheckProcedureCall(tk, TokenType.FROM); // start scanning 
                    // 	select stmt for "FROM" and capture any function references during the skip
                    source.addInvocationRefs(procMap); // keep adding the function references
                }
                Token nextTok = tk.nextToken();
                while (nextTok.getTknVal() != TokenType.EOF && nextTok.getTknVal() != TokenType.SEMI
                        && tk.lookAhead().getTknVal() != TokenType.LOOP && !isSeparator(tk, nextTok)) { // just look for FROM statements and read table names following it
                    if (nextTok.getTknVal() == TokenType.SELECT) { // Nested select statement
                        Map<String, Procedure> procMap = skipCheckProcedureCall(tk, TokenType.FROM); // start scanning select stmt for "FROM" and
                        // capture any function references during the skip
                        source.addInvocationRefs(procMap); // keep adding the function references
                        nextTok = tk.nextToken();
                    } else if (nextTok.getTknVal() == TokenType.FROM && tk.lookAhead().getTknVal() == TokenType.ID) { // an identifier following a from clause
                        // is likely to be a table name, so consume it and add it to the table list
                        nextTok = tk.nextToken(); // consume the FROM token and move to the next token
                        while (nextTok.getTknVal() != TokenType.WHERE && nextTok.getTknVal() != TokenType.CLOSE_BRACK
                                && nextTok.getTknVal() != TokenType.OPEN_BRACK && nextTok.getTknVal() != TokenType.UNION
                                && nextTok.getTknVal() != TokenType.SEMI && nextTok.getTknVal() != TokenType.EOF) { // ensure that it is not 
                            // one of where, union, where or another open bracket and loop through to collect
                            // table names. The side effect of this is that, if a select statement has a form like this:
                            //		SELECT constraintID, name 
                            //      FROM MktConstraint mk;
                            // then this piece of logic will create a table reference form mk as well. This can be
                            // eliminated later during the table rationalisation process, where an access is only valid
                            // if a table exists with that name
                            if (nextTok.getTknVal() != TokenType.COMMA) { // skip the comma
                                source.addTableAccessRef(nextTok.getStrVal(), AccessType.SELECT); // add the table
                                // reference to the object on the top of the stack which corresponds to the currently
                                // processed function, procedure or trigger.
                            }
                            nextTok = tk.nextToken();
                        }
                    } else {
                        if (nextTok.getTknVal() == TokenType.ID && checkProcedureCall(nextTok.getStrVal(), tk)) {
                            String currentToken = nextTok.getStrVal();
                            TokenType tknVal = tk.lookAhead().getTknVal();
                            if (tknVal == TokenType.OPEN_BRACK) {
                                Map<String, Procedure> procMap = new HashMap<>();
                                int paramCount = countTknAndCheckProcedureCall(procMap, tk, TokenType.COMMA, TokenType.CLOSE_BRACK);
                                if (verbose) {
                                    out.writeln("--> Adding reference to procedure#paramcount:" + currentToken + "#" + paramCount);
                                }
                                source.addInvocationRef(currentToken + "#" + paramCount, null);
                                source.addInvocationRefs(procMap);
                                tk.nextToken(); // consume the CLOSE_BRACK token
                            } else {
                                if (verbose) {
                                    out.writeln("--> Adding reference to procedure#paramcount:" + currentToken + "#0");
                                }
                                source.addInvocationRef(currentToken + "#" + 0, null);
                            }
                        }
                        nextTok = tk.nextToken(); // get the next token
                    }
                }
            }
            break;
            case INSERT:
                skipAndCheckUntil(tk, TokenType.INTO);
                if (tk.hasMoreTokens()) {
                    String nextTok = tk.nextToken().getStrVal();
                    AccessReferencer ref = (AccessReferencer) stack.peek();
                    ref.addTableAccessRef(nextTok, AccessType.INSERT);
                }
                break;
            case UPDATE:
                if (tk.hasMoreTokens()) {
                    Token token = tk.nextToken();
                    String val = token.getStrVal();
                    if (token.getTknVal() != TokenType.OF && token.getTknVal() != TokenType.SEMI) {
                        AccessReferencer ref = (AccessReferencer) stack.peek();
                        ref.addTableAccessRef(val, AccessType.UPDATE);
                    }
                }
                break;
            case DELETE:
                skipAndCheckUntil(tk, TokenType.FROM);
                if (tk.hasMoreTokens()) {
                    String nextTok = tk.nextToken().getStrVal();
                    AccessReferencer ref = (AccessReferencer) stack.peek();
                    ref.addTableAccessRef(nextTok, AccessType.DELETE);
                }
                break;
        }
    }

    private void parseTableDefinition(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            String tableName = token.getStrVal();
            Table table = new Table(tableName);
            Map<String, Column> columns = parseColumns(tk, TokenType.CLOSE_BRACK);
            table.setColumnMap(columns);
            SqlSource source = (SqlSource) stack.peek();
            sqlSource.addTable(table);
            if (verbose) {
                out.writeln("+++++++++++++++++>Recognised table: " + tableName + " within unit " + source.getName());
            }
        }
    }

    private Map<String, Column> parseColumns(Tokeniser tk, TokenType exitTkn) throws Exception {
        Map<String, Column> columnList = null;
        if (skipUntil(tk, TokenType.OPEN_BRACK, exitTkn)) {
            columnList = new HashMap<>();
            while (tk.hasMoreTokens()) {
                String colName = tk.nextToken().getStrVal(); // get the name
                StringBuilder colType = new StringBuilder(tk.nextToken().getStrVal()); // get the column type

                Token lookAhead = tk.lookAhead();
                if (lookAhead.getTknVal() == TokenType.OPEN_BRACK) { // if this is of the type
                    // number(2) or number(2, 3) etc, then read it in its entirety into col type string buffer
                    Token tkn = tk.nextToken(); // consume the open bracket token
                    while (tk.hasMoreTokens() && tkn.getTknVal() != TokenType.CLOSE_BRACK) {
                        colType.append(tkn.getStrVal()); // include the current token into coltype
                        tkn = tk.nextToken();
                    }
                    colType.append(tkn.getStrVal()); // append the close bracket
                }

                Column column = new Column(colName, colType.toString());
                columnList.put(colName, column);

                Set<TokenType> endSet = new HashSet<>();
                endSet.add(TokenType.SEMI);
                endSet.add(TokenType.SEPARATOR);
                if (!skipUntilSeq(tk, new TokenType[]{TokenType.COMMA, TokenType.ID}, endSet)) {
                    break;
                }
            }
        }
        return columnList;
    }

    private void parseTableAlterClause(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            Token tkn = tk.nextToken();
            String tableName = tkn.getStrVal();
            tkn = tk.nextToken();
            if (tkn.getTknVal() == TokenType.ADD && tk.hasMoreTokens()) {
                tkn = tk.nextToken();
                if (tkn.getTknVal() == TokenType.CONSTRAINT) {
                    parseConstraintDefinition(tableName, tk);
                }
            }
        }
    }

    private void parseViewDefinition(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            String viewName = tk.nextToken().getStrVal();
            View view = new View(viewName);
            SqlSource source = (SqlSource) stack.peek();
            source.addView(view);
            if (tk.lookAhead().getTknVal() == TokenType.OPEN_BRACK) { // view definition defines the column names
                Token tkn = tk.nextToken(); // consume the open bracket
                tkn = tk.nextToken(); // read the column name
                while (tkn.getTknVal() != TokenType.CLOSE_BRACK) { // process column names between the brackets
                    if (tkn.getTknVal() != TokenType.COMMA) { // skip commas in the middle
                        String colName = tkn.getStrVal();
                        view.addColumn(colName);
                    }
                    tkn = tk.nextToken();
                }
            } else if (tk.lookAhead().getTknVal() == TokenType.AS) { // view definition does not include column
                // names, then this needs to be inferred from the column projections from the underlying select
                // statement
                Token tkn = tk.nextToken();
                if (tk.lookAhead().getTknVal() == TokenType.SELECT) { // check if the next statement is a select
                    // view columns are not explicitly defined
                    // and need to be inferred from the select columns
                    tkn = tk.nextToken(); // advance to the SELECT token
                    while (tkn.getTknVal() != TokenType.FROM) { // process columns from the select until from
                        if (tk.lookAhead().getTknVal() == TokenType.COMMA) {
                            String colName = tkn.getStrVal(); // get the column name
                            view.addColumn(colName);
                            tk.nextToken(); // consume the comma
                        } else if (tk.lookAhead().getTknVal() == TokenType.FROM) {
                            String colName = tkn.getStrVal(); // get the column name
                            view.addColumn(colName);// add the column name
                            // don't consume the FROM token as it would signal the end of processing
                            // for the outer loop
                        } else { // this could be a function invocation, hence check for procedure call
                            if (tkn.getTknVal() == TokenType.ID && checkProcedureCall(tkn.getStrVal(), tk)) {
                                String currentToken = tkn.getStrVal();
                                TokenType tknVal = tk.lookAhead().getTknVal();
                                if (tknVal == TokenType.OPEN_BRACK) {
                                    Map<String, Procedure> procMap = new HashMap<String, Procedure>();
                                    int paramCount = countTknAndCheckProcedureCall(procMap, tk, TokenType.COMMA, TokenType.CLOSE_BRACK);
                                    if (verbose) {
                                        out.writeln("--> Adding reference to procedure#paramcount: " + currentToken + "#" + paramCount);
                                    }
                                    source.addInvocationRef(currentToken + "#" + paramCount, null);
                                    source.addInvocationRefs(procMap);
                                    tk.nextToken(); // consume the CLOSE_BRACK token
                                } else {
                                    if (verbose) {
                                        out.writeln("--> Adding reference to procedure#paramcount: " + currentToken + "#0");
                                    }
                                    source.addInvocationRef(currentToken + "#" + 0, null);
                                }
                            }
                        }
                        tkn = tk.nextToken(); // this would be a column name if the next token is a comma
                    }
                }
            }
            if (tk.getCurrentToken().getTknVal() != TokenType.FROM) {
                Map<String, Procedure> procMap = skipCheckProcedureCall(tk, TokenType.FROM); // start scanning 
                // 	select stmt for "FROM" and capture any function references during the skip
                view.addInvocationRefs(procMap); // keep adding the function references
            }
            Token nextTok = tk.nextToken();
            while (nextTok.getTknVal() != TokenType.EOF && nextTok.getTknVal() != TokenType.SEMI) { // just look for FROM statements and read table names following it
                if (nextTok.getTknVal() == TokenType.SELECT) { // Nested select statement
                    Map<String, Procedure> procMap = skipCheckProcedureCall(tk, TokenType.FROM); // start scanning select stmt for "FROM" and
                    // capture any function references during the skip
                    view.addInvocationRefs(procMap); // keep adding the function references
                    nextTok = tk.nextToken();
                } else if (nextTok.getTknVal() == TokenType.FROM && tk.lookAhead().getTknVal() == TokenType.ID) { // an identifier following a from clause
                    // is likely to be a table name, so consume it and add it to the table list
                    nextTok = tk.nextToken(); // consume the FROM token and move to the next token
                    while (nextTok.getTknVal() != TokenType.WHERE && nextTok.getTknVal() != TokenType.CLOSE_BRACK
                            && nextTok.getTknVal() != TokenType.OPEN_BRACK && nextTok.getTknVal() != TokenType.UNION
                            && nextTok.getTknVal() != TokenType.SEMI && nextTok.getTknVal() != TokenType.EOF) { // ensure that it is not 
                        // one of where, union, where or another open bracket and loop through to collect
                        // table names. The side effect of this is that, if a select statement has a form like this:
                        //		SELECT constraintID, name 
                        //      FROM MktConstraint mk;
                        // then this piece of logic will create a table reference form mk as well. This can be
                        // eliminated later during the table rationalisation process, where an access is only valid
                        // if a table exists with that name
                        if (nextTok.getTknVal() != TokenType.COMMA) { // skip the comma
                            view.addTable(nextTok.getStrVal()); // add the table
                            // reference to the object on the top of the stack which corresponds to the currently
                            // processed function, procedure or trigger.
                        }
                        nextTok = tk.nextToken();
                    }
                } else {
                    if (nextTok.getTknVal() == TokenType.ID && checkProcedureCall(nextTok.getStrVal(), tk)) {
                        String currentToken = nextTok.getStrVal();
                        TokenType tknVal = tk.lookAhead().getTknVal();
                        if (tknVal == TokenType.OPEN_BRACK) {
                            Map<String, Procedure> procMap = new HashMap<String, Procedure>();
                            int paramCount = countTknAndCheckProcedureCall(procMap, tk, TokenType.COMMA, TokenType.CLOSE_BRACK);
                            if (verbose) {
                                out.writeln("--> Adding reference to procedure#paramcount: " + currentToken + "#" + paramCount);
                            }
                            source.addInvocationRef(currentToken + "#" + paramCount, null);
                            source.addInvocationRefs(procMap);
                            tk.nextToken(); // consume the CLOSE_BRACK token
                        } else {
                            if (verbose) {
                                out.writeln("--> Adding reference to proceure#paramcount: " + currentToken + "#0");
                            }
                            source.addInvocationRef(currentToken + "#" + 0, null);
                        }
                    }
                    nextTok = tk.nextToken(); // get the next token
                }
            }
        }
    }

    private void parseTriggerDefinition(Tokeniser tk) throws Exception {
        if (tk.hasMoreTokens()) {
            String triggerName = tk.nextToken().getStrVal();
            skipUntil(tk, TokenType.ON);
            String tableOrViewName = tk.nextToken().getStrVal();
            Trigger trigger = new Trigger(triggerName, tableOrViewName);
            SqlSource source = (SqlSource) stack.peek();
            source.addTrigger(trigger);
            stack.push(trigger); // push the trigger object and treat this like a procedure.
            if (verbose) {
                out.writeln(">>>>>>>>>>>>>>>>>Pushing: " + trigger.getName());
            }
            //List<TriggerCondition> tgList = parseTriggerCondition(tk);
        }
    }

    private void parseConstraintDefinition(String tableName, Tokeniser tk) throws Exception {
        Token tok = tk.nextToken();
        String constraintName = tok.getStrVal();
        Constraint constraint = new Constraint(constraintName);
        SqlSource source = (SqlSource) stack.peek();
        source.addConstraint(constraint);
        constraint.setSourceTable(tableName);
        tok = tk.nextToken();
        if (tok.getTknVal() == TokenType.FOREIGN) {
            constraint.setConstraintType(ConstraintType.FK);
        } else if (tok.getTknVal() == TokenType.PRIMARY) {
            constraint.setConstraintType(ConstraintType.PK);
        } else {
            constraint.setConstraintType(ConstraintType.UNIQUENESS);
        }
        tk.nextToken(); // consume the keyword "key"
        tok = tk.nextToken();
        if (tok.getTknVal() == TokenType.OPEN_BRACK) {
            tok = tk.nextToken(); // consume open bracket
            while (tok.getTknVal() != TokenType.CLOSE_BRACK) {
                if (tok.getTknVal() != TokenType.COMMA) {
                    constraint.addSourceColumn(tok.getStrVal());
                }
                tok = tk.nextToken();
            }
        }
        if (tk.lookAhead().getTknVal() == TokenType.REFERENCES) {
            tk.nextToken(); // consume the keyword "references"
            tok = tk.nextToken();
            String targetTableName = tok.getStrVal();
            constraint.setTargetTable(targetTableName);
            tok = tk.nextToken();
            if (tok.getTknVal() == TokenType.OPEN_BRACK) {
                tok = tk.nextToken(); // consume open bracket
                while (tok.getTknVal() != TokenType.CLOSE_BRACK) {
                    if (tok.getTknVal() != TokenType.COMMA) {
                        constraint.addTargetColumn(tok.getStrVal());
                    }
                    tok = tk.nextToken();
                }
            }
        }
    }

    public void parse() throws Exception {
        Tokeniser tk = new Tokeniser(in, out.getWriter());
        tk.setLookAheadLimit(2);
        tk.setVerbose(verbose);
        while (tk.hasMoreTokens()) {
            Token token = tk.nextToken();
            parseToken(tk, token);
        }
    }

    private void processCase(Tokeniser tk) throws Exception {
        if (tk.lookAhead().getTknVal() != TokenType.SEMI) {
            stack.peek().incBeginCount();
            if (verbose) {
                out.writeln("=========>BeginCount: " + stack.peek().getName() + ":" + stack.peek().getBeginCount());
            }
        }
    }

    private void processLoop(Tokeniser tk) throws Exception {
        if (tk.lookAhead().getTknVal() != TokenType.SEMI) {
            stack.peek().incBeginCount();
            if (verbose) {
                out.writeln("=========>BeginCount: " + stack.peek().getName() + ":" + stack.peek().getBeginCount());
            }
        }
    }

    private void processBegin(Tokeniser tk) throws Exception {
        stack.peek().incBeginCount();
        if (verbose) {
            out.writeln("=========>BeginCount: " + stack.peek().getName() + ":" + stack.peek().getBeginCount());
        }
    }

    private void processIf(Tokeniser tk) throws Exception {
        if (tk.lookAhead().getTknVal() != TokenType.SEMI) {
            stack.peek().incBeginCount();
            if (verbose) {
                out.writeln("=========>BeginCount: " + stack.peek().getName() + ":" + stack.peek().getBeginCount());
            }
        }
    }

    private void processEnd(Tokeniser tk) throws Exception {
        Token la = tk.lookAhead();
        if (la.getTknVal() != TokenType.AS && la.getTknVal() != TokenType.EQUAL && la.getTknVal() != TokenType.GREAT
                && la.getTknVal() != TokenType.LESS) {
            stack.peek().decBeginCount();
            if (verbose) {
                out.writeln("=========>BeginCount: " + stack.peek().getName() + ":" + stack.peek().getBeginCount());
            }
            if (stack.size() > 1 && stack.peek().getBeginCount() == 0) {
                if (verbose) {
                    out.writeln("++++++++++++++++>Popping: " + stack.peek().getName());
                }
                stack.pop();
            }
            if (tk.lookAhead(1).getTknVal() == TokenType.ID
                    && tk.lookAhead(2).getTknVal() == TokenType.SEMI) { // end of the package or procedure
                tk.nextToken(); // consume identifier
                tk.nextToken(); // consume semicolon
            }
        }
    }

    private void parseToken(Tokeniser tk, Token token) throws Exception {
        TokenType tokenVal = token.getTknVal();
        switch (tokenVal) {
            // do nothing for these keywords and skip them
            case OR:
            case RETURN:
                break;
            case COMMIT:
                break;

            case FUNCTION:
                parseFunction(tk);
                break;

            case DROP:
                skipUntil(tk, TokenType.SEMI);
                break;

            case GRANT:
                skipUntil(tk, TokenType.SEMI);
                break;

            case PROCEDURE:
                parseProcedure(tk);
                break;

            case CASE:
                processCase(tk);
                break;

            case LOOP:
                processLoop(tk);
                break;

            case BEGIN:
                processBegin(tk);
                break;

            case IF:
                processIf(tk);
                break;

            case END:
                processEnd(tk);
                break;

            case SELECT: // parse table access references from select, insert, delete and update statements
            case INSERT:
            case DELETE:
            case UPDATE:
                parseTableAccess(tk, token);
                break;

            case CREATE:
            case GLOBAL:
            case TEMPORARY:
            case REPLACE:
            case FORCE:
                if (tk.hasMoreTokens()) {
                    Token tkn = tk.lookAhead();
                    switch (tkn.getTknVal()) {
                        case TABLE:
                            tk.nextToken(); // consume the token
                            parseTableDefinition(tk);
                            break;
                        case VIEW:
                            tk.nextToken(); // consume the token
                            parseViewDefinition(tk);
                            break;
                        case TRIGGER:
                            tk.nextToken(); // consume the token
                            parseTriggerDefinition(tk);
                            break;
                        case PACKAGE:
                            tk.nextToken(); // consume the token
                            // peek the token stream to see if the next token is the keyword body
                            if (tk.lookAhead().getTknVal() == TokenType.BODY) {
                                tk.nextToken(); // consume the body keyword by calling next token
                                parsePackage(tk); // pass the tokeniser for parsing
                            } else {
                                skipUntil(tk, TokenType.END); // go to the end of the package spec
                                skipUntil(tk, TokenType.SEMI); // then go to the semicolon following it.
                            }
                            break;
                    }
                }
                break;

            case ALTER:
                if (tk.hasMoreTokens()) {
                    switch (tk.nextToken().getTknVal()) {
                        case TABLE:
                            parseTableAlterClause(tk);
                            break;
                    }
                }
                break;

            case ID: // if it is an identifier then check if this is a procedure or function call invocation
                String currentToken = token.getStrVal().trim();
                if (checkProcedureCall(currentToken, tk)) {
                    AccessReferencer referencer = (AccessReferencer) stack.peek();
                    Map<String, Procedure> procMap = new HashMap<>();
                    TokenType tknVal = tk.lookAhead().getTknVal();
                    if (tknVal == TokenType.OPEN_BRACK) {
                        int paramCount = countTknAndCheckProcedureCall(procMap, tk, TokenType.COMMA, TokenType.CLOSE_BRACK);
                        if (verbose) {
                            out.writeln("--> Adding reference to procedure#paramcount: " + currentToken + "#" + paramCount);
                        }
                        referencer.addInvocationRef(currentToken + "#" + paramCount, null);
                        referencer.addInvocationRefs(procMap);
                    } else {
                        if (verbose) {
                            out.writeln("--> Adding reference to procedure#paramcount: " + currentToken + "#0");
                        }
                        referencer.addInvocationRef(currentToken + "#" + 0, null);
                    }
                }
                break;

            default: // if it does not fit into any of the above categories, then it is not of interest to us
                break;

        }
    }

    private static void showUsage(PrintStream stream) {
        stream.println("Usage: RE <resources-dir>");
        stream.println("<resources-dir> should contain the follwing files:");
        stream.println("1. parser.properties");
        stream.println("2. logging.properties (optional)");
        stream.println("3. keywords.txt - contains all the PL/SQL keywords and is used by the parser");
        stream.println("parser.properties should contain the following properties defined (examples shown):");
        stream.println("sourceDir=/a/b/input");
        stream.println("outputDir=/a/b/output");
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            showUsage(System.err);
            System.exit(-1);
        }
        String resourcesDir = args[0];

        long start = System.currentTimeMillis();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(resourcesDir + "/parser.properties");
            Properties properties = new Properties();
            properties.load(fis);

            String sourceDir = properties.getProperty("SourceDir", null);
            if (sourceDir == null) {
                throw new IllegalArgumentException("SourceDir property not defined in parser.properties");
            }

            String outputDir = properties.getProperty("OutputDir", "/tmp");

            Set<String> packageNames = new HashSet<>();
            DirectoryScanner scanner = new DirectoryScanner(sourceDir);
            Schema schema = new Schema();

            String includeFilters = properties.getProperty("IncludeFilters", null);
            String excludeFilters = properties.getProperty("ExcludeFilters", null);

            StringTokenizer tokeniser;
            if (includeFilters != null) {
                tokeniser = new StringTokenizer(includeFilters, ",");
                while (tokeniser.hasMoreTokens()) {
                    scanner.addIncludeFilter(tokeniser.nextToken());
                }
            }

            if (excludeFilters != null) {
                tokeniser = new StringTokenizer(excludeFilters, ",");
                while (tokeniser.hasMoreTokens()) {
                    scanner.addExcludeFilter(tokeniser.nextToken());
                }
            }

            String verbose = properties.getProperty("Verbose", "off");
            boolean isVerbose = "on".equalsIgnoreCase(verbose);

            String parseOnly = properties.getProperty("ParseOnly", "false");
            boolean isParseOnly = ("true".equalsIgnoreCase(parseOnly) || "no".equalsIgnoreCase(parseOnly));
            Set<File> inputSet = scanner.scan();

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, String.format("Processing %d files under directory %s", inputSet.size(), scanner.getSource()));
            }
            
            if (inputSet.isEmpty()) {
                throw new IOException("Cannot find files ending with .pkb .prc .trg or .sql in directory");
            }

            // create all the package names and use it as input to the parser. So when symbol references
            // for procedure invocations are added, the parse can validate if it is a qualified name
            // of a procedure or function invocation before adding them into the list of references.
            // This will significantly reduce the number of symbols processed during the actual resolution
            // process
            for (Iterator<File> fileIterator = inputSet.iterator(); fileIterator.hasNext();) {
                File file = fileIterator.next();
                String fileName = file.getName();
                packageNames.add(fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase());
            }

            int count = 1;
            for (Iterator<File> fileIterator = inputSet.iterator(); fileIterator.hasNext();) {
                File file = fileIterator.next();
                String fileName = file.getAbsolutePath();

                File outDir = new File(outputDir);
                if (!outDir.exists()) {
                    outDir.mkdir();
                }
                Parser parser = new Parser(file, resourcesDir, outputDir);
                parser.setPackageSet(packageNames);
                parser.setVerbose(isVerbose);
                if(logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, String.format("Processing file %d of %d => %s", count, inputSet.size(), fileName));
                }
                count++;
                try {
                    parser.parse();
                } catch (Exception e) { //print stack trace and continue with the rest
                    e.printStackTrace();
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.log(Level.SEVERE, "Error: {0}", e.getLocalizedMessage());
                    }
                    return; // exit

                }
                SqlSource sqlSource = parser.getParseTree();
                schema.addDBObject(sqlSource); // complete the parsing of the input set fully
            }
            schema.setOutputDir(outputDir);
            schema.rationaliseReferences();
            if (isParseOnly) {
                schema.writeToXML();
            } 
            else {
                UmlModelBuilder builder = null;
                FileWriter outputWriter = null;
                try {
                    builder = new UmlModelBuilder();

                    builder.buildModel(schema);
                    builder.buildReferences(schema);

                    File outputFile = new File (outputDir + "/model.puml");
                    outputWriter = new FileWriter(outputFile);
                    outputWriter.write(builder.toUml());

                } 
                catch (UnsatisfiedLinkError ule) {
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.log(Level.SEVERE, "Error: {0}", ule.getLocalizedMessage());
                    }
                    ule.printStackTrace();
                } 
                catch (Exception e) {
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.log(Level.SEVERE, "Error: {0}", e.getLocalizedMessage());
                    }
                    e.printStackTrace();
                }
                finally {
                    if(outputWriter != null) {
                        outputWriter.close();
                    }
                }
            }
        }
        catch (FileNotFoundException fe) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "FileNotFoundException caught: {0}", fe.getLocalizedMessage());
            }
        } 
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if(logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "Processing completed in {0}s ", elapsed / 1000);
        }
    }
}
