package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import models._
import views._
import play.api.libs.json.Json
import scala.collection.mutable.ListBuffer
import play.api.libs.json.JsValue

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
  def create(uId: Long) = IsAuthenticated { email =>
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

  def update(dId: Long) = IsAuthenticated { email =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest("Petó"),
        {
          case (medicine, amount, measure, period, user_id) =>
            val pDose = Dose.findById(dId).get
            val dose = Dose(Some(dId), medicine, amount, measure, period, pDose.created, pDose.updated, User.findById(user_id).get)
            if (Application.isManagerOf(dose.user)) {
              Dose.update(dose)
              Redirect(routes.Application.index)
            } else Results.Forbidden
        })
  }

  /**
   * Add a project.
   */
  def delete(id: Long) = IsAuthenticated { email =>
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
  def listByUser(uId: Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        Ok(html.doses.index(user.id.get, Dose.findByUser(user)))
      } else Results.Forbidden("Prohibido")
  }

  def get(dId: Long) = IsAuthenticated { email =>
    implicit request =>
      val dose = Dose.findById(dId).get
      if (Application.isManagerOf(dose.user)) {
        Ok("get")
      } else Results.Forbidden("Prohibido")
  }

  def createForm(uId: Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(uId).get
      val managees = User.getManageesByManagerId(user.id.get) ++ List(user)
      if (Application.isManagerOf(user)) {
        Ok(html.doses.create(uId, managees))
      } else NotFound("404")
  }

  def updateForm(dId: Long)= IsAuthenticated { email =>
    implicit request =>
      Dose.findById(dId) match {
        case Some(dose) =>
          val user = User.findByEmail(email).get
          val managees = User.getManageesByManagerId(user.id.get) ++ List(user)
          Ok(html.doses.update(dose, managees))
        case _ => NotFound("404")
      }
  }

  def retrieveDosesByUser(uId: Long, last: Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        val doses = Dose.retrieveLastByUser(uId, last)
        println(doses)
        val list = new ListBuffer[JsValue]
        doses.foreach(dose => {
          var obj = Json.obj(
              "id" -> dose.id.get,
              "medicine" -> dose.medicine,
              "amount" -> dose.amount, 
              "measure" -> dose.measure,
              "period" -> dose.period)
          list += obj
        })
        Ok(Json.obj("doses" -> Json.arr(list)))
      } else Results.Forbidden("Prohibido")
  }

}