#include <string>
#include "targets/type_checker.h"
#include ".auto/all_nodes.h"  // automatically generated
#include <cdk/types/primitive_type.h>

#include <mml_parser.tab.h>

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }


static bool compatible_pointed_types(std::shared_ptr<cdk::basic_type> ltype, std::shared_ptr<cdk::basic_type> rtype) {
  auto lt = ltype;
  auto rt = rtype;
  
  
  while (rt != nullptr && lt->name() == cdk::TYPE_POINTER && rt->name() == cdk::TYPE_POINTER) {
    lt = cdk::reference_type::cast(lt)->referenced();
    rt= cdk::reference_type::cast(rt)->referenced();
  }
  
  return rt == nullptr || rt->name() == cdk::TYPE_UNSPEC || lt->name() == cdk::TYPE_VOID || rt->name() == cdk::TYPE_VOID || lt->name() == rt->name() || (lt->name() == cdk::TYPE_INT && rt->name()==cdk::TYPE_DOUBLE);
}

static bool compatible_function_types(std::shared_ptr<cdk::functional_type> ltype, std::shared_ptr<cdk::functional_type> rtype) {

  
  if (ltype->output(0)->name() == cdk::TYPE_POINTER) {
    if (!(rtype->output(0)->name() == cdk::TYPE_POINTER && compatible_pointed_types(ltype->output(0), rtype->output(0)))) {
      return false;
    }
  } else if (ltype->output(0)->name() == cdk::TYPE_FUNCTIONAL) {
     if (!(rtype->output(0)->name() == cdk::TYPE_FUNCTIONAL && 
          compatible_function_types(cdk::functional_type::cast(ltype->output(0)), cdk::functional_type::cast(rtype->output(0))))) {
      return false;
    }

  } else if (ltype->output(0)->name() == cdk::TYPE_DOUBLE) {
    if (!((rtype->output(0)->name() == cdk::TYPE_INT) || (rtype->output(0)->name() == cdk::TYPE_DOUBLE))) {
      return false;
    }
  } else if ((ltype->output(0)->name() != rtype->output(0)->name())) {
    return false;
  }

  if (ltype->input_length() != rtype->input_length()) {
    return false;
  }
  for (size_t tx = 0; tx < ltype->input_length(); tx++) {
    if (ltype->input(tx)->name() == cdk::TYPE_POINTER) {
      if (!(rtype->input(tx)->name() == cdk::TYPE_POINTER && compatible_pointed_types(ltype->input(tx), rtype->input(tx)))) {
        return false;
      }
    } else if (ltype->input(tx)->name() == cdk::TYPE_FUNCTIONAL) {
      if (!(rtype->input(tx)->name() == cdk::TYPE_FUNCTIONAL && 
          compatible_function_types(cdk::functional_type::cast(ltype->input(tx)), cdk::functional_type::cast(rtype->input(tx))))) {
        return false;
      }
    } else if (rtype->input(tx)->name() == cdk::TYPE_DOUBLE) {
      if ((!((ltype->input(tx)->name() == cdk::TYPE_INT) || (ltype->input(tx)->name() == cdk::TYPE_DOUBLE)))) {
        return false;
      }
    } else if ((ltype->input(tx)->name() != rtype->input(tx)->name())) {
      return false;
    }
  }

  return true;
}



//---------------------------------------------------------------------------

void mml::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
    for (size_t i = 0; i < node->size(); i++)
        node->node(i)->accept(this, lvl);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}
void mml::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}

//---------------------------------------------------------------------------

void mml::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void mml::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
}

void mml::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
}

void mml::type_checker::do_null_node(mml::null_node * const node, int lvl){
    ASSERT_UNSPEC;
    node->type(cdk::reference_type::create(4, nullptr));
}

//---------------------------------------------------------------------------

void mml::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->argument()->accept(this, lvl + 2);
    if (!node->argument()->is_typed(cdk::TYPE_INT)) 
        throw std::string("wrong type in argument of not expression");
     node->type(node->argument()->type());
}

