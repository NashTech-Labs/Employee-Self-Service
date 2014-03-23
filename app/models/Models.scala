package models

import java.util.{ Date }
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.language.postfixOps
import play.api.Logger

case class Employee(id: Pk[Long] = NotAssigned, name: String, address: String, dob: Date, joiningDate: Date, designation: String)

object Employee {

  // -- Parsers

  /**
   * Parse a Employee from a ResultSet
   */
  val employee = {
    get[Pk[Long]]("employee.id") ~
      get[String]("employee.name") ~
      get[String]("employee.address") ~
      get[Date]("employee.dob") ~
      get[Date]("employee.joining_date") ~
      get[String]("employee.designation") map {
        case id ~ name ~ address ~ dob ~ joiningDate ~ designation => Employee(id, name, address, dob, joiningDate, designation)
      }
  }

  // -- Queries

  /**
   * Retrieve a employee from the id.
   */
  def findById(id: Long): Option[Employee] = {
    DB.withConnection { implicit connection =>
      SQL("select * from employee where id = {id}").on('id -> id).as(employee.singleOpt)
    }
  }

  /**
   * Retrieve all employee.
   *
   * @return
   */
  def list(): Either[String, List[Employee]] = {
    DB.withConnection { implicit connection =>
      try {
        Right(SQL("select * from employee order by name").as(employee *))
      } catch {
        case ex: Exception => Logger.info("ERROR", ex); Left(ex.getMessage())
      }
    }
  }

  /**
   * Update a employee.
   *
   * @param id The employee id
   * @param employee The employee values.
   */
  def update(id: Long, employee: Employee): Int = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update employee
          set name = {name}, address = {address}, dob = {dob}, joining_date = {joiningDate}, designation = {designation}
          where id = {id}
        """).on(
          'id -> id,
          'name -> employee.name,
          'address -> employee.address,
          'dob -> employee.dob,
          'joiningDate -> employee.joiningDate,
          'designation -> employee.designation).executeUpdate()
    }
  }

  /**
   * Insert a new employee.
   *
   * @param employee The employee values.
   */
  def insert(employee: Employee): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into employee values (
    		{id}, {name}, {address}, {dob}, {joiningDate}, {designation}
          )
        """).on(
          'id -> employee.id,
          'name -> employee.name,
          'address -> employee.address,
          'dob -> employee.dob,
          'joiningDate -> employee.joiningDate,
          'designation -> employee.designation).executeInsert()
    }
  }

  /**
   * Delete a employee.
   *
   * @param id Id of the employee to delete.
   */
  def delete(id: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("delete from employee where id = {id}").on('id -> id).executeUpdate()
    }
  }

}
