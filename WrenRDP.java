public class WrenRDP extends RDP implements WrenTokens {
    private boolean debugShow;
    public void setDebugShow(boolean debugShow){
        this.debugShow = debugShow;
    }
    public boolean getDebugShow(){
        return debugShow;
    }
    public static void main(String args[]) {
    String input = "";
    if (args.length == 0) {
        System.out.println("Need program-string arg.");
        System.out.println("Example use: java WrenRDP \"`cat test.w`\"");
        System.exit(1);
    }
    for (String s : args)
        input = input + s + "\n";

    System.out.println(input);
    WrenRDP p = new WrenRDP(input);
    p.parse();
    }
    private void debug(String message) {
        if (getDebugShow() == true) System.out.println(message);
    }
    public WrenRDP(String input) {
    setDebugShow(false); 
    lexer = new WrenLexer(input);
    }
    public void StartSymbol() {
    program();
    }
    private void program() {
       match(PROG_TOK);
       debug(" prog tok matched in program");
       match(VARIABLE_TOK);
       debug(" variable tok matched in program");
       match(IS_TOK);
       debug(" is tok matched in program");
       debug("  block called from program");
       block();
    }
    private void block() {
        
        decseq();
        match(BEGIN_TOK);
        commandseq();
        match(END_TOK);
    }

    private void decseq() {
        if (currTok == VAR_TOK){ 
            dec();
            decseq();
        }
    }

    private void dec() {
            match(VAR_TOK); 
            debug(" var tok matched in dec");
            debug("  varlist called in dec");
            varlist();
            match(COLON_TOK);
            debug(" colon tok matched in dec");
            debug("  type called in dec");
            type();
            match(SEMICOLON_TOK);
            debug(" semicolon tok matched in dec");
     }
    
    private void type() {
        if (currTok == INT_TOK) {
            match(INT_TOK);
            debug(" march int tok in type");
        }
        else if (currTok == BOOL_TOK) {
             match(BOOL_TOK);
             debug(" match book tok in type");
        }
        else error("type");
    }
    private void varlist() {
        if (currTok == VARIABLE_TOK){
             match(VARIABLE_TOK);
             debug(" variable tok matched in varlist");
             if (currTok == COMMA_TOK){ 
                 match(COMMA_TOK);
                 debug(" comma tok matched in varlise");
                 debug(" varlist called from varlist");
                 varlist();
             } 
        }
        else error("varlist");
    }

    private void varlist2() {
        if (currTok == VARIABLE_TOK){
         match(VARIABLE_TOK);
         debug(" variable tok matched in varlist2");
        match(COMMA_TOK);
        debug(" comma tok matched in varlist2");
        debug(" varlist called from varlist2");
        varlist();
        }
    }
    private void commandseq() {
        command();
        debug(" command called from commandseq");
        debug(Integer.toString(currTok));
        if (currTok == SEMICOLON_TOK){

            match(SEMICOLON_TOK);
            debug(" semicolon tok matched in commandseq");
            debug("call commandseq from commandseq");
            commandseq();
        }
    }
    private void commandseq2() {
        if (currTok == SEMICOLON_TOK) { 
            match(SEMICOLON_TOK);
            debug(" semicolon tok matched in commandseq2");
            commandseq2(); 
            command(); 
        }
        debug(" command called in commandseq2");
    }

    private void command() {
        if (currTok == VARIABLE_TOK){ 
            assign();
            debug(" assign called in command");
            debug(Integer.toString(currTok));
        }
        else if (currTok == SKIP_TOK){ 
             match(SKIP_TOK);
             debug(" skip tok matched in command");
        }
        else if (currTok == READ_TOK){
            match(READ_TOK);
            debug(" match read tok in command");
            match(VARIABLE_TOK);
            debug(" match var tok in command");
        }
        else if (currTok == WRITE_TOK){
            match(WRITE_TOK);
            debug(" match write in command");
            debug(" call intexpr from command");
            intexpr();
        }
        else if (currTok == WHILE_TOK){
            match(WHILE_TOK); 
            debug(" while tok matched in command");
            debug(" call boolexpr from command");
            boolexpr();
            match(DO_TOK);
            debug(" do tok matched in command");
            commandseq();
            if (currTok == END_TOK){
                match(END_TOK);
                debug(" end tok matched in command");
                match(WHILE_TOK);
                debug(" while tok matched in command");
            }
            
            else if (currTok != END_TOK){
                commandseq();
                match(END_TOK);
                debug(" end tok matched in while loop in command");
                match(WHILE_TOK);
                debug(" while tok matched at end of while loop in command");
            }
        } 
        else if (currTok == IF_TOK){
            match(IF_TOK);
            debug(" if tok matched in command");
            debug(" call boolexpr from command");
            boolexpr();
            match(THEN_TOK);
            debug(" then tok matched in command");
            debug("  commandseq called from command");
            commandseq();
            if (currTok == END_TOK){
                match(END_TOK);
                debug(" end tok matched in command");
                match(IF_TOK);
                debug(" if tok matched in command");
            }
            else if (currTok == ELSE_TOK){
                match(ELSE_TOK);
                debug(" else tok matched");
                debug("  call commandseq from command");
                commandseq();
                match(END_TOK);
                debug(" End tok matched in command");
                match(IF_TOK);
                debug(" if tok matched in command");
            }
            else error("problem in if statements");
        }
        else error("command"); 
    }

    private void assign(){
            match(VARIABLE_TOK);
            debug(" variable tok matched in assign");
             if (currTok == INTASSIGN_TOK) {
                 match(INTASSIGN_TOK);
                 debug("intassign tok matched");
                 debug("intexpr called from assign");
                 intexpr();
             }
             else if (currTok == BOOLASSIGN_TOK) {
                 match(BOOLASSIGN_TOK);
                 debug("boolassign tok matched in assign");
                 debug(Integer.toString(currTok));
                 debug("  running boolexpr from assign");
                 boolexpr();
            }
       
        else error("assign");
    }

    private void if2() {
        
        match(IF_TOK);
        boolexpr();
        match(THEN_TOK);
        commandseq();
        match(ELSE_TOK);
        commandseq();
        match(EQ_TOK);
        match(IF_TOK);
    }

    private void intexpr() {
        if (currTok == INTCONST_TOK || currTok == VARIABLE_TOK ||
            currTok == LPAR_TOK || currTok == MINUS_TOK){
                debug(" intterm called from intexpr");
                intterm();
                 debug(" intexpr2 called from intexpr");
                intexpr2();
        }
    }
    
    private void intexpr2() {
        if (currTok == PLUS_TOK || currTok == MINUS_TOK){
            debug(" weak op called from intexpr2");
            weak_op();
            debug(" intterm called from intexpr2");
            intterm();
            debug(" intexpr2 called from intexpr2");
            intexpr2();
        }
    }
    
    private void intterm() {
        if (currTok == INTCONST_TOK || currTok == VARIABLE_TOK ||
            currTok == LPAR_TOK || currTok == MINUS_TOK){
                debug(" intelement called from intterm");
                intelement();
                debug(" intterm2 called from intterm");
                intterm2();
        }
    }
    
    private void intterm2() {
        if (currTok == MUL_TOK || currTok == DIV_TOK) {
            debug(" strong op called from intterm2");
            strong_op();
            debug(" intelement called from intterm2");
            intelement();
            debug(" intterm2 called from intterm2");
            intterm2();
        }
    }
    private void strong_op() {
        if (currTok == MUL_TOK) {
            match(MUL_TOK);
            debug(" mul tok matchedi in strong op");
        }
        else if (currTok == DIV_TOK) {
            match(DIV_TOK);
            debug(" Div tok matched in strong op");
        }
        else error("strong op");
    }
    private void weak_op() {
        if (currTok == PLUS_TOK){
             match(PLUS_TOK);
             debug(" plus tok matched in weak op");
        }
        else if (currTok == MINUS_TOK){
             match(MINUS_TOK);
             debug(" minus tok matched in weak op");
        }
        else error("weak op");
    }
    private void intelement() {
        if (currTok == INTCONST_TOK){
             match(INTCONST_TOK);
             debug("intconst matched in intelement"); 
        }
        else if (currTok == VARIABLE_TOK) { 
            match(VARIABLE_TOK);
            debug("Variable tok matched in intelement");
        }
        else if (currTok == LPAR_TOK) {
            match(LPAR_TOK);
            debug("lpar tok matched in intelement");
            debug("intepr called from intelement");
            intexpr();
            match(RPAR_TOK);
            debug("rpar tok matched in intelement");
        }
        else if (currTok == MINUS_TOK){
            match(MINUS_TOK);
            debug("minus matched in intelement");
            debug(" intelement called from intelement");
             intelement();
             }
        else error("intelement");
    }
    private void boolexpr() {
        debug(Integer.toString(currTok));
            debug(" boolterm called from boolexpr");
            boolterm();
            debug(" boolexpr2 called from boolexpr");
            boolexpr2();
    }

    private void boolexpr2() {
        if (currTok == OR_TOK || currTok == FALSE_TOK || currTok == NOT_TOK){
            match(OR_TOK);
            debug("or tok matched in boolexpr2");
            debug(" boolterm called from boolexpr2");
            boolterm();
            debug(" boolexpr2 called from boolexpr2");
            boolexpr2();
        }
    }
    private void boolterm() {
        debug(" boolelement called from boolterm");
        boolelement();
        boolterm2();
    }
    private void boolterm2() {
        if (currTok == OR_TOK || currTok == AND_TOK || currTok == INTCONST_TOK){
            match(currTok);
            debug(Integer.toString(currTok));
            debug(" match current token");
            debug(" boolement called from boolterm");
            boolelement();
            debug(" boolterm2 called from boolterm");
            boolterm2();    
        }
    }
    private void relation() {
        if (currTok == LE_TOK) match(LE_TOK);
        else if (currTok == NE_TOK) match(NE_TOK);
        else if (currTok == GE_TOK) match(GE_TOK);
        else if (currTok == EQ_TOK) match(EQ_TOK);
        else if (currTok == GT_TOK) match(GT_TOK);
        else if (currTok == LT_TOK) match(LT_TOK);
        else error("relation");
    }
    
    private void boolelement() {
    if (currTok == TRUE_TOK) match(TRUE_TOK);
    else if (currTok == FALSE_TOK) match(FALSE_TOK);
    else if (currTok == NOT_TOK) {
        match(NOT_TOK);
        match(LBRACK_TOK);
        boolexpr();
        match(RBRACK_TOK);
    }
    else if (currTok == LBRACK_TOK) {
        match(LBRACK_TOK);
        boolexpr();
        match(RBRACK_TOK);
    }
    else if (currTok == VARIABLE_TOK) {
        
        currTok = lexer.getToken();
        switch (currTok) {
        case LT_TOK : case LE_TOK: case GT_TOK: 
        case GE_TOK: case EQ_TOK: case NE_TOK:
        relation(); intexpr();
        break;
        case MUL_TOK : case DIV_TOK: 
        intterm2(); relation(); intexpr();
        break;
        case PLUS_TOK : case MINUS_TOK: 
            intexpr2(); relation(); intexpr();
        break;
        }
    }
    else if (currTok == INTCONST_TOK || currTok == LPAR_TOK || currTok == MINUS_TOK) {
        intexpr();
        relation();
        intexpr();
    }
    else error("boolelement");
    }
}