void mml::type_checker::do_identity_node(mml::identity_node * const node, int lvl){
    ASSERT_UNSPEC;
    node->argument()->accept(this, lvl + 2);
    if (!(node->argument()->is_typed(cdk::TYPE_INT) || node->argument()->is_typed(cdk::TYPE_DOUBLE))) 
        throw std::string("wrong type in argument of identity expression");
     node->type(node->argument()->type());
}

void mml::type_checker::do_neg_node(cdk::neg_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->argument()->accept(this, lvl + 2);
    if (!(node->argument()->is_typed(cdk::TYPE_INT) || node->argument()->is_typed(cdk::TYPE_DOUBLE))) 
        throw std::string("wrong type in argument of negation expression");
     node->type(node->argument()->type());
}

//---------------------------------------------------------------------------

void mml::type_checker::do_PIDExpression(cdk::binary_operation_node *const node, int lvl){
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    }
    else if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } 
    else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } 
    else if (node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(node->left()->type());
    } 
    else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_POINTER)) {
        node->type(node->right()->type());
    } 
    else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } 
    else if (node->left()->is_typed(cdk::TYPE_UNSPEC) && node->right()->is_typed(cdk::TYPE_UNSPEC)) { 
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        node->left()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } 
    else {
        throw std::string("wrong types in binary expression");
    }   
}

void mml::type_checker::do_IDExpression(cdk::binary_operation_node *const node, int lvl){
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } 
    else if (node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } 
    else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)) {
        node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } 
    else if (node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)) {
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if (node->left()->is_typed(cdk::TYPE_UNSPEC) && node->right()->is_typed(cdk::TYPE_UNSPEC)) {
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        node->left()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        node->right()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } 
    else {
        throw std::string("wrong types in binary expression");
    }
}

void mml::type_checker::do_IntOnlyExpression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    if (!node->left()->is_typed(cdk::TYPE_INT)) 
        throw std::string("wrong type in left argument of binary expression");

    node->right()->accept(this, lvl + 2);
    if (!node->right()->is_typed(cdk::TYPE_INT)) 
        throw std::string("wrong type in right argument of binary expression");

    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void mml::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
    do_PIDExpression(node, lvl);
}
void mml::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);
    if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER) &&
        compatible_pointed_types(node->left()->type(), node->right()->type())){
            node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else{
        do_PIDExpression(node, lvl);
    }
}
void mml::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
    do_IDExpression(node, lvl);
}
void mml::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
    do_IDExpression(node, lvl);
}
void mml::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
    do_IntOnlyExpression(node, lvl);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_ScalarLogicalExpression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else {
        throw std::string("wrong types in binary expression");
    }
}

void mml::type_checker::do_GeneralLogicalExpression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;
    node->left()->accept(this, lvl + 2);
    node->right()->accept(this, lvl + 2);

    if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_INT)){
    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_DOUBLE)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_INT) && node->right()->is_typed(cdk::TYPE_DOUBLE)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_DOUBLE) && node->right()->is_typed(cdk::TYPE_INT)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else if(node->left()->is_typed(cdk::TYPE_POINTER) && node->right()->is_typed(cdk::TYPE_POINTER)){
        node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    }
    else{
        throw std::string("wrong types in binary expression");
    }
}

