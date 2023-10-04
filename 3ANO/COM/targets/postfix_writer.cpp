#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include "targets/frame_size_calculator.h"
#include "targets/symbol.h"
#include ".auto/all_nodes.h"  // all_nodes.h is automatically generated

#include <mml_parser.tab.h>

//---------------------------------------------------------------------------

void mml::postfix_writer::do_nil_node(cdk::nil_node * const node, int lvl) {
  // EMPTY
}
void mml::postfix_writer::do_data_node(cdk::data_node * const node, int lvl) {
  // EMPTY
}

void mml::postfix_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
    for (size_t i = 0; i < node->size(); i++) {
        node->node(i)->accept(this, lvl);
    }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
    if(_inFunctionBody) {
        _pf.DOUBLE(node->value());
    }
    else {
        _pf.SDOUBLE(node->value());
    }
}


void mml::postfix_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
    if(_inFunctionBody) {
        _pf.INT(node->value()); // push an integer
    }
    else {
        _pf.SINT(node->value());
    }
}

void mml::postfix_writer::do_string_node(cdk::string_node * const node, int lvl) {
    std::string lbl = mklbl(++_lbl);
    _pf.RODATA();
    _pf.ALIGN();
    _pf.LABEL(lbl);
    _pf.SSTRING(node->value());
    if (_inFunctionBody) {
        _pf.TEXT(_return_labels.back());
        _pf.ADDR(lbl);
    } else {
        _pf.DATA();
        _pf.SADDR(lbl);
    }
}

void mml::postfix_writer::do_null_node(mml::null_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    if (_inFunctionBody) {
        _pf.INT(0);
    } 
    else {
        _pf.SINT(0);
    }
}


//---------------------------------------------------------------------------

void mml::postfix_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl); // determine the value
    _pf.NEG(); // 2-complement
}

void mml::postfix_writer::do_identity_node(mml::identity_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl);  
}

void mml::postfix_writer::do_not_node(cdk::not_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl); 
    _pf.INT(0);
    _pf.EQ();
}


//---------------------------------------------------------------------------

void mml::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    
    node->left()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
    }
    else if (node->is_typed(cdk::TYPE_POINTER) && node->left()->is_typed(cdk::TYPE_INT)) {
        auto referenced = cdk::reference_type::cast(node->right()->type())->referenced();
        if(referenced->name() == cdk::TYPE_VOID){
            _pf.INT(1);
        }
        else{
            _pf.INT(referenced->size());
        }
        _pf.MUL();
    }

    node->right()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    } 
    else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
        auto referenced = cdk::reference_type::cast(node->left()->type())->referenced();
        if(referenced->name() == cdk::TYPE_VOID){
            _pf.INT(1);
        }
        else{
            _pf.INT(referenced->size());
        }
        _pf.MUL();
    }
    
    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DADD();
    } 
    else {
        _pf.ADD();
    }
}

void mml::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    
    node->left()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
    _pf.I2D();
    }
    else if (node->is_typed(cdk::TYPE_POINTER) && node->left()->is_typed(cdk::TYPE_INT)) {
        auto referenced = cdk::reference_type::cast(node->right()->type())->referenced();
        _pf.INT(referenced->size());
        _pf.MUL();
    }

    node->right()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    } 
    else if (node->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
        auto referenced = cdk::reference_type::cast(node->left()->type())->referenced();
        _pf.INT(referenced->size());
        _pf.MUL();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DSUB();
    } 
    
    else {
        _pf.SUB();
    }
    
    if(node->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER)){
        auto referenced = cdk::reference_type::cast(node->left()->type())->referenced();
        _pf.INT(referenced->size());
        _pf.DIV();
    }
}

void mml::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    }

    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DMUL();
    }
    else {
        _pf.MUL();
    }
}

void mml::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
  
    node->left()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->left()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
    }
    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.DDIV();
    }
    else {
        _pf.DIV();
    }
}

void mml::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->left()->accept(this, lvl);
    node->right()->accept(this, lvl);
    _pf.MOD();
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.LT();
}

void mml::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.LE();
}
void mml::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.GE();
}
void mml::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
     _pf.GT();
}

