public class WrenRDP extends RDP implements WrenTokens {
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
    public WrenRDP(String input) {
	lexer = new WrenLexer(input);
    }
    public void StartSymbol() {
	program();
    }
    private void program() {
       match(PROG_TOK);
       System.out.println(" prog tok matched");
       match(VARIABLE_TOK);
       System.out.println(" variable tok matched program");
       match(IS_TOK);
       System.out.println(" is tok matched program block called");
       block();
    }
    private void block() {
        decseq();
        if (currTok == BEGIN_TOK) {
            match(BEGIN_TOK);
            System.out.println(" begin tok matched");
        }
        commandseq();
        if (currTok == END_TOK) match(END_TOK);
        else error("block");
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
        if (currTok == VAR_TOK) {
            match(VAR_TOK); 
            System.out.println(" var tok matched in dec varlist called");
            varlist();
            match(COLON_TOK);
            System.out.println(" colon tok matched type called");
            type();
            match(SEMICOLON_TOK);
            System.out.println(" semicolon tok matched");
        }
        else error("dec");
     }
    
    private void type() {
        if (currTok == INT_TOK) {
            match(INT_TOK);
            System.out.println(" march int tok in type");
        }
        else if (currTok == BOOL_TOK) {
             match(BOOL_TOK);
             System.out.println(" match book tok in type");
        }
        else error("type");
    }
    private void varlist() {
        if (currTok == VARIABLE_TOK){
             match(VARIABLE_TOK);
             System.out.println(" variable tok matched");
             if (currTok == COMMA_TOK){ 
                 match(COMMA_TOK);
                 System.out.println(" comma tok matched varlist called");
                 varlist();
             } 
        }
        else error("varlist");
    }

    private void varlist2() {
        if (currTok == VARIABLE_TOK){
         match(VARIABLE_TOK);
         System.out.println(" variable tok matched");
        match(COMMA_TOK);
        System.out.println(" comma tok matched varlist called");
        varlist();
        }
    }
    private void commandseq() {
        command();
        System.out.println(" command called1");
        //commandseq2();
        if (currTok == SEMICOLON_TOK){

            match(SEMICOLON_TOK);
            System.out.println(" semicolon tok matched call commandseq");
            commandseq();
        }
        else error("commandseq");
    }
    private void commandseq2() {
        command();
        System.out.println(" command called");
        if (currTok == SEMICOLON_TOK) { 
            match(SEMICOLON_TOK);
            System.out.println(" semicolon tok matched");
        }   
        commandseq();
        System.out.println(" command seq called");
    }
    private void command() {
        assign();
        System.out.println(" assign called");
        if (currTok == SKIP_TOK){
             match(SKIP_TOK);
             System.out.println(" skip tok matched");
        }
        else if (currTok == READ_TOK){
            match(READ_TOK);
            System.out.println(" match read tok");
            match(VARIABLE_TOK);
            System.out.println(" match var tok");
        }
        else if (currTok == WRITE_TOK){
            match(WRITE_TOK);
            System.out.println(" matcch write call intexpr");
            intexpr();
        }
        else if (currTok == WHILE_TOK){
            match(WHILE_TOK); 
            System.out.println(" while tok matched call boolexpr");
            boolexpr();
            match(DO_TOK);
            System.out.println(" do tok matched call commandseq");
            commandseq();
            match(END_TOK);
            System.out.println(" end tok matched");
            match(WHILE_TOK);
            System.out.println(" while tok matched");
        } 
        else if (currTok == IF_TOK){
            match(IF_TOK);
            System.out.println(" if tok matched call boolexpr");
            boolexpr();
            match(THEN_TOK);
            System.out.println(" then tok matched commandseq called");
            commandseq();
            if (currTok == END_TOK){
                match(END_TOK);
                System.out.println(" end tok matched");
                match(IF_TOK);
                System.out.println(" if tok matched");
            }
            else if (currTok == ELSE_TOK){
                match(ELSE_TOK);
                System.out.println(" else tok matched call commandseq");
                commandseq();
                match(END_TOK);
                System.out.println(" End tok matched");
                match(IF_TOK);
                System.out.println(" if tok matched");
            }
            else error("problem in if statements");
        }
        else error("command"); 
    }
    private void assign(){
        if (currTok == VARIABLE_TOK){
            match(VARIABLE_TOK);
            System.out.println(" variable tok matched, checking in assign");
             if (currTok == INTASSIGN_TOK) {
                 match(INTASSIGN_TOK);
                 System.out.println("intassign tok matched, running intexpr");
                 intexpr();
             }
             else if (currTok == BOOLASSIGN_TOK) {
                 match(BOOLASSIGN_TOK);
                 System.out.println("boolassign tok matched, running boolexpr");
                 boolexpr();
            }
        }
        else error("assign");
    }
    private void assign2() {
//        if (currTok == VARIABLE_TOK){
            //match(COLON_TOK);
            //match(EQ_TOK);
            //match(COLON_TOK);
//            match(BOOLASSIGN_TOK);
//            boolexpr();
//        }
//        else error("assign2");

    }
    private void if1() {
    }
    private void if2() {
    }

