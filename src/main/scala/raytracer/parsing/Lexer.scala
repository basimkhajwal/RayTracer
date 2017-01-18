package raytracer.parsing

import java.io.{FileReader, StreamTokenizer}

/**
  *
  * Tokenise the input and perform lexical analysis
  * on each token
  *
  * Created by Basim on 12/01/2017.
  */
class Lexer(val fileName: String) {

  private val fin = new FileReader(fileName)
  private val tokenize = new StreamTokenizer(fin)

  tokenize.commentChar('#')

  def currentLine: Int = tokenize.lineno

  def nextToken(): Option[String] = tokenize.nextToken match {
    case StreamTokenizer.TT_WORD => Some(tokenize.sval)
    case '"' => Some(tokenize.sval)
    case StreamTokenizer.TT_NUMBER => Some(tokenize.nval.toString)
    case c => Some(c.toChar.toString)
    case StreamTokenizer.TT_EOL => nextToken
    case StreamTokenizer.TT_EOF => None
  }

}