void mml::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.NE();
}
void mml::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    bool haveDouble = false;

    node->left()->accept(this, lvl);
    if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    node->right()->accept(this, lvl);
    if (node->right()->is_typed(cdk::TYPE_INT) && node->left()->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.I2D();
    }

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) || node->right()->is_typed(cdk::TYPE_DOUBLE)){
        haveDouble = true;
    }

    if(haveDouble){
        _pf.DCMP();
        _pf.INT(0);
    }
    _pf.EQ();
}

void mml::postfix_writer::do_and_node(cdk::and_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    std::string lbl = mklbl(++_lbl);
    node->left()->accept(this, lvl);
    _pf.DUP32();
    _pf.JZ(lbl);
    node->right()->accept(this, lvl);
    _pf.AND();
    _pf.ALIGN();
    _pf.LABEL(lbl);
}

void mml::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    std::string lbl = mklbl(++_lbl);
    node->left()->accept(this, lvl);
    _pf.DUP32();
    _pf.JNZ(lbl);
    node->right()->accept(this, lvl);
    _pf.OR();
    _pf.ALIGN();
    _pf.LABEL(lbl);
}


//---------------------------------------------------------------------------

void mml::postfix_writer::do_variable_node(cdk::variable_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    
    const std::string &id = node->name();
    auto symbol = _symtab.find(id);
    
    if (symbol->qualifier() == tFOREIGN) {
        _extern_label = symbol->name();
    }
    else if (symbol->global()) {
        _pf.ADDR(symbol->name());
    }
    else {
        _pf.LOCAL(symbol->offset());
    }
}

void mml::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->lvalue()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.LDDOUBLE();
    }
    else {
        if(_extern_label.empty()) {
            _pf.LDINT();
        }
    }
}

void mml::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    node->rvalue()->accept(this, lvl); // determine the new value
    if (node->is_typed(cdk::TYPE_DOUBLE)){
        if(node->rvalue()->is_typed(cdk::TYPE_INT)){
            _pf.I2D();
        }
        _pf.DUP64();
    }
    else{
        _pf.DUP32();
    }
    node->lvalue()->accept(this, lvl);
    if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.STDOUBLE();
    }
    else{
        _pf.STINT();
    }
}

//---------------------------------------------------------------------------




void mml::postfix_writer::do_block_node(mml::block_node * const node, int lvl){
    _symtab.push();
    if (node->declarations()) 
        node->declarations()->accept(this, lvl + 2);
    if (node->instructions()) 
        node->instructions()->accept(this, lvl + 2);
    _symtab.pop();
}

void mml::postfix_writer::do_evaluation_node(mml::evaluation_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    node->argument()->accept(this, lvl); // determine the value
    if(!(node->argument()->type()->name() == cdk::TYPE_VOID))
        _pf.TRASH(node->argument()->type()->size());
}


void mml::postfix_writer::do_write_node(mml::write_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    for (size_t ix = 0; ix < node->arguments()->size(); ix++) {
        auto arg = dynamic_cast<cdk::expression_node*>(node->arguments()->node(ix));
        arg->accept(this, lvl);
        if (arg->is_typed(cdk::TYPE_INT)) {
            _external_functions.insert("printi");
            _pf.CALL("printi");
            _pf.TRASH(4);
        }
        else if (arg->is_typed(cdk::TYPE_DOUBLE)) {
            _external_functions.insert("printd");
            _pf.CALL("printd");
            _pf.TRASH(8);
        }
        else if (arg->is_typed(cdk::TYPE_STRING)) {
            _external_functions.insert("prints");
            _pf.CALL("prints");
            _pf.TRASH(4);
        }
        else {
            std::cerr << "cannot write expression of unknown type" << std::endl;
            return;
        }
    }
    if (node->newline()) {
        _external_functions.insert("println");
        _pf.CALL("println");
    }
}


void mml::postfix_writer::do_input_node(mml::input_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;

    if (node->is_typed(cdk::TYPE_INT)) {
        _external_functions.insert("readi");
        _pf.CALL("readi");
        _pf.LDFVAL32();
    }
    else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _external_functions.insert("readd");
        _pf.CALL("readd");
        _pf.LDFVAL64();
    }
    else {
        std::cerr << "cannot read input type" << std::endl;
    }
}


