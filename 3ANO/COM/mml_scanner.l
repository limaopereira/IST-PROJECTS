%option c++ prefix="mml_scanner_" outfile="mml_scanner.cpp"
%option stack noyywrap yylineno 8bit debug
%{ 
// make relevant includes before including the parser's tab file
#include <string>
#include <sstream>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "mml_parser.tab.h"


static std::ostringstream strlit;
// don't change this
#define yyerror LexerError
%}
%x X_STRING X_COMMENT X_BACKSLASH

%%


yydebug=1; 
set_debug(1);


"//".*$                 ; /* ignore comments */
"/*" yy_push_state(X_COMMENT);
<X_COMMENT>"/*" yy_push_state(X_COMMENT);
<X_COMMENT>"*/" yy_pop_state();
<X_COMMENT>.|\n ;


"null"              return tNULLPTR;

"auto"              return tAUTO;
"forward"           return tFORWARD;
"public"            return tPUBLIC;
"foreign"           return tFOREIGN;

"int"               return tTYPE_INT;
"double"            return tTYPE_REAL;
"string"            return tTYPE_STRING;
"void"              return tTYPE_VOID;

"->"                return tARROW;
"begin"             return tBEGIN;
"end"              return tEND;

"if"                return tIF;

"change"             return tCHANGE;
"with"              return tWITH;
".."                return tRANGE;


"elif"              return tELIF; 
"else"              return tELSE; 

"while"             return tWHILE; 
"next"              return tNEXT;
"stop"              return tSTOP;
"return"            return tRETURN;
"!!"                return tWRITELN; /* faz sentido incluir "!!" return tWRITE? */


"=="                return tEQ;
"!="                return tNE; 
"<="                return tLE; 
">="                return tGE; 

"&&"                return tAND; /* faz sentido incluir "~" return tNOT ?*/
"||"                return tOR;

"input"             return tINPUT;
"sizeof"            return tSIZEOF;

0[0-7]+              {
    try {   
        yylval.i = std::stoi(yytext, NULL, 8); return tINTEGER;
    } catch (const std::out_of_range& e) {
        yyerror("integer is out of range");
    }
}

0[0-9]+              yyerror("Invalid base 8 number.");

0|[1-9][0-9]*         {
    try { 
        yylval.i = std::stoi(yytext, NULL, 10); 
        return tINTEGER;
    } catch (const std::out_of_range& e) {
        yyerror("integer is out of range");
    }
}

([0-9]*\.[0-9]+|[0-9]+\.[0-9]*)([eE][-+]?[0-9]+)?        {   
    try{
        yylval.d = std::stod(yytext); return tREAL; 
    } catch (const std::out_of_range& e){
        yyerror("double is out of range");
    }
}

[0-9]+([Ee][-+]?[0-9]+)                 {
    try{
        yylval.d = std::stod(yytext); return tREAL; 
    } catch (const std::out_of_range& e){
        yyerror("double is out of range");
    }
}

[A-Za-z][A-Za-z0-9]*  yylval.s = new std::string(yytext); return tIDENTIFIER;

"\""                  yy_push_state(X_STRING); yylval.s = new std::string("");
<X_STRING>\\        yy_push_state(X_BACKSLASH);
<X_STRING>"\""        {
                        size_t nullPos = yylval.s->find('\0');
                        if (nullPos != std::string::npos)
                        {
                            yylval.s = new std::string(yylval.s->substr(0, nullPos));
                        }
                        yy_pop_state();
                        return tSTRING;
                    }
<X_STRING>\0        yyerror("null character in string");
<X_STRING>\n        yyerror("newline in string");       
<X_STRING>.         *yylval.s += yytext;


<X_BACKSLASH>n      *yylval.s += '\n'; yy_pop_state();
<X_BACKSLASH>r      *yylval.s += '\r'; yy_pop_state();
<X_BACKSLASH>t      *yylval.s += '\t'; yy_pop_state();
<X_BACKSLASH>\"     *yylval.s += '\"'; yy_pop_state();
<X_BACKSLASH>\\     *yylval.s += '\\'; yy_pop_state();
<X_BACKSLASH>[0-7]{1,3} {
                            unsigned long res = std::stoul(yytext,NULL,8);
                            if(res > 255 ){
                                yyerror("invalid 8 bit number");
                            }
                            *yylval.s += (char)(unsigned char)res;
                            yy_pop_state();
                        }
<X_BACKSLASH>.      *yylval.s += *yytext; yy_pop_state();


[,;!(){}\[\]+-?*/%<>=~@] return *yytext;

[ \t\n\r]+               ; /* ignore whitespace */

.                      yyerror("Unknown character");

%%
