import mono.MonoDaemon
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    var daemon: MonoDaemon = MonoDaemon()
    exitProcess(daemon.run())
}