void mml::type_checker::do_BooleanLogicalExpression(cdk::binary_operation_node *const node, int lvl) {
    ASSERT_UNSPEC;

    node->left()->accept(this, lvl + 2);
    if (!node->left()->is_typed(cdk::TYPE_INT)) {
        throw std::string("integer expression expected in binary expression");
    }

    node->right()->accept(this, lvl + 2);
    if (!node->right()->is_typed(cdk::TYPE_INT)) {
        throw std::string("integer expression expected in binary expression");
    }

    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void mml::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
    do_ScalarLogicalExpression(node, lvl);
}
void mml::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
    do_ScalarLogicalExpression(node, lvl);
}
void mml::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
    do_ScalarLogicalExpression(node, lvl);
}
void mml::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
    do_ScalarLogicalExpression(node, lvl);
}
void mml::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
    do_GeneralLogicalExpression(node, lvl);
}
void mml::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
    do_GeneralLogicalExpression(node, lvl);
}
void mml::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
    do_BooleanLogicalExpression(node, lvl);
}
void mml::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
    do_BooleanLogicalExpression(node, lvl);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_variable_node(cdk::variable_node *const node, int lvl) {
    ASSERT_UNSPEC;

    const std::string &id = node->name();
    std::shared_ptr<mml::symbol> symbol = _symtab.find(id);

    if (symbol != nullptr) {
        node->type(symbol->type());
    } 
    else {
        throw id;
    }
}

void mml::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
    ASSERT_UNSPEC;

    try {
        node->lvalue()->accept(this, lvl);
        node->type(node->lvalue()->type());
    } 
    catch (const std::string &id) {
        throw "undeclared variable '" + id + "'";
    }
}

void mml::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
    ASSERT_UNSPEC;


    node->lvalue()->accept(this, lvl + 2);
    node->rvalue()->accept(this, lvl + 2);

    if (node->lvalue()->is_typed(cdk::TYPE_INT)) {
        if (node->rvalue()->is_typed(cdk::TYPE_INT)) {
            node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        } 
        else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
            node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
            node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
        } 
        else {
            throw std::string("wrong assignment to integer");
        }
    }
    else if (node->lvalue()->is_typed(cdk::TYPE_DOUBLE)) {
        if (node->rvalue()->is_typed(cdk::TYPE_DOUBLE) || node->rvalue()->is_typed(cdk::TYPE_INT)) {
            node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
        } 
        else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {            
            node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
            node->rvalue()->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
        } 
        else {
            throw std::string("wrong assignment to real");
        }
    }
    else if (node->lvalue()->is_typed(cdk::TYPE_STRING)) {
        if (node->rvalue()->is_typed(cdk::TYPE_STRING)) {
            node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
        } else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {           
            node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
            node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
        } else {
            throw std::string("wrong assignment to string");
        }
    }
    else if (node->lvalue()->is_typed(cdk::TYPE_POINTER)) {
        if (node->rvalue()->is_typed(cdk::TYPE_POINTER)) {
           if (!(compatible_pointed_types(node->lvalue()->type(), node->rvalue()->type()))){
            throw std::string("wrong assignment to pointer");
           }
           node->type(node->rvalue()->type());
        }
        else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
            node->rvalue()->type(node->lvalue()->type());
            node->type(node->rvalue()->type());  // Ver melhor
        }
        else{
            throw std::string("wrong assignment to pointer");
        }
    }
    else if (node->lvalue()->is_typed(cdk::TYPE_FUNCTIONAL)) {  
        if (node->rvalue()->is_typed(cdk::TYPE_FUNCTIONAL)) {
            if (!(compatible_function_types(cdk::functional_type::cast(node->lvalue()->type()), cdk::functional_type::cast(node->rvalue()->type())) || 
                    (node->rvalue()->is_typed(cdk::TYPE_POINTER) && cdk::reference_type::cast(node->rvalue()->type())->referenced() == nullptr))) {
                throw std::string("wrong type for initializer (function expected).");
            }
        node->type(node->rvalue()->type());
        } 
        else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {              
            node->type(cdk::primitive_type::create(4, cdk::TYPE_ERROR));
            node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_ERROR));
        } 
        else {
            throw std::string("wrong assignment to function");
        }
    }
    else{
        throw std::string("wrong types in assignment");
    }
}

//---------------------------------------------------------------------------

/*
void mml::type_checker::do_program_node(mml::program_node *const node, int lvl) {
  // EMPTY
}
*/

