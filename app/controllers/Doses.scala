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
  def create(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (medicine, amount, measure, user_id) =>
            val user = User.findById(user_id).get
            if (Application.isManagerOf(user)) {
              val dose = Dose.create(
                Dose(None, medicine, amount, measure, user))
              Ok("")
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
        Ok("deleted")
      } else Results.Forbidden
  }
  
  /**
   * List all doses by an user Id
   */
  def listByUser(uId: Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        //Dose.delete(uId)
        Dose.findByUser(user)
        Ok("listed")
      } else Results.Forbidden
  }
  
  def get(dId: Long) = IsAuthenticated { username => 
    implicit request =>
      val dose = Dose.findById(dId).get
      if(Application.isManagerOf(dose.user)) {
        Ok("get")
      } else Results.Forbidden        
  }
  
  def edit(dId: Long) = IsAuthenticated { username =>
    implicit request =>
      doseForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (medicine, amount, measure, user_id) =>
            val dose = Dose.findById(dId).get
            if (Application.isManagerOf(dose.user)) {
              Dose.update(dId, dose)
              Ok("edited")
            } else Results.Forbidden
        })   
  }

}