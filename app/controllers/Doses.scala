package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Doses extends Controller with Secured {

  val doseForm = Form(
    tuple(
      "medicine" -> text,
      "amount" -> text,
      "measure" -> text,
      "user_id" -> number))

  /**
   * Add a project.
   */
  def add(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (medicine, amount, measure, user_id) =>
            val user = User.findById(user_id).get
            if (isManagerOf(user)) {
              val dose = Dose.create(
                Dose(None, medicine, amount, measure, user))
              Ok("")
            } else Results.Forbidden
        })
  }

  /**
   * Add a project.
   */
  def remove(id: Long) = IsAuthenticated { username =>
    implicit request =>
      val dose = Dose.findById(id).get
      if (isManagerOf(dose.user)) {
        Dose.delete(id)
        Ok("")
      } else Results.Forbidden
  }

  private def isManagerOf(user: User)(implicit request: RequestHeader) = {
    val currentUser = User.findByEmail(request.session.get("email").get).get
    println(currentUser.id)
    currentUser.id.get match {
      case uId if uId == user.id.get =>
        true
      case _ => {
        val seq = User.getManageesByManagerId(currentUser.id.get)
        seq.filterNot(_.id == user.id).isEmpty
      }
    }
  }
}