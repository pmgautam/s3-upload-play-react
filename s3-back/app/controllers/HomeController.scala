package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import s3.S3PropsBase

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, s3Impl: S3PropsBase)(implicit ex: ExecutionContext) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /**
    * upload file here
    * @return
    */
  def uploadFile() = Action.async(parse.multipartFormData) {
    implicit request =>

      play.api.Logger.logger.info(s"UPLOADING IMAGE ${request.body}")

      val f = request.body.file("image")
      f match {
        case Some(file) =>
          val upload = s3Impl.uploadImage(file)
          if (upload) {
            Future.successful(Ok(Json.obj(("msg", "file has been uploaded"))))
          }
          else {
            Future.successful(InternalServerError(Json.obj(("msg", "md5 mismatch"))))
          }

        case None => Future.successful(BadRequest(Json.obj(("msg", "no file to upload"))))
      }
  }
}

