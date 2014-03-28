package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._
import play.api.libs.concurrent.Promise
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._

object Application extends Controller {
  
  /**
   * Describe the employee form (used in both edit and create screens).
   */ 
  val employeeForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> date("yyyy-MM-dd"),
      "joiningDate" -> date("yyyy-MM-dd"),
      "designation" -> nonEmptyText
    )(Employee.apply)(Employee.unapply)
  )

  /**
   * Handle default path requests, redirect to employee list
   */
  def index = Action.async { Promise.timeout(Home, 1 seconds) }

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list())

  def list() = Action.async { implicit request =>
    Employee.list match {
      case Right(data) => Promise.timeout(Ok(html.list(data)), 1 seconds)
      case Left(error) => Promise.timeout(Ok(html.list(Nil)), 1 seconds)
    }
  }
  
  /**
   * Display the 'edit form' of a existing Employee.
   *
   * @param id Id of the employee to edit
   */
  def edit(id: Long) = Action.async {
    Employee.findById(id).map { employee =>
      Promise.timeout(Ok(html.editForm(id, employeeForm.fill(employee))), 1 seconds)
    }.getOrElse(Promise.timeout(NotFound, 1 seconds))
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the employee to edit
   */
  def update(id: Long) = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => {Promise.timeout(BadRequest(html.editForm(id, formWithErrors)), 1 seconds)},
      employee => {
        Employee.update(id, employee)
        Promise.timeout(Home.flashing("success" -> "Employee %s has been updated".format(employee.name)), 1 seconds)
      }
    )
  }
  
  /**
   * Display the 'new employee form'.
   */
  def create = Action.async {
    Promise.timeout(Ok(html.createForm(employeeForm)), 1 seconds)
  }
  
  /**
   * Handle the 'new employee form' submission.
   */
  def save = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => Promise.timeout(BadRequest(html.createForm(formWithErrors)), 1 seconds),
      employee => {
        Employee.insert(employee)
        Promise.timeout(Home.flashing("success" -> "Employee %s has been created".format(employee.name)), 1 seconds)
      }
    )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action.async {
    Employee.delete(id)
    Promise.timeout(Home.flashing("success" -> "Computer has been deleted"), 1 seconds)
  }

}