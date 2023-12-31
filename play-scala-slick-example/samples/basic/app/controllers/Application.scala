package controllers

import dao.CatDAO
import dao.DogDAO

import javax.inject.Inject
import models.Cat
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import models.Dog
import play.api.data.validation.Constraints.{max, min}

import scala.concurrent.ExecutionContext

class Application @Inject() (
    catDao: CatDAO,
    dogDao: DogDAO,
    mcc: MessagesControllerComponents
)(implicit executionContext: ExecutionContext) extends MessagesAbstractController(mcc) {

  val catForm: Form[Cat] = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply)
  )

  val dogForm: Form[Dog] = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Dog.apply)(Dog.unapply)
  )

  def index = Action.async { implicit request =>
    catDao.all().zip(dogDao.all()).map {
      case (cats, dogs) => Ok(views.html.index(catForm, dogForm, cats, dogs))
    }
  }

  def insertCat = Action.async { implicit request =>
    val cat: Cat = catForm.bindFromRequest().get
    catDao.insert(cat).map(_ => Redirect(routes.Application.index))
  }

  def insertDog = Action.async { implicit request =>
    val dog: Dog = dogForm.bindFromRequest().get
    dogDao.insert(dog).map(_ => Redirect(routes.Application.index))
  }
}
