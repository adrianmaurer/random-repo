import org.scalatest._

class LunatechServiceSpec extends FlatSpec with Matchers {

  "LunatechService" should "pop values in last-in-first-out order" in {
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}
