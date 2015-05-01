public class messyWrenRDP extends RDP implements WrenTokens {
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
    //on/off switch for debug statements
    private void debug(String message) {
        if (getDebugShow() == true) System.out.println(message);
    }
    public messyWrenRDP(String input) {
    //setDebugShow(true); //will have debug statements
    setDebugShow(false); //will not have debut statements

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
        //moved all function calls and matches outside if statements
        decseq();
        match(BEGIN_TOK);
        commandseq();
        match(END_TOK);
        //removed all if statements to handle block without them
        /*
        if (currTok == BEGIN_TOK) {
            match(BEGIN_TOK);
            debug(" begin tok matched");
        }
        commandseq();
        if (currTok == END_TOK){
             match(END_TOK); 
             debug(" end tok matched");
        }
        else error("block");
        */
    }
    //what do we do about lambda?
    private void decseq() {
        if (currTok == VAR_TOK){ //could be variable
            dec();
            decseq();
        }
       // else error("decseq");  //caused error
        //attempt to figure out lambdaif (currTok == ASSIGN_TOK) match(ASSIGN_TOK);
        //lambda means anything = vartok?
    }

    private void dec() {
        //w didnt have if so removed
        //if (currTok == VAR_TOK) {
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
    //    }
    //    else error("dec");
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
        //2 changed again including compares for semi in commandseq
        //2 and removed call to commandseq2
        //2 commandseq2();
        //2 debug(" commandseq2 called in commandseq");
        //1 w removed all comparisons of semi in commandseq
        //2 reinstalled compare to semi tok in commandseq
        if (currTok == SEMICOLON_TOK){

            match(SEMICOLON_TOK);
            debug(" semicolon tok matched in commandseq");
            debug("call commandseq from commandseq");
            commandseq();
        }
        //commented out error to inspect error 21
        //else error("commandseq");
    }
    private void commandseq2() {
        //command();    //w rem
       // debug(" command called"); removed w/command above
        if (currTok == SEMICOLON_TOK) { 
            match(SEMICOLON_TOK);
            debug(" semicolon tok matched in commandseq2");
            commandseq2(); //w adjusted to inside compare
            command(); //w adjusted to inside compare
        }
        //removed these because it didnt help 
        //w put these inside the compare for semicolontok   
        //commandseq2();  //w add reversed w/ method under
        //command();  //w add
        debug(" command called in commandseq2");
    }

    private void command() {
        if (currTok == VARIABLE_TOK){ //w used var tok compare to call assign
            assign();
            debug(" assign called in command");
            debug(Integer.toString(currTok));
        }
        else if (currTok == SKIP_TOK){ //changed if to else if to compare
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
            //added lines outside to conform to working if logic
            match(DO_TOK);
            debug(" do tok matched in command");
            //added because do needs to call a command seq after it 
            //gets a bool expression
            commandseq();
            //1 added if statement to check if do or end is read during a while loop
            //removed, do has to be seen in correct syntax of loop
            //if (currTok == DO_TOK){
            //    match(DO_TOK);
            //    debug(" do tok matched call commandseq");
            //    commandseq();
            //}
            //left if here because end could not be seen right after command, while loop could keep going
            if (currTok == END_TOK){
                match(END_TOK);
                debug(" end tok matched in command");
                match(WHILE_TOK);
                debug(" while tok matched in command");
            }
            //added else because logically if loop continues command sequence needs to be handled again
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
        // 1last ditch effort to catch any ends i possibly could be missing elsewhere for if and while statements.
        //removed did no good
        //else if (currTok == END_TOK){
        //    match(END_TOK);
        //    commandseq();
       // }
        else error("command"); 
    }

    private void assign(){
       // if (currTok == VARIABLE_TOK){ //removed to avoid stackoverflow by duplicate comparison in command when called
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
       // } //end bracket for removed if statement that caused stackover
        else error("assign");
    }
    private void assign2() {
        //dont know what this should be doing
//        if (currTok == VARIABLE_TOK){
            //match(COLON_TOK);
            //match(EQ_TOK);
            //match(COLON_TOK);
//            match(BOOLASSIGN_TOK);
//            boolexpr();
//        }
//        else error("assign2");

    }

    private void if2() {
        //w add all
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
        //1 w didnt have so pulled if state
        //2 replaced to see what would happen
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
        //1 w didnt have so pulled if statei
        //2 replaced to see what would happen
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
            strong_op();//w change location
            debug(" intelement called from intterm2");
            intelement();
            debug(" intterm2 called from intterm2");
            intterm2();//w change location
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
            // attempt to handle bool vs int discrepancy from variable
          // if (currTok == BOOL_TOK) {
          //     match(BOOL_TOK);
           //    debug("Bool tok matched running boolterm");
           //    boolterm();
          //  }
         //   else if (currTok == INT_TOK){
         //       match(INT_TOK);
         //       debug("int tok matched running intterm");
         //       intterm();
         //   }
         //   else error("matching int and bool problem");
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
        //added all if statements that compare bool element
        //if (currTok == TRUE_TOK || currTok == FALSE_TOK || currTok == NOT_TOK){
            //1 reversed order and removed call to boolexpr2
            //2 undid because it didnt work
            debug(" boolterm called from boolexpr");
            boolterm();
            debug(" boolexpr2 called from boolexpr");
            boolexpr2();
            //2 removed because didnt work
            //boolexpr();
            //match(OR_TOK);
            //boolterm();
       // }
    }
    //removed call to boolexpr2
    private void boolexpr2() {
        //boolexpr();
        //w add if statement
        //changed the first compare to or from true
        if (currTok == OR_TOK || currTok == FALSE_TOK || currTok == NOT_TOK){
            match(OR_TOK);
            debug("or tok matched in boolexpr2");
            debug(" boolterm called from boolexpr2");
            boolterm();
            debug(" boolexpr2 called from boolexpr2");
            boolexpr2();
        }
        //boolterm();
    }
    private void boolterm() {
        debug(" boolelement called from boolterm");
        boolelement();
        boolterm2();
    }
    private void boolterm2() {
        //c says change to or and inconst
       // if (currTok == TRUE_TOK || currTok == FALSE_TOK || currTok == NOT_TOK){
        if (currTok == OR_TOK || currTok == AND_TOK || currTok == INTCONST_TOK){
            //boolterm(); //w relocate
            //c says match current token instead of and token
            //match(AND_TOK);
            //debug("and tok matched in boolterm");
            match(currTok);
            //debug to see since were not calling a particular token
            debug(Integer.toString(currTok));
            debug(" match current token");
            debug(" boolement called from boolterm");
            boolelement();
            debug(" boolterm2 called from boolterm");
            boolterm2();    // w relocated here
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
	    // jbf : VARIABLE ALONE OR COMPARISON, PREDICTION PROBLEM
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
