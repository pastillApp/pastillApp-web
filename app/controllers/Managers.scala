package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Managers extends Controller with Secured{
  
  def listByManagee(uId:Long) = IsAuthenticated { email =>
    implicit request =>
      val user = User.findById(uId).get
      if(Application.isManagerOf(user)) {
        User.getManagersByManageeId(uId)
        Ok("Listed managers")
      } else Results.Forbidden     
        
  }
  
  def listByManager(uId:Long) = IsAuthenticated { email =>
  	implicit request => 
  	  val user = User.findById(uId).get
  	  val aUser = User.findByEmail(email).get
  	  if(user.id == aUser.id) {
  	    User.getManageesByManagerId(uId)
  	    Ok("Listed managees")
  	  } else Results.Forbidden
  }
}