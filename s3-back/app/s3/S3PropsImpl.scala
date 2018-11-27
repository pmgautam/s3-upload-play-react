package s3

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.security.{DigestInputStream, MessageDigest}

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}
import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData

class S3PropsImpl @Inject()(config: Configuration) extends S3PropsBase {

  //return s3Props object
  def getImageS3Conf: S3Props = {
    val accessKeyId = config.get[String]("aws_access_key_id")
    val secretKey = config.get[String]("aws_secret_access_key")
    val folderName = config.get[String]("folder")
    val bucketName = config.get[String]("bucket")
    val endPoint = config.get[String]("endPoint")
    val region = config.get[String]("region")

    S3Props(accessKeyId, secretKey, bucketName, folderName, endPoint, region)
  }

  override def uploadImage(file: MultipartFormData.FilePart[TemporaryFile]): Boolean = {
    val s3Props = getImageS3Conf

    //copy file to disk and upload to s3
    val localFile =  file.ref.moveTo(Paths.get(s"/opt/${file.filename}"), replace = true)

    //copy local file to s3
    val doCred = new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Props.accessKeyId, s3Props.secretKey))
    val doBuckets = AmazonS3ClientBuilder.standard()
      .withCredentials(doCred)
      .withEndpointConfiguration(new EndpointConfiguration(s3Props.endPoint, s3Props.region))
      .build()

    val s3Loc = s"${s3Props.folder}/${file.filename}"

    val uploadRequest = new PutObjectRequest(s3Props.bucket, s3Loc, localFile).withCannedAcl(CannedAccessControlList.PublicRead)

    val upload = doBuckets.putObject(uploadRequest)

    upload.getETag.equalsIgnoreCase(computeHash(localFile.getPath))
  }

  def computeHash(path: String): String = {
    val buffer = new Array[Byte](8192)
    val md5 = MessageDigest.getInstance("MD5")

    val dis = new DigestInputStream(new FileInputStream(new File(path)), md5)
    try {
      while (dis.read(buffer) != -1) {}
    } finally {
      dis.close()
    }

    md5.digest.map("%02x".format(_)).mkString
  }
}