//---------------------------------------------------------------------------



/*void mml::postfix_writer::do_print_node(mml::print_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value to print
  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    _pf.CALL("printi");
    _pf.TRASH(4); // delete the printed value
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _pf.CALL("prints");
    _pf.TRASH(4); // delete the printed value's address
  } else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  }
  _pf.CALL("println"); // print a newline
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_read_node(mml::read_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.CALL("readi");
  _pf.LDFVAL32();
  node->argument()->accept(this, lvl);
  _pf.STINT();
} */
// nodes no longer used

//---------------------------------------------------------------------------

void mml::postfix_writer::do_while_node(mml::while_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    _whileCond.push_back(++_lbl);
    _whileEnd.push_back(++_lbl);


    _symtab.push();

    _pf.ALIGN();
    _pf.LABEL(mklbl(_whileCond.back()));
    node->condition()->accept(this, lvl + 2);
    _pf.JZ(mklbl(_whileEnd.back()));

    bool oldReturnSeen = _returnSeen;

    node->block()->accept(this, lvl + 2);

    _returnSeen = oldReturnSeen;

    _pf.JMP(mklbl(_whileCond.back()));
    _pf.ALIGN();
    _pf.LABEL(mklbl(_whileEnd.back()));

    _symtab.pop();
    
    _whileEnd.pop_back();
    _whileCond.pop_back();

}

void mml::postfix_writer::do_with_node(mml::with_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;

    std::string comp = mklbl(++_lbl);
    std::string end = mklbl(++_lbl);

    _pf.ALIGN();
    node->low()->accept(this, lvl + 2);

    _pf.LABEL(comp);
    _pf.DUP32();
    node->high()->accept(this, lvl + 2);
    
    _pf.LE();
    _pf.JZ(end);
   
    auto vector_type = cdk::reference_type::cast(node->vector()->type())->referenced();    
    
    _pf.DUP32();
    _pf.INT(vector_type->size());
    _pf.MUL();  
    node->vector()->accept(this, lvl + 2);
    _pf.ADD();

    if(vector_type->name() == cdk::TYPE_DOUBLE ){
        _pf.I2D();
        _pf.DUP64();
        _pf.D2I();
        _pf.LDDOUBLE();
    }
    else {
        _pf.DUP32();
        _pf.LDINT();
    }

    node->identifier()->accept(this, lvl + 2);
    if (!_extern_label.empty()) {
            _pf.CALL(_extern_label);
    }
    else {
        _pf.BRANCH();
    }


    if(vector_type->name() == cdk::TYPE_DOUBLE ){
        _pf.TRASH(8);
        _pf.LDFVAL64();
        _pf.SWAP64();
        _pf.D2I();
        _pf.STDOUBLE();
    }
    else {
        _pf.TRASH(4);
        _pf.LDFVAL64();
        _pf.D2I();
        _pf.SWAP32();
        _pf.STINT();
    }

    _pf.INT(1);
    _pf.ADD();

    _pf.JMP(comp);
    _pf.ALIGN();
    _pf.LABEL(end);
    
}




//---------------------------------------------------------------------------

void mml::postfix_writer::do_if_node(mml::if_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
    
    std::string lbl = mklbl(++_lbl);
    node->condition()->accept(this, lvl);
    _pf.JZ(lbl);
    bool oldReturnSeen = _returnSeen;

    node->block()->accept(this, lvl + 2);

    _returnSeen = oldReturnSeen;
    _pf.LABEL(lbl);
}

