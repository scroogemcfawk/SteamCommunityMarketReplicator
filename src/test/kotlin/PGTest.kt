import java.sql.DriverManager

fun main() {
    val jdbcURL = "jdbc:postgresql://localhost:5432/scm_test"
    val user = "postgres"
    val pass = "admin"
    val connection = DriverManager.getConnection(jdbcURL, user, pass)
    val q = connection.prepareStatement("INSERT INTO App(id, name, icon_url) VALUES ('730', 'Counter-Strike: Global Offensive', 'https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/apps/730/69f7ebe2735c366c65c0b33dae00e12dc40edbe4.jpg')")
//    val q = connection.prepareStatement("INSERT INTO App(id, name, icon) VALUES ('730', 'Counter-Strike: Global Offensive')")
    q.execute()
}