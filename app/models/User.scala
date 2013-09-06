package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id: Option[Long], email: String, password: String, name: String, surname: Option[String], address: String, zip:String, telephone: String)

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
      get[String]("users.zip_code") ~
      get[String]("users.telephone") map {
        case id ~ email ~ password ~ name ~ surname ~ address ~ zip ~ telephone=> User(Option(id), email, password, name, Option(surname), address, zip, telephone)
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
          insert into users (email, password, name, surname, address, zip_code, telephone) values (
          {email}, {password}, {name}, {surname}, {address}, {zip_code}, {telephone}
          )
        """).on(
          'email -> user.email,
          'password -> user.password,
          'name -> user.name,
          'surname -> user.surname,
          'address -> user.address,
          'zip_code -> user.zip,
          'telephone -> user.telephone
          ).executeInsert()

      user

    }
  }

  /**
   * Create a User.
   */
  def update(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update users set email = {email}, password = {password}, name = {name}, 
          surname = {surname}, address = {address}, zip_code = {zip_code}, telephone = {telephone} where id = {id}
        """).on(
          'email -> user.email,
          'password -> user.password,
          'name -> user.name,
          'surname -> user.surname,
          'address -> user.address,
          'zip_code -> user.zip,
          'telephone -> user.telephone,
          'id -> user.id.get).executeUpdate()

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
  
  def getManagersByManageeId(id:Long) : Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("""select users.id, email, name, surname, address, telephone, password, zip_code from users, manage where users.id=manage.manager and manage.managee = {id}""").on(
        'id -> id).as(User.simple *)    
    }
  }
  
  def getManageesByManagerId(id:Long) : Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("""select users.id, email, name, surname, address, telephone, password, zip_code from users, manage where users.id=manage.managee and manage.manager = {id}""").on(
        'id -> id).as(User.simple *)    
    }
  }

}