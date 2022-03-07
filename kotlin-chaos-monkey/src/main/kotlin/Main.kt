import mono.MonoDaemon
import java.util.*
import kotlin.system.exitProcess

fun daemonAppDemo(): Int {
    val commands: Queue<String> = LinkedList()
    val daemon = MonoDaemon(commands)
    daemon.run()

    //Add commands in rate limit overed time
    commands.add("1")
    commands.add("2")
    commands.add("3")
    commands.add("4")
    commands.add("5")

    //Add next command which followed rate limit constraint
    Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * 1000)
    commands.add("6")

    //Manual stop daemon after hard code delay time
    Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * 4 * 1000)
    daemon.stop()
    return MonoDaemon.EXIT_OK
}

fun main() {
    exitProcess(daemonAppDemo())
}
