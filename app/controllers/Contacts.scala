package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Contacts extends Controller with Secured {

  val contactForm = Form(
    tuple(
      "name" -> text,
      "surname" -> text,
      "genre" -> text,
      "telephone" -> text))

  def create(uId: Long) = IsAuthenticated { email =>
    implicit request =>
      contactForm.bindFromRequest.fold(
        errors => BadRequest("Petó"),
        {
          case (name, surname, genre, telephone) =>
            val user = User.findByEmail(email).get
            if (Application.isManagerOf(user)) {
              val contact = Contact.create(
                Contact(None, name, surname, genre, telephone, user))
              Redirect(routes.Contacts.listByUser(contact.user.id.get))
            } else Results.Forbidden("Prohibido")
        })
  }

  def get(id: Long) = IsAuthenticated { email =>
    implicit request =>
      val contact = Contact.findById(id).get
      if (Application.isManagerOf(contact.user)) {
        Ok("get")
      } else Results.Forbidden("Prohibido")
  }

  def listByUser(uId: Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        val contacts = Contact.findByUser(user)
        Ok(html.contacts.index(user.id.get, contacts))
      } else Results.Forbidden("Prohibido")
  }

  def update(id: Long) = IsAuthenticated { email =>
    implicit request =>
      contactForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (name, surname, genre, telephone) =>
            val contact = Contact.findById(id).get
            if (Application.isManagerOf(contact.user)) {
              Contact.update(Contact(Some(id), name, surname, genre, telephone, User.findByEmail(email).get))
              Redirect(routes.Contacts.listByUser(contact.user.id.get))
            } else Results.Forbidden("Prohibido")
        })
  }

  def delete(id: Long) = IsAuthenticated { email =>
    implicit request =>
      val contact = Contact.findById(id).get
      if (Application.isManagerOf(contact.user)) {
        Contact.delete(id)
        Redirect(routes.Contacts.listByUser(contact.user.id.get))
      } else Results.Forbidden("Prohibido")
  }

  def createForm(id: Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(id).get
      if (Application.isManagerOf(user)) {
        Ok(html.contacts.create(id))
      } else NotFound("404")
  }

  def updateForm(id: Long) = Action {
    implicit request =>
      Contact.findById(id) match {
        case Some(contact) => Ok(html.contacts.update(Contact.findById(id).get))
        case _ => NotFound("404")
      }
  }
}