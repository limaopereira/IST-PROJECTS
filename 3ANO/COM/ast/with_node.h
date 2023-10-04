#ifndef __MML_AST_WITH_NODE_H__
#define __MML_AST_WITH_NODE_H__

#include <cdk/ast/expression_node.h>

namespace mml {

  /**
   * Class for describing with-cycle nodes.
   */
  class with_node: public cdk::basic_node {
    cdk::expression_node *_identifier;
    cdk::expression_node *_vector;
    cdk::expression_node *_low;
    cdk::expression_node *_high;  

  public:
    inline with_node(int lineno, cdk::expression_node *identifier, cdk::expression_node *vector, cdk::expression_node *low, cdk::expression_node *high) :
        basic_node(lineno), _identifier(identifier), _vector(vector), _low(low), _high(high){
    }

  public:
    inline cdk::expression_node *identifier() {
      return _identifier;
    }

    inline cdk::expression_node *vector() {
      return _vector;
    }

    inline cdk::expression_node *low() {
      return _low;
    }
    
    inline cdk::expression_node *high() {
      return _high;
    }
    
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_with_node(this, level);
    }

  };

} // mml

#endif