void mml::postfix_writer::do_if_else_node(mml::if_else_node * const node, int lvl) {
    ASSERT_SAFE_EXPRESSIONS;
  
    std::string lbl_else = mklbl(++_lbl);
    std::string lbl_end = mklbl(++_lbl);
    node->condition()->accept(this, lvl);
    _pf.JZ(lbl_else);
    bool oldReturnSeen = _returnSeen;
    node->thenblock()->accept(this, lvl + 2);
    _returnSeen = oldReturnSeen;
    _pf.JMP(lbl_end);
    _pf.LABEL(lbl_else);
    oldReturnSeen = _returnSeen;
    node->elseblock()->accept(this, lvl + 2);
    _returnSeen = oldReturnSeen;
    _pf.LABEL(lbl_end);
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_address_of_node(mml::address_of_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    node->lvalue()->accept(this, lvl);
}

void mml::postfix_writer::do_index_node(mml::index_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    node->base()->accept(this, lvl);
    node->index()->accept(this, lvl);
    _pf.INT(node->type()->size());
    _pf.MUL();
    _pf.ADD();
}

void mml::postfix_writer::do_stack_alloc_node(mml::stack_alloc_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    auto referenced = cdk::reference_type::cast(node->type())->referenced();
    node->argument()->accept(this, lvl);
    _pf.INT(referenced->size());
    _pf.MUL();
    _pf.ALLOC();
    _pf.SP();
}

void mml::postfix_writer::do_sizeof_node(mml::sizeof_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    if (_inFunctionBody) {
        if(node->expression()->is_typed(cdk::TYPE_VOID)){
            _pf.INT(0);
        }
        else{
            _pf.INT(node->expression()->type()->size());
        }
    }
    else {
        if(node->expression()->is_typed(cdk::TYPE_VOID)){
            _pf.SINT(0);
        }
        else{
            _pf.SINT(node->expression()->type()->size());
        }
    }
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_declaration_node(mml::declaration_node * const node, int lvl){

    
    ASSERT_SAFE_EXPRESSIONS;

    auto id = node->identifier();

    int offset, typesize = node->type()->size();

    if (_inFunctionArgs) {
        offset = _offset;
        _offset += typesize;
    }
    else if (_inFunctionBody) {
        _offset -= typesize;
        offset = _offset;
    }
    else {
        offset = 0;
    }

    auto symbol = new_symbol();

    if (symbol) {
        symbol->set_offset(offset);
        reset_new_symbol();
    }

    if (!_inFunctionBody && !_inFunctionArgs) {
        _symbols_to_declare.insert(symbol->name());
    }

    if(node->initializer()){
        if (_inFunctionBody) {
            node->initializer()->accept(this, lvl);
            if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_STRING) || 
                symbol->is_typed(cdk::TYPE_POINTER) || symbol->is_typed(cdk::TYPE_FUNCTIONAL)) {
                    _pf.LOCAL(symbol->offset());
                    _pf.STINT();
            } 
            else if(symbol->is_typed(cdk::TYPE_DOUBLE)) {
                if (node->initializer()->is_typed(cdk::TYPE_INT)) {
                    _pf.I2D();
                }
                _pf.LOCAL(symbol->offset());
                _pf.STDOUBLE();
            } 
            else {
                std::cerr << "unknown declaration node" << std::endl;
                return;
            }
        } 
        else {
            if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_DOUBLE) || symbol->is_typed(cdk::TYPE_POINTER)) {
                _pf.DATA();
                _pf.ALIGN();
                _pf.LABEL(symbol->name());
                if (symbol->is_typed(cdk::TYPE_INT)) {
                    node->initializer()->accept(this, lvl);
                } 
                else if (symbol->is_typed(cdk::TYPE_POINTER)) {
                    node->initializer()->accept(this, lvl);
                } 
                else if (symbol->is_typed(cdk::TYPE_DOUBLE)) {
                    if (node->initializer()->is_typed(cdk::TYPE_DOUBLE)) {
                        node->initializer()->accept(this, lvl);
                    }
                    else if (node->initializer()->is_typed(cdk::TYPE_INT)) {
                        cdk::integer_node *dclini = dynamic_cast<cdk::integer_node*>(node->initializer());
                        cdk::double_node ddi(dclini->lineno(), dclini->value());
                        ddi.accept(this, lvl);
                    } 
                    else {
                        std::cerr << "bad declaration for real value" << std::endl;
                    }
                }
            } 
            else if (symbol->is_typed(cdk::TYPE_STRING)) {
                _pf.DATA();
                _pf.ALIGN();
                _pf.LABEL(symbol->name());
                node->initializer()->accept(this, lvl);
            } 
            else if (symbol->is_typed(cdk::TYPE_FUNCTIONAL)) {
                _fun_symbols.push_back(symbol);
                reset_new_symbol();
                node->initializer()->accept(this, lvl);
                _pf.DATA();
                if (_fun_symbols.back()->qualifier() == tPUBLIC) {
                    _pf.GLOBAL(_fun_symbols.back()->name(), _pf.OBJ());
                }
                _pf.ALIGN();
                _pf.LABEL(symbol->name());
                std::string label = _fun_label;
                _fun_label.clear();
                _pf.SADDR(label);
            } 
            else {
                std::cerr << "unexpected initializer in declaration" << std::endl;
            }
        }
        _symbols_to_declare.erase(symbol->name());
    }
}

