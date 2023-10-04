%{
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <algorithm>
#include <memory>
#include <cstring>
#include <cdk/compiler.h>
#include <cdk/types/types.h>
#include ".auto/all_nodes.h"
#define LINE                         compiler->scanner()->lineno()
#define yylex()                      compiler->scanner()->scan()
#define yyerror(compiler, s)         compiler->scanner()->error(s)
//-- don't change *any* of these --- END!
%}

%parse-param {std::shared_ptr<cdk::compiler> compiler}

%union {
  //--- don't change *any* of these: if you do, you'll break the compiler.
  YYSTYPE() : type(cdk::primitive_type::create(0, cdk::TYPE_VOID)) {}
  ~YYSTYPE() {}
  YYSTYPE(const YYSTYPE &other) { *this = other; }
  YYSTYPE& operator=(const YYSTYPE &other) { type = other.type; return *this; }

  std::shared_ptr<cdk::basic_type> type;        /* expression type */
  //-- don't change *any* of these --- END!

  int                   i;	/* integer value */
  double                d;
  std::string          *s;	/* symbol name or string literal */
  cdk::basic_node      *node;	/* node pointer */
  cdk::sequence_node   *sequence;
  cdk::expression_node *expression; /* expression nodes */
  cdk::lvalue_node     *lvalue;
  mml::declaration_node *decl;
  mml::block_node      *block;
  std::shared_ptr<cdk::basic_type> vartype;
  std::vector<std::shared_ptr<cdk::basic_type>> *types;
  mml::function_definition_node *fndef;
};

%token tNULLPTR
%token tAUTO tPRIVATE tFORWARD tPUBLIC tFOREIGN
%token tTYPE_INT tTYPE_REAL tTYPE_STRING tTYPE_VOID
%token tARROW tBEGIN tEND
%token tIF tELIF tELSE
%token tWHILE tNEXT tSTOP tRETURN tWRITELN tCHANGE tWITH tRANGE
%token tINPUT tSIZEOF

%token <i> tINTEGER
%token <d> tREAL
%token <s> tIDENTIFIER tSTRING

%nonassoc tIF tWHILE
%nonassoc tELIF tELSE


%right '=' 
%left tOR
%left tAND
%nonassoc '~'
%left tEQ tNE
%left tLE tGE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY '?'
%nonassoc '(' '['
 

%type <node> instruction elif 
%type <decl> declaration var filedeclaration
%type <sequence> file declarations instructions opt_instructions expressions opt_expressions opt_vars vars filedeclarations
%type <expression> expression integer real opt_expression expression_assignment
%type <lvalue> lval
%type <block> main_block
%type <vartype> type function_type return_type
%type <types> types
%type <fndef> fundef program 
%type <s> string

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%


file                    :                           {compiler->ast($$ = new cdk::sequence_node(LINE));}
                        |filedeclarations           {compiler->ast($$ = $1);}
                        |                 program   {compiler->ast($$ = new cdk::sequence_node(LINE,$1));}
                        |filedeclarations program   {compiler->ast($$ = new cdk::sequence_node(LINE,$2,$1));}
                        ;

filedeclarations        :                 filedeclaration   {$$ = new cdk::sequence_node(LINE,$1);}
                        |filedeclarations filedeclaration   {$$ = new cdk::sequence_node(LINE, $2, $1);}
                        ;

filedeclaration         :tPUBLIC type tIDENTIFIER opt_expression ';'   {$$ = new mml::declaration_node(LINE, tPUBLIC, $2, *$3, $4); delete $3;}
                        |tFORWARD type tIDENTIFIER ';'              {$$ = new mml::declaration_node(LINE, tFORWARD, $2, *$3, nullptr); delete $3;}
                        |tFOREIGN type tIDENTIFIER ';'              {$$ = new mml::declaration_node(LINE, tFOREIGN, $2, *$3, nullptr); delete $3;}
                        |tPUBLIC tIDENTIFIER expression_assignment ';'        {$$ = new mml::declaration_node(LINE, tPUBLIC, nullptr, *$2, $3); delete $2;}
                        |tPUBLIC tAUTO tIDENTIFIER expression_assignment ';'      {$$ = new mml::declaration_node(LINE, tPUBLIC, nullptr, *$3, $4); delete $3;}
                        |declaration                             {$$ = $1;}
                        ;

program	                :tBEGIN main_block tEND     {$$ = new mml::function_definition_node(LINE, cdk::primitive_type::create(4, cdk::TYPE_INT), nullptr, $2, true);} /* pensar melhor na questão de incluir o tipo de retorno "int" */
	                    ;

