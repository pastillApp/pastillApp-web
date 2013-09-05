package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Contacts extends Controller with Secured{

  val contactForm = Form(
    tuple(
      "name" -> text,
      "surname" -> text,
      "genre" -> text,
      "telephone" -> text,
      "user_id" -> number))
      
      
  def create = IsAuthenticated { username =>
    implicit request =>
      contactForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (name, surname, genre, telephone, user_id) =>
            val user = User.findById(user_id).get
            if (Application.isManagerOf(user)) {
              val contact = Contact.create(
                Contact(None, name, surname, genre, telephone, user))
              Ok("create")
            } else Results.Forbidden
        })
  }
  
  def get(id:Long) = IsAuthenticated { username => 
    implicit request =>
      val contact = Contact.findById(id).get
      if(Application.isManagerOf(contact.user)) {
        Ok("get")
      } else Results.Forbidden
    
  }
  
  def listByUser(uId:Long) = IsAuthenticated { username =>
    implicit request =>
      val user = User.findById(uId).get
      if (Application.isManagerOf(user)) {
        //Dose.delete(uId)
        Contact.findByUser(user)
        Ok("listed")
      } else Results.Forbidden
  }
  
  def edit(id:Long) = IsAuthenticated { username =>
    implicit request =>
      contactForm.bindFromRequest.fold(
        errors => BadRequest,
        {
          case (name, surname, genre, telephone, user_id) =>
            val contact = Contact.findById(id).get
            if (Application.isManagerOf(contact.user)) {
              Contact.update(id, contact)
              Ok("edited")
            } else Results.Forbidden
        })   
  }
  
  def delete(id:Long) = IsAuthenticated { username =>
    implicit request =>
      val contact = Contact.findById(id).get
      if (Application.isManagerOf(contact.user)) {
        Contact.delete(id)
        Ok("deleted")
      } else Results.Forbidden
  }
}