//---------------------------------------------------------------------------


void mml::postfix_writer::do_function_call_node(mml::function_call_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    std::vector<std::shared_ptr<cdk::basic_type>> inputTypes;
    if (node->identifier()) {
        inputTypes = cdk::functional_type::cast(node->identifier()->type())->input()->components();
    }
    else {
        auto currFun = _fun_symbols.back();
        inputTypes = cdk::functional_type::cast(currFun->type())->input()->components();
    }

    size_t argsSize = 0;
    if (node->arguments()) {
        for (int ix = node->arguments()->size() - 1; ix >= 0; --ix) {
            auto arg = dynamic_cast<cdk::expression_node*>(node->arguments()->node(ix));
            arg->accept(this, lvl + 2);
            if (arg->is_typed(cdk::TYPE_INT) && inputTypes.at(ix)->name() == cdk::TYPE_DOUBLE) {
                _pf.I2D();
                argsSize += 4;
            }
            argsSize += arg->type()->size();
        }
    }

    if (node->identifier()) {
        _extern_label.clear();
        node->identifier()->accept(this, lvl);
        if (!_extern_label.empty()) {
            _pf.CALL(_extern_label);
        }
        else {
            _pf.BRANCH();
        }
    }
    else {
        _pf.CALL(_return_labels.back());
    }

    if (argsSize != 0){
       _pf.TRASH(argsSize);
    }

    if (node->is_typed(cdk::TYPE_INT)) {
        if (!_extern_label.empty()) {
            _pf.LDFVAL32();
        }
        else {
            _pf.LDFVAL64();
            _pf.D2I();
        }
    }
    else if (node->is_typed(cdk::TYPE_POINTER) || node->is_typed(cdk::TYPE_STRING) || node->is_typed(cdk::TYPE_FUNCTIONAL)) {
        _pf.LDFVAL32();
    }
    else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        _pf.LDFVAL64();
    }

    _extern_label.clear();

}

//---------------------------------------------------------------------------

/*
void mml::postfix_writer::do_program_node(mml::program_node * const node, int lvl) {
  // Note that MML doesn't have functions. Thus, it doesn't need
  // a function node. However, it must start in the main function.
  // The ProgramNode (representing the whole program) doubles as a
  // main function node.

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();
  _pf.GLOBAL("_main", _pf.FUNC());
  _pf.LABEL("_main");
  _pf.ENTER(0);  // MML doesn't implement local variables

  node->statements()->accept(this, lvl);

  // end the main function
  _pf.INT(0);
  _pf.STFVAL32();
  _pf.LEAVE();
  _pf.RET();

  // these are just a few library function imports
  _pf.EXTERN("readi");
  _pf.EXTERN("printi");
  _pf.EXTERN("prints");
  _pf.EXTERN("println");
}

*/



