package s3

import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData

abstract class S3PropsBase {
  def getImageS3Conf: S3Props

  def uploadImage(file:MultipartFormData.FilePart[TemporaryFile]):Boolean
}
