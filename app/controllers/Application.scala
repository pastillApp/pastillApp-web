package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller with Secured {
  /**
   * Display the dashboard.
   */
  def index = IsAuthenticated { username =>
    _ =>
      User.findByEmail(username).map { user =>
        Ok("Secured")
      }.getOrElse(Forbidden)
  }
  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => User.authenticate(email, password).isDefined
      }))

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect({
        val aUser = User.findByEmail(user._1).get
        routes.Doses.listByUser(aUser.id.get)
      }).withSession("email" -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

  def isManagerOf(user: User)(implicit request: RequestHeader) = {
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

/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

}