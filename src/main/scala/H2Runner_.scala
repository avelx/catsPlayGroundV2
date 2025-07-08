import java.sql.{Connection, DriverManager, ResultSet, Timestamp}
import java.util.Calendar
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer

// H2 connection: failing due to concurrency issues

def createDbStructure(conn: Connection)(implicit ec: ExecutionContext): Future[Unit] = {
  val sql =
    """
      create schema if not exists state;
      set schema state;
      create table if not exists dump (
        id int auto_increment primary key,
        db_url varchar(255) not null,
        table_name varchar(255) not null,
        dtm timestamp not null);"""
  val stmt = conn.createStatement()
  Future {
    try {
      stmt.execute(sql)
    } finally {
      stmt.close()
    }
    ()
  }
}

def insertDbData(conn: Connection)(implicit ec: ExecutionContext): Future[Unit] = {
  Future {
    val sqlIns =
      """insert into state.dump(db_url, table_name, dtm)
                                  values (?, ?, ?)"""
    val stmt = conn.prepareStatement(sqlIns)
    stmt.setString(1, conn.getMetaData.getURL)
    stmt.setString(2, "state.dump")
    stmt.setTimestamp(
      3,
      new Timestamp(Calendar.getInstance().getTime.getTime)
    )
    stmt.executeUpdate()
    stmt.close()
    ()
  }
}

case class Data(tableName: String, dtm: String)

 def selectDbData(conn: Connection)(implicit ec: ExecutionContext): Future[List[Data]] = {
   println(s"DatA")
   Future {
     val sqlIns =
       """select db_url, table_name, dtm from state.dump"""
     val stmt = conn.prepareStatement(sqlIns)
     val res: ResultSet = stmt.executeQuery()
     val buff : ListBuffer[Data] = ListBuffer()
     while ( {
       res.next
     }) {
       val db_url = res.getString("db_url")
       val table_name = res.getString("table_name")
       val dtm = res.getTimestamp("dtm")
       //println(db_url + "Here is the table name:=>" + table_name + " " + dtm.toString)
       buff.append( Data(tableName = table_name, dtm = dtm.toString) )
     }
     stmt.close()
     val r = buff.toList
     println(s"Data: $r")
     r
   }
}

def deleteDbData(conn: Connection, id: Int)(implicit ec: ExecutionContext): Future[Unit] = {
  Future {
    val sqlDel = "delete from state.dump where id = " + id.toString
    val stmt = conn.prepareStatement(sqlDel)
    stmt.execute()
    stmt.close()
    ()
  }
}

import scala.concurrent.duration._
object Runner2  {
  Class.forName("org.h2.Driver")
  val conn: Connection = DriverManager.getConnection("jdbc:h2:./db/h2", "sa", "")
  try {

    val res = for {
      _ <- deleteDbData(conn, 2)
      _ <- insertDbData(conn)
      data <- selectDbData(conn)
    } yield println(s"Here is data: ${data}")

    Await.result(res, 1.seconds)

  } finally {
    conn.close()
  }
  println("End of story")
  //
//  val conn: Connection = DriverManager.getConnection("jdbc:h2:./db/h2", "sa", "")
//  try {
//    createDbStructure(conn)
//    println("createDbStructure: done!")
//    insertDbData(conn)
//    println("insertDbData: done!")
//    selectDbData(conn)
//    //deleteDbData(conn, 2)
//  } finally {
//    conn.close()
//  }
}
