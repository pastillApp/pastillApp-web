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
      "address" -> text,
      "zip_code" -> text,
      "telephone" -> text))

  /**
   * Add a project.
   */
  def create = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (email, password, name, surname, address, zip, telephone) =>
            val user = User.create(
              User(None, email, password, name, surname, address, zip, telephone))
            Redirect(routes.Application.index)
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
          case (email, password, name, surname, address, zip, telephone) =>
            val user = User.update(id,
              User(None, email, password, name, surname, address, zip, telephone))
            Redirect(routes.Application.index)
        })

  }

  /**
   * Delete a project.
   */
  def delete(id: Long) = IsAuthenticated { username =>
    implicit request =>
      val currentUser = User.findByEmail(request.session.get("email").get).get
      println(currentUser.id)
      currentUser.id.get match {
        case uId if uId == id =>
          User.delete(id)
          Ok
        case _ => Results.Forbidden
      }

  }
}