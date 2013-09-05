package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Contact (id:Long, telephone:String, name:String, surname:String, genre:String, user:User)

object Contact {
	// -- Parsers
  
  /**
   * Parse a Contact from a ResultSet
   */
  val simple = {
    get[Long]("contacts.id") ~
    get[String]("contacts.telephone") ~
    get[String]("contacts.name") ~
    get[String]("contacts.surname") ~
    get[String]("contacts.genre") ~
    get[Long]("contacts.user_id") map {
      case id~telephone~name~surname~genre~userId => Contact(id, telephone, name, surname, genre, User.findById(userId).get)
    }
  }
  
  // -- Queries

  /**
   * Retrieve a Contact from id.
   */
  def findById(id: Long): Option[Contact] = {
    DB.withConnection { implicit connection =>
      SQL("select * from contacts where id = {id}").on(
        'id -> id
      ).as(Contact.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve a Contact from user.
   */
  def findByUser(user: User): Seq[Contact] = {
    DB.withConnection { implicit connection =>
      SQL("select * from contacts where user_id = {user_id}").on(
        'user_id -> user.id
      ).as(Contact.simple *)
    }
  }
  
  /**
   * Create a Contact.
   */
  def create(contact: Contact): Contact = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into contacts values (
          {telephone}, {name}, {surname}, {genre}, {user_id}
          )
        """
      ).on(
        'telephone -> contact.telephone,
        'name -> contact.name,
        'surname -> contact.surname,
        'genre -> contact.genre,
        'user_id -> contact.user.id
      ).executeUpdate()
      
      contact
      
    }
  }
  
  /**
   * Update a Contact.
   */
  def update(id: Long, contact: Contact): Contact = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update contacts values set telephone = {telephone}, name = {name}, surname = {surname}, 
          genre = {genre}, user_id = {user_id} where id = {id}
          )
        """).on(
          'telephone -> contact.telephone,
          'name -> contact.name,
          'surname -> contact.surname,
          'genre -> contact.genre,
          'user_id -> contact.user.id,
          'id -> id).executeUpdate()

      contact

    }
  }

  /**
   * Delete a User.
   */
  def delete(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from contacts where id = {id}").on(
        'id -> id).executeUpdate()
    }
  }
  
  /**
   * Retrieve all contacts.
   */
  def findAll: Seq[Contact] = {
    DB.withConnection { implicit connection =>
      SQL("select * from contacts").as(Contact.simple *)
    }
  }
}