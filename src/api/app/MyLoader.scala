import play.api.{Application, ApplicationLoader}

class MyLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application =
    new Components(context).application
}
