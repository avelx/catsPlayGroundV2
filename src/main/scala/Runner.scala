import cats.data.EitherT

import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global


def someResultA(x: Int) : EitherT[Future, Throwable, String] = {
  EitherT {
    Future {
      if (x > 10) {
        Right(x.toString)
      }
      else
        Left(new Error(s"Value below expected: $x"))
    }
  }
}

def someResultC(y: Int) : Future[Either[Throwable, String]] = {
  Future{
    if (y > 100) {
      Right(y.toString)
    }
    else
      Left(new Error(s"Value below expected: $y"))
  }
}


import scala.concurrent.duration._

object Runner extends App {
 println("Data test ...")

  val finalRes = for {
    x <- EitherT( someResultA(11) )
    y <- EitherT( someResultC(3) )
  } yield s"Here is data: $x - $y"

  val s = Await.result(finalRes.value, 1.seconds)
  println(s)
}
