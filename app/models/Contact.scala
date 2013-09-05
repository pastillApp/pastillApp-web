package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Contact (id:Option[Long], name:String, surname:String, genre:String, telephone:String, user:User)

object Contact {
	// -- Parsers
  
  /**
   * Parse a Contact from a ResultSet
   */
  val simple = {
    get[Long]("contacts.id") ~
    get[String]("contacts.name") ~
    get[String]("contacts.surname") ~
    get[String]("contacts.genre") ~
    get[String]("contacts.telephone") ~
    get[Long]("contacts.user_id") map {
      case id~name~surname~genre~telephone~userId => Contact(Option(id), name, surname, genre, telephone, User.findById(userId).get)
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
          insert into contacts (name, surname, genre, user_id, telephone) values (
           {name}, {surname}, {genre}, {user_id}, {telephone}
          )
        """
      ).on(
        'name -> contact.name,
        'surname -> contact.surname,
        'genre -> contact.genre,
        'telephone -> contact.telephone,
        'user_id -> contact.user.id
      ).executeUpdate()
      
      contact
      
    }
  }
  
  /**
   * Update a Contact.
   */
  def update(contact: Contact): Contact = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update contacts set name = {name}, surname = {surname}, 
          genre = {genre}, telephone = {telephone}, user_id = {user_id} where id = {id}
          )
        """).on(
          'name -> contact.name,
          'surname -> contact.surname,
          'genre -> contact.genre,
          'user_id -> contact.user.id,
          'telephone -> contact.telephone,
          'id -> contact.id.get).executeUpdate()

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