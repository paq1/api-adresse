import play.api.{ApplicationLoader, Environment, Play}

// MKDMKD fixme trouver le moyen de lancer l'application via cette input pour faire du debug
object Launcher {
  def main(args: Array[String]): Unit = {
    val env = Environment.simple()
    val context = ApplicationLoader.Context.create(env)
    val loader = new MyLoader
    val app = loader.load(context)

    Play.start(app)
  }
}
