package raytracer.parsing

import scala.collection.mutable.ListBuffer

/**
  *
  * Tokenise the input and perform lexical analysis
  * on each token
  *
  * Created by Basim on 12/01/2017.
  */
class Lexer(val input: String) {

  type Token = String

  def tokenise: List[Token] = {

    var i = 0
    var currentTokenStart = -1
    var isComment = false
    var isString = false
    val ls: ListBuffer[Token] = new ListBuffer[Token]

    while (i < input.length) {
      val c = input(i)

      if (isComment) {
        if (c == '\n') isComment = false
      }
      else if (c == '#' || (!isString && c.isWhitespace)) {
        if (currentTokenStart != -1) {
          ls append input.substring(currentTokenStart, i)
          currentTokenStart = -1
        }
        if (c == '#') isComment = true
      }
      else if (c == '[' || c == ']') {

        if (currentTokenStart != -1) {
          ls append input.substring(currentTokenStart, i)
        }

        ls append c.toString
        currentTokenStart = -1
      }
      else if (c == '"') {
        if (isString) {
          ls append input.substring(currentTokenStart, i+1)
          currentTokenStart = -1
          isString = false
        } else {
          isString = true
          currentTokenStart = i
        }
      }
      else if (currentTokenStart == -1) {
        currentTokenStart = i
      }

      i += 1
    }

    if (currentTokenStart != -1) ls append input.substring(currentTokenStart)
    ls.toList
  }


}
