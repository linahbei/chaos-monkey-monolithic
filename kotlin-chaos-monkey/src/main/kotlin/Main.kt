import mono.MonoDaemon
import kotlin.system.exitProcess

fun main() {
    val daemon = MonoDaemon()
    exitProcess(daemon.run())
}
