

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/2.5.x/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
    with ScalaModule {

  override def configure() = {
    //bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
  }
}
