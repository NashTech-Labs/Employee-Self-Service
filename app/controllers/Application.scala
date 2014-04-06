package controllers

import scala.concurrent.Future
import scala.concurrent.duration._

import anorm._
import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.mvc._
import views._

object Application extends Controller {

  /**
   * Describe the employee form (used in both edit and create screens).
   */
  val employeeForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> date("yyyy-MM-dd"),
      "joiningDate" -> date("yyyy-MM-dd"),
      "designation" -> nonEmptyText)(Employee.apply)(Employee.unapply))

  /**
   * Handle default path requests, redirect to employee list
   */
  def index = Action { Home }

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list())

  def list() = Action.async { implicit request =>
    val futureEmpList: Future[List[Employee]] = scala.concurrent.Future { Employee.list }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 10.second)
    Future.firstCompletedOf(Seq(futureEmpList, timeoutFuture)).map {
      case data: List[Employee] => Ok(html.list(data))
      case t: String =>
        Logger.error("Problem found in employee list process")
        InternalServerError(t)
    }
  }

  /**
   * Display the 'edit form' of a existing Employee.
   *
   * @param id Id of the employee to edit
   */
  def edit(id: Long) = Action.async {
    val futureEmp: Future[Option[models.Employee]] = scala.concurrent.Future { Employee.findById(id) }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 10.second)
    Future.firstCompletedOf(Seq(futureEmp, timeoutFuture)).map {
      case employee: Option[Employee] =>
        employee.map { emp =>
          Ok(html.editForm(id, employeeForm.fill(emp)))
        }.getOrElse(NotFound)
      case t: String =>
        Logger.error("Problem found in employee edit process")
        InternalServerError(t)
    }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the employee to edit
   */
  def update(id: Long) = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => { Promise.timeout(BadRequest(html.editForm(id, formWithErrors)), 10 seconds) },
      employee => {
        val futureUpdateEmp: Future[Int] = scala.concurrent.Future { Employee.update(id, employee) }
        val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 10.second)
        Future.firstCompletedOf(Seq(futureUpdateEmp, timeoutFuture)).map {
          case empId: Int => Home.flashing("success" -> "Employee %s has been updated".format(employee.name))
          case t: String =>
            Logger.error("Problem found in employee update process")
            InternalServerError(t)
        }
      })
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
  def save = Action.async { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => Promise.timeout(BadRequest(html.createForm(formWithErrors)), 10.seconds),
      employee => {
        val futureUpdateEmp: Future[Option[Long]] = scala.concurrent.Future { Employee.insert(employee) }
        val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 10.second)
        Future.firstCompletedOf(Seq(futureUpdateEmp, timeoutFuture)).map {
          case empId: Option[Long] => empId match {
            case Some(id) =>
              val msg = "Employee %s has been created".format(employee.name)
              Logger.info(msg)
              Home.flashing("success" -> msg)
            case None =>
              val msg = "Employee %s has not created".format(employee.name)
              Logger.info(msg)
              Home.flashing("success" -> msg)
          }
          case t: String =>
            Logger.error("Problem found in employee update process")
            InternalServerError(t)
        }
      })
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) = Action.async {
    val futureInt = scala.concurrent.Future { Employee.delete(id) }
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 1.second)
    Future.firstCompletedOf(Seq(futureInt, timeoutFuture)).map {
      case i: Int => Home.flashing("success" -> "Computer has been deleted")
      case t: String => InternalServerError(t)
    }
  }

}