main_block              :             opt_instructions {$$ = new mml::block_node(LINE, nullptr, $1);}
                        |declarations opt_instructions {$$ = new mml::block_node(LINE, $1,$2);}
                        ;

declaration             :type tIDENTIFIER opt_expression ';'     {$$ = new mml::declaration_node(LINE, tPRIVATE, $1, *$2, $3); delete $2;}
                        |tAUTO tIDENTIFIER expression_assignment ';' {$$ = new mml:: declaration_node(LINE, tPRIVATE, nullptr, *$2, $3); delete $2;}
                        ;

declarations            :             declaration       {$$ = new cdk::sequence_node(LINE, $1);}  
                        |declarations declaration       {$$ = new cdk::sequence_node(LINE, $2, $1);}
                        ;

instruction             :expression ';'                             {$$ = new mml::evaluation_node(LINE, $1);}
                        |expressions '!'                            {$$ = new mml::write_node(LINE, $1);}
                        |expressions tWRITELN                       {$$ = new mml::write_node(LINE, $1, true);}
                        |tSTOP ';'                                  {$$ = new mml::stop_node(LINE);}
                        |tSTOP tINTEGER ';'                         {$$ = new mml::stop_node(LINE, $2);}
                        |tNEXT ';'                                  {$$ = new mml::next_node(LINE);}
                        |tNEXT tINTEGER ';'                         {$$ = new mml::next_node(LINE, $2);}
                        |tRETURN ';'                                {$$ = new mml::return_node(LINE);}
                        |tRETURN expression ';'                     {$$ = new mml::return_node(LINE, $2);}
                        |tIF '(' expression ')' instruction %prec tIF    {$$ = new mml::if_node(LINE, $3, $5);}
                        |tIF '(' expression ')' instruction elif      {$$ = new mml::if_else_node(LINE, $3, $5, $6);}
                        |tWHILE '(' expression ')' instruction      {$$ = new mml::while_node(LINE,$3,$5);}
                        |'{' main_block '}'                         {$$ = $2;}
                        |tWITH expression tCHANGE expression tARROW expression tRANGE expression ';' {$$ = new mml::with_node(LINE, $2, $4, $6, $8);}
                        ;

elif                    :tELSE instruction                              {$$ = $2;}
                        |tELIF '(' expression ')' instruction %prec tIF {$$ = new mml::if_node(LINE, $3, $5);}
                        |tELIF '(' expression ')' instruction elif       {$$ = new mml::if_else_node(LINE, $3, $5, $6);}
                        ;

instructions            :             instruction       {$$ = new cdk::sequence_node(LINE, $1);}
                        |instructions instruction       {$$ = new cdk::sequence_node(LINE, $2, $1);}
                        ;
opt_instructions        :               {$$ = nullptr;}
                        |instructions   {$$ = $1;}
                        ;

expression              :integer                                {$$ = $1;}
                        |real                                   {$$ = $1;}
                        |string                                 {$$ = new cdk::string_node(LINE, $1);}
                        |tNULLPTR                               {$$ = new mml::null_node(LINE);}
                        |'(' expression ')'                     {$$ = $2;}
                        |'[' expression ']'                     {$$ = new mml::stack_alloc_node(LINE, $2);}
                        |lval '?'                               {$$ = new mml::address_of_node(LINE, $1);}
                        |lval                                   {$$ = new cdk::rvalue_node(LINE, $1);}
                        |expression '(' opt_expressions ')'     {$$ = new mml::function_call_node(LINE, $1, $3);}
                        |'@' '(' opt_expressions ')'            {$$ = new mml::function_call_node(LINE,nullptr, $3);}
                        |tSIZEOF '(' expression ')'             {$$ = new mml::sizeof_node(LINE, $3);}
                        |tINPUT                                 {$$ = new mml::input_node(LINE);}
                        |'+' expression %prec tUNARY            {$$ = new mml::identity_node(LINE, $2);}
                        |'-' expression %prec tUNARY            {$$ = new cdk::neg_node(LINE, $2);}
                        |expression '+' expression              {$$ = new cdk::add_node(LINE, $1, $3);}
                        |expression '-' expression              {$$ = new cdk::sub_node(LINE, $1, $3);}
                        |expression '*' expression              {$$ = new cdk::mul_node(LINE, $1, $3);}
                        |expression '/' expression              {$$ = new cdk::div_node(LINE, $1, $3);}
                        |expression '%' expression              {$$ = new cdk::mod_node(LINE, $1, $3);}
                        |expression '<' expression              {$$ = new cdk::lt_node(LINE, $1, $3);}
                        |expression '>' expression              {$$ = new cdk::gt_node(LINE, $1, $3);}
                        |expression tGE expression              {$$ = new cdk::ge_node(LINE, $1, $3);}
                        |expression tLE expression              {$$ = new cdk::le_node(LINE, $1, $3);}
                        |expression tNE expression              {$$ = new cdk::ne_node(LINE, $1, $3);}
                        |expression tEQ expression              {$$ = new cdk::eq_node(LINE, $1, $3);}
                        |'~' expression                         {$$ = new cdk::not_node(LINE, $2);}
                        |expression tAND expression             {$$ = new cdk::and_node(LINE, $1, $3);}
                        |expression tOR expression              {$$ = new cdk::or_node(LINE, $1, $3);}
                        |lval '='   expression                  {$$ = new cdk::assignment_node(LINE, $1, $3);}
                        |fundef                                 {$$ = $1;}
                        ;

