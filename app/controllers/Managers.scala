package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Managers extends Controller with Secured{
  
  val managerForm = Form(
      "email" -> text)

  def listByManagee(uId:Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if(Application.isManagerOf(user)) {
        User.getManagersByManageeId(uId)
        Ok("Listed managers")
      } else Results.Forbidden     
        
  }
  
  def listByManager(uId:Long) = IsAuthenticated {username =>
  	implicit request => 
  	  val user = User.findById(uId).get
  	  val aUser = User.findByEmail(username).get
  	  if(user.id == aUser.id) {
  	    User.getManageesByManagerId(uId)
  	    Ok("Listed managees")
  	  } else Results.Forbidden
  }
  
  def createForm(uId:Long) = IsAuthenticated {username =>
    implicit request =>
      val user = User.findById(uId).get
      //val managees = User.getManageesByManagerId(user.id.get) ++ List(user)
      if (Application.isManagerOf(user)) {
        Ok(html.managers.create())
      } else NotFound("404")
  }
  
  def create(uId:Long) = IsAuthenticated {username =>
    implicit request => 
      val manager = User.findByEmail(username).get
      //TODO Faltaria genererar una confirmacion
       managerForm.bindFromRequest.fold(
        errors => BadRequest("Petó"),
        {
          case email =>
            val managee = User.findByEmail(email).get 
            User.addManagee(managee.id.get, manager.id.get)
            /*if (Application.isManagerOf(user)) {
              val date = new Date
              val dose = Dose.create(
                Dose(None, medicine, amount, measure, period, date, date, user))
              Redirect(routes.Application.index)
            } else Results.Forbidden*/
            Ok(html.managers.index(User.getManageesByManagerId(manager.id.get)))
        })
  }
}