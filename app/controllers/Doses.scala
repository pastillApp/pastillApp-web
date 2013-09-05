package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date

import models._
import views._

object Doses extends Controller with Secured {

  val doseForm = Form(
    tuple(
      "medicine" -> text,
      "amount" -> text,
      "measure" -> text,
      "period" -> number,
      "user_id" -> number))

  /**
   * Add a project.
   */
  def create(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest("Petó"),
        {
          case (medicine, amount, measure, period, user_id) =>
            val user = User.findById(user_id).get
            if (Application.isManagerOf(user)) {
              val date = new Date
              val dose = Dose.create(
                Dose(None, medicine, amount, measure, period, date, date, user))
              Redirect(routes.Application.index)
            } else Results.Forbidden
        })
  }

  def update(dId: Long) = IsAuthenticated { username =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest("Petó"),
        {
          case (medicine, amount, measure, period, user_id) =>
            val pDose = Dose.findById(dId).get
            val dose = Dose(Some(dId), medicine, amount, measure,  period, pDose.created, pDose.updated, User.findById(user_id).get)
            if (Application.isManagerOf(dose.user)) {
              Dose.update(dose)
              Redirect(routes.Application.index)
            } else Results.Forbidden
        })
  }

  /**
   * Add a project.
   */
  def delete(id: Long) = IsAuthenticated { username =>
    implicit request =>
      val dose = Dose.findById(id).get
      if (Application.isManagerOf(dose.user)) {
        Dose.delete(id)
        Redirect(routes.Application.index)
      } else Results.Forbidden("Prohibido")
  }
  
  /**
   * List all doses by an user Id
   */
  def listByUser(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        Ok(html.doses.index(user.id.get, Dose.findByUser(user)))
      } else Results.Forbidden("Prohibido")
  }

  def get(dId: Long) = IsAuthenticated { username =>
    implicit request =>
      val dose = Dose.findById(dId).get
      if(Application.isManagerOf(dose.user)) {
        Ok("get")
      } else Results.Forbidden("Prohibido")
  }

  def createForm(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        Ok(html.doses.create(uId))
      } else NotFound("404")
  }

  def updateForm(dId: Long) = Action {
    implicit request =>
      Dose.findById(dId) match {
        case Some(dose) => Ok(html.doses.update(dose))
        case _ => NotFound("404")
      }
  }

  def retrieveDosesByUser(uId:Long, last:Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if(Application.isManagerOf(user)) {
        Dose.retrieveLastByUser(uId, last)
        Ok("")
      } else Results.Forbidden("Prohibido")  
  }


}