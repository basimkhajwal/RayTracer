package raytracer.parsing

import java.io.{FileReader, StreamTokenizer}

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer

/**
  *
  * Tokenise the input and perform lexical analysis
  * on each token
  *
  * Created by Basim on 12/01/2017.
  */
class Lexer(val fileName: String) {

  private val inputReader = new FileReader(fileName)
  private val tokenize = new StreamTokenizer(inputReader)
  private val peekedTokens: Queue[String] = Queue()

  private var finished = false

  tokenize.commentChar('#')

  def currentLine: Int = tokenize.lineno

  private def getNextToken(): Option[String] = tokenize.nextToken match {
    case StreamTokenizer.TT_WORD => Some(tokenize.sval)
    case '"' => Some(tokenize.sval)
    case StreamTokenizer.TT_NUMBER => Some(tokenize.nval.toString)
    case c => Some(c.toChar.toString)
    case StreamTokenizer.TT_EOL => getNextToken

    case StreamTokenizer.TT_EOF => {
      finished = true
      close()
      None
    }
  }

  def next(): Option[String] = {
    if (finished) None
    else if (peekedTokens.isEmpty) getNextToken
    else Some(peekedTokens.dequeue)
  }

  def peek(i: Int = 0): Option[String] = {
    if (finished) None
    else {
      while (peekedTokens.size <= i) {
        getNextToken match {
          case None => return None
          case Some(s) => peekedTokens enqueue s
        }
      }

      Some(peekedTokens(i))
    }
  }

  def close(): Unit = inputReader.close()
}