expressions             :expression                             {$$ = new cdk::sequence_node(LINE, $1);}
                        |expressions ',' expression             {$$ = new cdk::sequence_node(LINE, $3, $1);}
                        ;

opt_expressions         :                                       {$$ = new cdk::sequence_node(LINE);}
                        |expressions                            {$$ = $1;}
                        ;


opt_expression          :                           {$$ = nullptr;}
                        |expression_assignment      {$$ = $1;} 
                        ;    

expression_assignment   :'=' expression    {$$ = $2;}
                        ;



var                     :type tIDENTIFIER {$$ = new mml::declaration_node(LINE, tPRIVATE, $1, *$2, nullptr); delete $2;}
                        ;


vars                    :         var   {$$ = new cdk::sequence_node(LINE,$1);}
                        |vars ',' var   {$$ = new cdk::sequence_node(LINE,$3,$1);}
                        ;

opt_vars                :               {$$ = nullptr;}
                        |vars           {$$ = $1;}
                        ;

type                    :tTYPE_INT              { $$ = cdk::primitive_type::create(4, cdk::TYPE_INT);    }
                        |tTYPE_REAL             { $$ = cdk::primitive_type::create(8, cdk::TYPE_DOUBLE); }
                        |tTYPE_STRING           { $$ = cdk::primitive_type::create(4, cdk::TYPE_STRING); }
                        |'[' tTYPE_VOID ']'     { $$ = cdk::reference_type::create(4, cdk::primitive_type::create(4, cdk::TYPE_VOID)); } 
                        |'[' type ']'           {   if ($2->name() == cdk::TYPE_POINTER && 
                                                    cdk::reference_type::cast($2)->referenced()->name() == cdk::TYPE_VOID) {
                                                        $$ = cdk::reference_type::create(4, cdk::primitive_type::create(4, cdk::TYPE_VOID)); 
                                                    }    
                                                    else {
                                                        $$ = cdk::reference_type::create(4, $2);     
                                                    }   
                                                }
                        |function_type          {$$ = $1;}
                        ;

types                   :type                   {$$ = new std::vector<std::shared_ptr<cdk::basic_type>>(); $$->push_back($1);}
                        |types ',' type         {$$ = $1; $$->push_back($3);}
                        ;

function_type           :tTYPE_VOID '<' '>'             {$$ = cdk::functional_type::create(cdk::primitive_type::create(4, cdk::TYPE_VOID)); }
                        |tTYPE_VOID '<' types '>'      {$$ = cdk::functional_type::create(*$3, cdk::primitive_type::create(4, cdk::TYPE_VOID));delete $3;}
                        |type '<' '>'                  {$$ = cdk::functional_type::create($1);}
                        |type '<' types '>'            {$$ = cdk::functional_type::create(*$3, $1); delete $3; }
                        ;

return_type             :type                   {$$ = $1;}
                        |tTYPE_VOID             {$$ = cdk::primitive_type::create(4,cdk::TYPE_VOID);}
                        ;

fundef                  :'(' opt_vars ')' tARROW return_type '{' main_block '}' {$$ = new mml::function_definition_node(LINE, $5, $2, $7);}
                        ;

integer                 :tINTEGER                       {$$ = new cdk::integer_node(LINE,$1);}
                        ;

real                    :tREAL                          {$$ = new cdk::double_node(LINE,$1);}
                        ;

string                  :tSTRING                        {$$ = $1;}
                        |string tSTRING                 {$$ = $1; $$->append(*$2);delete $2;}
                        ;

lval                    :tIDENTIFIER                    {$$ = new cdk::variable_node(LINE, $1);}
                        |expression '[' expression ']'       {$$ = new mml::index_node(LINE,$1,$3);}
                        ;

%%
