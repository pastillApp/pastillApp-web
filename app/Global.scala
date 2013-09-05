import play.api._

import models._
import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    InitialData.insert()
  }

}

/**
 * Initial set of data to be imported
 * in the sample application.
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {

    if (User.findAll.isEmpty) {
      User(None, "info@pastillapp.com", "admin", "Señor Palo", Option("Piedra"), "O2 Arena", "33006", "00349852323")
      Seq(
        User(None, "info@pastillapp.com", "admin", "Señor Palo", Option("Piedra"), "O2 Arena", "33006", "00349852323"),
        User(None, "intern@pastillapp.com", "admin", "Señor Piedra", Option("Piedra"), "O2 Arena", "33006", "00349852323"),
        User (None, "ceo@pastillapp.com", "admin", "Señor Caja", Option("Piedra"), "O2 Arena", "33006", "00349852323")
      ).foreach(User.create)

    }

  }

}