    private void intexpr() {
        if (currTok == INTCONST_TOK || currTok == VARIABLE_TOK ||
            currTok == LPAR_TOK || currTok == MINUS_TOK){
                intterm();
                intexpr2();
        }
    }
    
    private void intexpr2() {
        if (currTok == PLUS_TOK || currTok == MINUS_TOK){
            weak_op();
            intterm();
            intexpr2();
        }
    }
    
    private void intterm() {
        if (currTok == INTCONST_TOK || currTok == VARIABLE_TOK ||
            currTok == LPAR_TOK || currTok == MINUS_TOK){
                intelement();
                intterm2();
        }
    }
    
    private void intterm2() {
        intterm();
        strong_op();
        intelement();
    }
    private void strong_op() {
        if (currTok == MUL_TOK) match(MUL_TOK);
        else if (currTok == DIV_TOK) match(DIV_TOK);
        else error("strong op");
    }
    private void weak_op() {
        if (currTok == PLUS_TOK) match(PLUS_TOK);
        else if (currTok == MINUS_TOK) match(MINUS_TOK);
        else error("weak op");
    }
    private void intelement() {
        if (currTok == INTCONST_TOK){
             match(INTCONST_TOK);
             System.out.println("intconst matched"); 
        }
        else if (currTok == VARIABLE_TOK) { 
            match(VARIABLE_TOK);
            System.out.println("Variable tok matched");
          // if (currTok == BOOL_TOK) {
          //     match(BOOL_TOK);
           //    System.out.println("Bool tok matched running boolterm");
           //    boolterm();
          //  }
         //   else if (currTok == INT_TOK){
         //       match(INT_TOK);
         //       System.out.println("int tok matched running intterm");
         //       intterm();
         //   }
         //   else error("matching int and bool problem");
        }
        else if (currTok == LPAR_TOK) {
            match(LPAR_TOK);
            System.out.println("lpar tok matched running intepr");
            intexpr();
            match(RPAR_TOK);
            System.out.println("rpar tok matched");
        }
        else if (currTok == MINUS_TOK){
            match(MINUS_TOK);
            System.out.println("minus matched, running intelement");
             intelement();
             }
        else error("intelement");
    }
    private void boolexpr() {
        boolterm();
        boolexpr2();
         
    }
    //why?
    private void boolexpr2() {
        //boolexpr();
        match(OR_TOK);
        System.out.println("or tok matched");
        boolterm();
        boolexpr2();
        //boolterm();
    }
    private void boolterm() {
        boolelement();
    }
    private void boolterm2() {
        boolterm();
        match(AND_TOK);
        System.out.println("and tok matched");
        boolelement();

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
