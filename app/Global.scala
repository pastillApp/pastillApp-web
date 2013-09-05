import play.api._
import models._
import anorm._
import java.util.Date

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
      Seq(
        User(None, "info@pastillapp.com", "admin", "Señor Palo", Option("Piedra"), "O2 Arena", "33006", "00349852323"),
        User(None, "intern@pastillapp.com", "admin", "Señor Piedra", Option("Piedra"), "O2 Arena", "33006", "00349852323"),
        User(None, "ceo@pastillapp.com", "admin", "Señor Caja", Option("Piedra"), "O2 Arena", "33006", "00349852323")).foreach(User.create)

    }

    if (Dose.findAll.isEmpty) {
      Seq(
        Dose(None, "dormidina", "2", "pastilla", 480, new Date(1378404155), new Date(1378404155),  User.findByEmail("info@pastillapp.com").get),
        Dose(None, "aspirina", "1", "pastilla", 480, new Date(1378404155), new Date(1378404155), User.findByEmail("intern@pastillapp.com").get),
        Dose(None, "jarabe", "2", "cucharaditas", 720, new Date(1378404155), new Date(1378404155), User.findByEmail("ceo@pastillapp.com").get)).foreach(Dose.create)
    }

    if (Contact.findAll.isEmpty) {
      Seq(
        Contact(None, "Pedro", "Picapiedra", "C", "695", User.findByEmail("info@pastillapp.com").get),
        Contact(None, "Vilma", "Picapiedra", "C", "695", User.findByEmail("info@pastillapp.com").get),
        Contact(None, "Mono", "Picapiedra", "C", "695", User.findByEmail("info@pastillapp.com").get)).foreach(Contact.create)
    }

  }

}