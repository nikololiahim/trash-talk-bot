package trash.core

object Settings {
  val botEnvVarName = "TRASHTALK_TOKEN"
  val botToken: Option[String] = sys.env.get(botEnvVarName)
  // This is needed for the frontend auth
  val botName:    String = "debil_inno_bot"

  val dbName:     String = sys.env.getOrElse("TRASHDB_NAME", "postgres")
  val dbURL:      String = sys.env.getOrElse("TRASHDB_URL",  "jdbc:postgresql://localhost:5432")
  val dbUsername: String = sys.env.getOrElse("TRASHDB_UNAME","postgres")
  val dbPassword: String = sys.env.getOrElse("TRASHDB_PASS", "changeme")

  val backendURL: String = sys.env.getOrElse("TRASHBACK_URL", "TBD")
}