void mml::type_checker::do_evaluation_node(mml::evaluation_node *const node, int lvl) {
    node->argument()->accept(this, lvl + 2);
}

/*void mml::type_checker::do_print_node(mml::print_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
}
*/


void mml::type_checker::do_write_node(mml::write_node * const node, int lvl){
    node->arguments()->accept(this, lvl + 2);
}


void mml::type_checker::do_input_node(mml::input_node * const node, int lvl){
    node->type(cdk::primitive_type::create(0, cdk::TYPE_UNSPEC));
}

void mml::type_checker::do_block_node(mml::block_node * const node, int lvl){}


//---------------------------------------------------------------------------

/*
void mml::type_checker::do_read_node(mml::read_node *const node, int lvl) {
  try {
    node->argument()->accept(this, lvl);
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  }
} */
// nodes no longer used

//---------------------------------------------------------------------------

void mml::type_checker::do_while_node(mml::while_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
  if(!node->condition()->is_typed(cdk::TYPE_INT))
    throw std::string("expected integer condition");
}

void mml::type_checker::do_with_node(mml::with_node *const node, int lvl) {
  node->identifier()->accept(this, lvl + 4);
  if(!node->identifier()->is_typed(cdk::TYPE_FUNCTIONAL))
    throw std::string("expected function");
  
  node->vector()->accept(this, lvl + 4);
  if(!node->vector()->is_typed(cdk::TYPE_POINTER))
    throw std::string("expected vector");

  auto func_type = cdk::functional_type::cast(node->identifier()->type());
  auto vector_type = cdk::reference_type::cast(node->vector()->type())->referenced();


  if(!compatible_pointed_types(func_type->input(0), func_type->output(0))){
    throw std::string("expected same type between input and output");
  }
  else {
    if(!compatible_pointed_types(func_type->output(0), vector_type)){
        throw std::string("expected same type between vector and function output/input");
    }
  }

  node->low()->accept(this, lvl + 4);
   if(!node->low()->is_typed(cdk::TYPE_INT))
    throw std::string("expected integer");

  node->high()->accept(this, lvl + 4);
   if(!node->high()->is_typed(cdk::TYPE_INT))
    throw std::string("expected integer");

}



//---------------------------------------------------------------------------

void mml::type_checker::do_if_node(mml::if_node *const node, int lvl) {
    node->condition()->accept(this, lvl + 4);
    if(!node->condition()->is_typed(cdk::TYPE_INT))
        throw std::string("expected integer condition");
}

void mml::type_checker::do_if_else_node(mml::if_else_node *const node, int lvl) {
    node->condition()->accept(this, lvl + 4);
    if(!node->condition()->is_typed(cdk::TYPE_INT))
        throw std::string("expected integer condition");
}

//---------------------------------------------------------------------------

void mml::type_checker::do_address_of_node(mml::address_of_node * const node, int lvl){
    ASSERT_UNSPEC;
    node->lvalue()->accept(this, lvl + 2);
    node->type(cdk::reference_type::create(4, node->lvalue()->type()));
}

void mml::type_checker::do_index_node(mml::index_node * const node, int lvl){
    ASSERT_UNSPEC;
    
    node->base()->accept(this, lvl + 2);
    
    if(node->base()->is_typed(cdk::TYPE_UNSPEC)){
        node->base()->type(cdk::reference_type::create(4, cdk::primitive_type::create(4, cdk::TYPE_INT)));
    }

    std::shared_ptr<cdk::reference_type> btype = cdk::reference_type::cast(node->base()->type());

    if (!node->base()->is_typed(cdk::TYPE_POINTER)) 
        throw std::string("pointer expression expected in index left-value");

    node->index()->accept(this, lvl + 2);
    if (!node->index()->is_typed(cdk::TYPE_INT)) 
        throw std::string("integer expression expected in left-value index");

    node->type(btype->referenced());
}

