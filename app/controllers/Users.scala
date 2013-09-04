package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Users extends Controller with Secured {

  val userForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> text,
      "name" -> text,
      "surname" -> optional(text),
      "telephone" -> text,
      "address" -> text))

  /**
   * Add a project.
   */
  def add = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (email, password, name, surname, telephone, address) =>
            val user = User.create(
              User(None, email, password, name, surname, telephone, address))
            Ok("")
        })
  }

  /**
   * Delete a project.
   */
  def update(id: Long) = IsAuthenticated { username =>
    implicit request =>
      userForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (email, password, name, surname, telephone, address) =>
            val user = User.update(id,
              User(None, email, password, name, surname, telephone, address))
            Ok("")
        })

  }
  
  /**
   * Delete a project.
   */
  def delete(id: Long) = IsAuthenticated { username =>
    implicit request =>
      request.session.get("id") match {
        case uId if uId == id =>
          User.delete(id)
          Ok
        case _ => Results.Forbidden
      }

  }
}