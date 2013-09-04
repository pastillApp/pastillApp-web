package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id: Option[Long], email: String, password: String, name: String, surname: Option[String], address: String, telephone: String)

object User {

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[Long]("users.id") ~
      get[String]("users.email") ~
      get[String]("users.password") ~
      get[String]("users.name") ~
      get[String]("users.surname") ~
      get[String]("users.address") ~
      get[String]("users.telephone") map {
        case id ~ email ~ password ~ name ~ surname ~ telephone ~ address => User(Option(id), email, password, name, Option(surname), telephone, address)
      }
  }

  // -- Queries

  /**
   * Retrieve a User from email.
   */
  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id = {id}").on(
        'id -> id).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email = {email}").on(
        'email -> email).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from users where 
         email = {email} and password = {password}
        """).on(
          'email -> email,
          'password -> password).as(User.simple.singleOpt)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into users values (
          {email}, {password}, {name}, {surname}, {address}, {telephone}
          )
        """).on(
          'email -> user.email,
          'password -> user.password,
          'name -> user.name,
          'surname -> user.surname,
          'address -> user.address,
          'telephone -> user.telephone).executeUpdate()

      user

    }
  }

  /**
   * Create a User.
   */
  def update(id: Long, user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update users values set email = {email}, password = {password}, name = {name}, 
          surname = {surname}, address = {address}, telephone = {telephone} where id = {id}
          )
        """).on(
          'email -> user.email,
          'password -> user.password,
          'name -> user.name,
          'surname -> user.surname,
          'address -> user.address,
          'telephone -> user.telephone,
          'id -> id).executeUpdate()

      user

    }
  }

  /**
   * Delete a User.
   */
  def delete(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from users where id = {id}").on(
        'id -> id).executeUpdate()
    }
  }
  
  def getManagerByManageeId(id:Long) : Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("""select users.id, email, name, surname, address, telephone, password, zip_code from users, manage where users.id=manage.manager and manage.managee = {id}""").on(
        'id -> id).as(User.simple *)    
    }
  }
  
  def getManageeByManagerId(id:Long) : Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("""select users.id, email, name, surname, address, telephone, password, zip_code from users, manage where users.id=manage.managee and manage.manager = {id}""").on(
        'id -> id).as(User.simple *)    
    }
  }

}