void mml::type_checker::do_stack_alloc_node(mml::stack_alloc_node * const node, int lvl){
    ASSERT_UNSPEC;
    
    node->argument()->accept(this, lvl + 2);

    if (!node->argument()->is_typed(cdk::TYPE_INT))
        throw std::string("integer expression expected in allocation expression");
    
    auto mytype = cdk::primitive_type::create(0, cdk::TYPE_UNSPEC);
    node->type(mytype);
}

void mml::type_checker::do_sizeof_node(mml::sizeof_node * const node, int lvl){
    ASSERT_UNSPEC;
    node->expression()->accept(this, lvl + 2);
    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}


//---------------------------------------------------------------------------

void mml::type_checker::do_declaration_node(mml::declaration_node * const node, int lvl){

    if (node->initializer() != nullptr) {
        node->initializer()->accept(this, lvl + 2);
        if (node->type()) {
            if (node->is_typed(cdk::TYPE_INT)) {
                if (!node->initializer()->is_typed(cdk::TYPE_INT)) 
                    throw std::string("wrong type for initializer (integer expected).");
            }
            else if (node->is_typed(cdk::TYPE_DOUBLE)) {
                if (!node->initializer()->is_typed(cdk::TYPE_INT) && !node->initializer()->is_typed(cdk::TYPE_DOUBLE)) {
                    throw std::string("wrong type for initializer (integer or double expected).");
                }
            }
            else if (node->is_typed(cdk::TYPE_STRING)) {
                if (!node->initializer()->is_typed(cdk::TYPE_STRING)) {
                    throw std::string("wrong type for initializer (string expected).");
                }
            }
            else if (node->is_typed(cdk::TYPE_POINTER)) {
                
                if(node->initializer()->is_typed(cdk::TYPE_UNSPEC)){
                    node->initializer()->type(node->type());
                }

                if (!(node->initializer()->is_typed(cdk::TYPE_POINTER) && compatible_pointed_types(node->type(), node->initializer()->type()))) {
                    throw std::string("wrong type for initializer (pointer expected).");
                }
            }
            else if (node->is_typed(cdk::TYPE_FUNCTIONAL)) {
                if (!((node->initializer()->is_typed(cdk::TYPE_FUNCTIONAL) && 
                    compatible_function_types(cdk::functional_type::cast(node->type()), 
                        cdk::functional_type::cast(node->initializer()->type()))) || 
                    ((node->initializer()->is_typed(cdk::TYPE_POINTER) && 
                    cdk::reference_type::cast(node->initializer()->type())->referenced() == nullptr)))) {
                        throw std::string("wrong type for initializer (function expected).");
                }
            }
            else{
                throw std::string("unknown type for initializer.");
            }
        }
        else {
            node->type(node->initializer()->type());
        }
    }

    std::string id = node->identifier();
    auto symbol = mml::make_symbol(node->type(), id, (bool)node->initializer(), node->qualifier());
    std::shared_ptr<mml::symbol> previous = _symtab.find_local(symbol->name());

    

    if (previous) {
        _symtab.replace(symbol->name(), symbol);
    }
    else {
        _symtab.insert(id, symbol);
    }
  

    _parent->set_new_symbol(symbol);
}

//---------------------------------------------------------------------------