void mml::postfix_writer::do_function_definition_node(mml::function_definition_node * const node, int lvl){
    
    
    ASSERT_SAFE_EXPRESSIONS;
    bool isMain = node->isMain();
    bool publicFun = false; 
    std::string funName, lbl;
    // std::shared_ptr<mml::symbol> symbol;
    
    if(isMain){
        for (std::string name : _symbols_to_declare) {
            auto symbol = _symtab.find(name, 0);
            if (symbol->qualifier() == tFOREIGN) {
                _external_functions.insert(name);
            } 
            else {
                _pf.BSS();
                _pf.ALIGN();
                _pf.LABEL(name);
                _pf.SALLOC(symbol->type()->size());    
            }
        }
        lbl = "_main";
        funName = "_main";
    }
    else {
        lbl = mklbl(++_lbl);
    }


    auto symbol = new_symbol();

    if (symbol) {
        _fun_symbols.push_back(symbol);
        reset_new_symbol();
    }

    int oldOffset = _offset;
    _offset = 8;
    _symtab.push();

    
    if (node->arguments()) {
        _inFunctionArgs = true;
        for (size_t ix = 0; ix < node->arguments()->size(); ix++){
            cdk::basic_node *argument = node -> arguments()->node(ix);
            if(!argument)
                break;
            argument->accept(this, 0);
        }
        _inFunctionArgs = false;
    }

    _return_labels.push_back(lbl);
    _pf.TEXT(_return_labels.back());
    _pf.ALIGN();
    if (publicFun || isMain) {
        _pf.GLOBAL(funName, _pf.FUNC());
    }
    _pf.LABEL(lbl);

    frame_size_calculator lsc(_compiler, _symtab, symbol);

    _symtab.push();
    node->accept(&lsc, lvl);
    _symtab.pop();

    _pf.ENTER(lsc.localsize());

    _offset = 0;

    bool oldReturnSeen = _returnSeen;
    _returnSeen = false;
    bool _wasInFunctionBody = _inFunctionBody;
    _inFunctionBody = true;
    if (node->block()) {
        node->block()->accept(this, lvl);
    }

    _inFunctionBody = _wasInFunctionBody;

    _symtab.pop();

    if (!_returnSeen) {
        if(isMain){
            _pf.INT(0);
            _pf.STFVAL32(); 
        }
        _pf.LEAVE();
        _pf.RET();
    }
    _return_labels.pop_back();
    if (symbol) {
        _fun_symbols.pop_back();
    }


    if (_inFunctionBody) {
        _pf.TEXT(_return_labels.back());
        _pf.ADDR(lbl);
    }

    if(isMain){
        // _fun_symbols.pop_back();
        for (std::string ext : _external_functions) {
            _pf.EXTERN(ext);
        }
        _external_functions.clear();
    }

    _fun_label = lbl;
    _returnSeen = oldReturnSeen;
    _offset = oldOffset;
}


//---------------------------------------------------------------------------



void mml::postfix_writer::do_return_node(mml::return_node * const node, int lvl){
    ASSERT_SAFE_EXPRESSIONS;
    _returnSeen = true;
    auto currFun = _fun_symbols.back();

   
    std::shared_ptr<cdk::basic_type> outputType = cdk::functional_type::cast(currFun->type())->output(0);
    if (outputType->name() != cdk::TYPE_VOID) {
        node->retval()->accept(this, lvl + 2);
        if (outputType->name() == cdk::TYPE_INT) {
            if (!currFun->is_main()) {
                _pf.I2D();
                _pf.STFVAL64();
            } 
            else {
            _pf.STFVAL32();
            }
        }
        else if (outputType->name() == cdk::TYPE_STRING || outputType->name() == cdk::TYPE_POINTER || 
                outputType->name() == cdk::TYPE_FUNCTIONAL) {
                    _pf.STFVAL32();
        }
        else if (outputType->name() == cdk::TYPE_DOUBLE) {
            if (node->retval()->type()->name() == cdk::TYPE_INT) 
                _pf.I2D();
            _pf.STFVAL64();
        }
        else {
            std::cerr << "UNKNOWN RETURN TYPE" << std::endl;
        }
    }
    _pf.LEAVE();
    _pf.RET();
}

//---------------------------------------------------------------------------

void mml::postfix_writer::do_stop_node(mml::stop_node * const node, int lvl){
    int level = node->level();
    if (_whileEnd.size() >= static_cast<std::size_t>(level)) {
        _pf.JMP(mklbl(_whileEnd.at(_whileEnd.size() - level)));
    }
    else {
        std::cerr << "stop instruction outside cycle" << std::endl;
    }
}

void mml::postfix_writer::do_next_node(mml::next_node * const node, int lvl){
    int level = node->level();
    if (_whileCond.size() >= static_cast<std::size_t>(level)) {
        _pf.JMP(mklbl(_whileCond.at(_whileCond.size() - level)));
    }
    else {
        std::cerr << "next instruction outside cycle" << std::endl;
    }
}

//---------------------------------------------------------------------------
