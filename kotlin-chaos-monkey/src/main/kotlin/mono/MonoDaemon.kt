package mono

import java.lang.System.*
import java.util.*
import javax.naming.TimeLimitExceededException

public data class CommandData(val command: String, val added: Long, var status: String?=null)

class MonoDaemon(commandsIn: Queue<CommandData>, commandsOut: Queue<CommandData>) {
    companion object {
        const val EXIT_OK = 0
        const val RATE_LIMIT = 3

        const val STATUS_DROPPED = "dropped"
        const val STATUS_EXECUTED = "executed"
        const val STATUS_ADDED = "added"
    }
    private var runner: Thread? = null
    private var commandsIn: Queue<CommandData>? = null
    private var commandsOut: Queue<CommandData>? = null
    private var latestExecutedTimestamp: Long

    init {
        this.commandsIn = commandsIn
        this.commandsOut = commandsOut
        latestExecutedTimestamp = 0
    }

    private fun assertRateLimit() {
        val now: Long = currentTimeMillis()
        if(RATE_LIMIT > now - latestExecutedTimestamp) {
            throw TimeLimitExceededException()
        }
        latestExecutedTimestamp = now
    }

    private fun producer(command: CommandData) {
        try {
            assertRateLimit()
            command.status = STATUS_EXECUTED
        } catch (timeLimitExceededException: TimeLimitExceededException) {
            command.status = STATUS_DROPPED
        } finally {
            commandsOut!!.add(command)
        }
    }

    fun add(command: String) {
        commandsIn!!.add(
            CommandData(command, currentTimeMillis(), STATUS_ADDED)
        )
    }

    fun stop() {
        if(null != runner && runner!!.isAlive) {
            runner!!.interrupt()
        }
    }

    fun forever() {
        if(null != runner && runner!!.isAlive) {
            runner!!.join()
        }
    }

    fun run() {
        if(null == runner || !runner!!.isAlive) {
            runner = Thread {
                while(true) {
                    Thread.sleep(1)
                    try {
                        if( commandsIn!!.isEmpty())
                            continue
                        producer(commandsIn!!.poll())
                    } catch (exception: Exception) {
                        print(exception.message)
                    }
                }
            }
            runner!!.start()
        }
    }
}