void mml::type_checker::do_function_call_node(mml::function_call_node * const node, int lvl){
    ASSERT_UNSPEC;
    std::vector<std::shared_ptr<cdk::basic_type>> input_types;
    std::shared_ptr<cdk::basic_type> output_type;
    
    if(!node->identifier()) {
        auto symbol = _symtab.find("@", 1);
        if (symbol == nullptr) {
            throw std::string("recursive call outside function");
        }
        if (symbol->is_main()) {
            throw std::string("recursive call in main function");
        }
        input_types = cdk::functional_type::cast(symbol->type())->input()->components();
        output_type = cdk::functional_type::cast(symbol->type())->output(0);
    }
    else {
        node->identifier()->accept(this, lvl + 2);    
        if (!(node->identifier()->type()->name() == cdk::TYPE_FUNCTIONAL)) {
            throw std::string("expected function pointer on function call");
        }
        input_types = cdk::functional_type::cast(node->identifier()->type())->input()->components();
        output_type = cdk::functional_type::cast(node->identifier()->type())->output(0);
    }

    if (node->arguments()->size() == input_types.size()) {
        node->arguments()->accept(this, lvl + 4);
        for (size_t ax = 0; ax < node->arguments()->size(); ax++) {
            if (node->argument(ax)->type()->name() == input_types[ax]->name()) 
                continue;
            if (input_types[ax]->name() == cdk::TYPE_DOUBLE && node->argument(ax)->is_typed(cdk::TYPE_INT)) 
                continue;
            throw std::string("type mismatch for argument " + std::to_string(ax + 1) + ".");
        }
    }
    else {
        throw std::string(
            "number of arguments in call (" + std::to_string(node->arguments()->size()) + ") must match declaration ("
                + std::to_string(input_types.size()) + ").");
    }

    node->type(output_type);
}

void mml::type_checker::do_function_definition_node(mml::function_definition_node * const node, int lvl){
    auto function = mml::make_symbol(node->type(), "@", 0, tPRIVATE);

    if(node->isMain()){
        function->set_main();
    }

    if (_symtab.find_local(function->name())) {
        _symtab.replace(function->name(), function);
    } else {
        if (!_symtab.insert(function->name(), function)) {
            return;
        }
    }

    _parent->set_new_symbol(function);

}



void mml::type_checker::do_return_node(mml::return_node * const node, int lvl){
    auto symbol = _symtab.find("@", 1);
    if (symbol == nullptr){ 
        throw std::string("return statement outside function block");
    }
    else{
        if (node->retval()) {
            std::shared_ptr<cdk::functional_type> rettype = cdk::functional_type::cast(symbol->type());
            if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_VOID) {
                throw std::string("return value specified for void function.");
            }
        
            node->retval()->accept(this, lvl + 2);

            if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_INT) {
                if (!node->retval()->is_typed(cdk::TYPE_INT)) {
                    throw std::string("wrong type for return expression (integer expected).");
                }
            }
            else if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_DOUBLE) {
                if (!node->retval()->is_typed(cdk::TYPE_INT) && !node->retval()->is_typed(cdk::TYPE_DOUBLE)) {
                    throw std::string("wrong type for return expression (integer or double expected).");
                }
            }
            else if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_STRING) {
                if (!node->retval()->is_typed(cdk::TYPE_STRING)) {
                    throw std::string("wrong type for return expression (string expected).");
                }
            }
            else if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_POINTER) {
                if (node->retval()->is_typed(cdk::TYPE_POINTER)) {
                    if (!(compatible_pointed_types(rettype->output(0), node->retval()->type()))) {
                        throw std::string("wrong type for return expression (pointer expected).");
                    }
                }
            }
            else if (rettype->output() != nullptr && rettype->output(0)->name() == cdk::TYPE_FUNCTIONAL) {
                node->retval()->accept(this, lvl + 2);
                if (node->retval()->is_typed(cdk::TYPE_FUNCTIONAL)) {
                    if (!(compatible_function_types(cdk::functional_type::cast(rettype->output(0)), 
                        cdk::functional_type::cast(node->retval()->type())) || 
                        (node->retval()->is_typed(cdk::TYPE_POINTER) && 
                        cdk::reference_type::cast(node->retval()->type())->referenced() == nullptr))) {
                            throw std::string("wrong type for return expression (function expected).");
                    }
                }
            }
            else {
                throw std::string("unknown type for return expression.");
            }
        }
    }
}

//---------------------------------------------------------------------------

void mml::type_checker::do_stop_node(mml::stop_node * const node, int lvl){}

void mml::type_checker::do_next_node(mml::next_node * const node, int lvl){}

//---------------------------------------------------------------------------
