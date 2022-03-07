import mono.CommandData
import mono.MonoDaemon
import java.util.*
import kotlin.system.exitProcess

fun daemonAppDemo(): Int {
    val commandsIn: Queue<CommandData> = LinkedList()
    val commandsOut: Queue<CommandData> = LinkedList()
    val daemon = MonoDaemon(commandsIn, commandsOut)
    daemon.run()

    //Add commands in rate limit overed time
    daemon.add("1")
    daemon.add("2")
    daemon.add("3")
    daemon.add("4")
    daemon.add("5")

    //Add next command which followed rate limit constraint
    Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * 1000)
    daemon.add("6")

    //Manual stop daemon after hard code delay time
    Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * 4 * 1000)
    daemon.stop()

    //Get results
    for(commandOut in commandsOut) {
        println("%s: %s (added: %s)"
            .format(commandOut.command, commandOut.status, commandOut.added))
    }
    return MonoDaemon.EXIT_OK
}

fun main() {
    exitProcess(daemonAppDemo())
}
