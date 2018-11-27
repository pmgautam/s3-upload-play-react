package modules

import play.api.inject.Binding
import play.api.{Configuration, Environment}
import s3.{S3PropsBase, S3PropsImpl}

class S3Module extends play.api.inject.Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(bind[S3PropsBase].to[S3PropsImpl])
}
