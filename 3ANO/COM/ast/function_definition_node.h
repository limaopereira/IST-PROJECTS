#ifndef __MML_AST_FUNCTION_DEFINITION_NODE_H__
#define __MML_AST_FUNCTION_DEFINITION_NODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>
#include <cdk/types/basic_type.h>
#include "ast/block_node.h"

namespace mml {

  class function_definition_node: public cdk::expression_node {
    cdk::sequence_node *_arguments;
    mml::block_node *_block;
    bool _isMain;

  public:
    function_definition_node(int lineno, cdk::sequence_node *arguments, mml::block_node *block, bool isMain = false) :
        cdk::expression_node(lineno), _arguments(arguments), _block(block), _isMain(isMain) {
      type(cdk::primitive_type::create(0, cdk::TYPE_VOID));
    }

    function_definition_node(int lineno, std::shared_ptr<cdk::basic_type> funType, cdk::sequence_node *arguments, mml::block_node *block, bool isMain = false) :
        cdk::expression_node(lineno), _arguments(arguments), _block(block), _isMain(isMain) {
      type(cdk::functional_type::create(getArgumentsVector(), funType));
    }

  public:
    cdk::sequence_node* arguments() {
      return _arguments;
    }
    cdk::typed_node* argument(size_t ax) {
      return dynamic_cast<cdk::typed_node*>(_arguments->node(ax));
    }
    mml::block_node* block() {
      return _block;
    }
    bool isMain(){
        return _isMain;
    }
    std::vector<std::shared_ptr<cdk::basic_type>> getArgumentsVector(){
        std::vector<std::shared_ptr<cdk::basic_type>> argtypes;
        if(arguments()){
            for (size_t ax = 0; ax < arguments()->size(); ax++) {
                argtypes.push_back(argument(ax)->type());
            }
        }
        return argtypes;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_definition_node(this, level);
    }

  };

} // mml

#endif