package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import views._
import models._


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
  def index = Action { Home }

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list())

  def list() = Action { implicit request =>
    Employee.list match {
      case Right(data) => Ok(html.list(data))
      case Left(error) => Ok(html.list(Nil))
    }
  }
  
  /**
   * Display the 'edit form' of a existing Employee.
   *
   * @param id Id of the employee to edit
   */
  def edit(id: Long) = Action {
    Employee.findById(id).map { employee =>
      Ok(html.editForm(id, employeeForm.fill(employee)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the employee to edit
   */
  def update(id: Long) = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors)),
      employee => {
        Employee.update(id, employee)
        Home.flashing("success" -> "Employee %s has been updated".format(employee.name))
      }
    )
  }
  
  /**
   * Display the 'new employee form'.
   */
  def create = Action {
    Ok(html.createForm(employeeForm))
  }
  
  /**
   * Handle the 'new employee form' submission.
   */
  def save = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      employee => {
        Employee.insert(employee)
        Home.flashing("success" -> "Employee %s has been created".format(employee.name))
      }
    )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action {
    Employee.delete(id)
    Home.flashing("success" -> "Computer has been deleted")
  }

}