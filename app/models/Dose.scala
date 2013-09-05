package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.mvc.RequestHeader

case class Dose(id: Option[Long], medicine: String, amount: String, measure: String, user: User)

object Dose {

  // -- Parsers

  /**
   * Parse a Dose from a ResultSet
   */
  val simple = {
    get[Long]("doses.id") ~
      get[String]("doses.medicine") ~
      get[String]("doses.amount") ~
      get[String]("doses.measure") ~
      get[Long]("doses.user_id") map {
        case id ~ medicine ~ amount ~ measure ~ userId => Dose(Option(id), medicine, amount, measure, User.findById(userId).get)
      }
  }

  // -- Queries

  /**
   * Retrieve a Dose from id.
   */
  def findById(id: Long): Option[Dose] = {
    DB.withConnection { implicit connection =>
      SQL("select * from doses where id = {id}").on(
        'id -> id).as(Dose.simple.singleOpt)
    }
  }

  /**
   * Retrieve a Dose from user.
   */
  def findByUser(user: User): Seq[Dose] = {
    DB.withConnection { implicit connection =>
      SQL("select * from doses where user_id = {user_id}").on(
        'user_id -> user.id).as(Dose.simple *)
    }
  }

  /**
   * Create a Dose.
   */
  def create(dose: Dose): Dose = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into doses (medicine, amount, measure, user_id) values (
          {medicine}, {amount}, {measure}, {user_id}
          )
        """).on(
          'medicine -> dose.medicine,
          'amount -> dose.amount,
          'measure -> dose.measure,
          'user_id -> dose.user.id).executeUpdate()

      dose

    }
  }

  /**
   * Update a User.
   */
  def update(dose: Dose): Dose = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update doses set amount = {amount}, measure = {measure}, medicine = {medicine}, 
          user_id = {user_id} where id = {id}
        """).on(
          'amount -> dose.amount,
          'measure -> dose.measure,
          'medicine -> dose.medicine,
          'user_id -> dose.user.id.get,
          'id -> dose.id.get).executeUpdate()
     dose
    }
  }

  /**
   * Delete a Dose.
   */
  def delete(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from doses where id = {id}").on(
        'id -> id).executeUpdate()
    }
  }

  def findAll(): Seq[Dose] = {
    DB.withConnection { implicit connection =>
      SQL("select * from doses").as(Dose.simple *)
    }
  }

}