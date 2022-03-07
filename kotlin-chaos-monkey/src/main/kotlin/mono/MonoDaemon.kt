package mono

import java.lang.System.*
import java.util.*
import javax.naming.TimeLimitExceededException

class MonoDaemon(commands: Queue<String>) {
    companion object {
        const val EXIT_OK = 0
        const val RATE_LIMIT = 3
    }
    private var runner: Thread? = null
    private var commands: Queue<String>? = null
    private var latestExecutedTimestamp: Long

    init {
        this.commands = commands
        latestExecutedTimestamp = currentTimeMillis()
    }

    private fun assertRateLimit() {
        val now: Long = currentTimeMillis()
        if(RATE_LIMIT > now - latestExecutedTimestamp) {
            throw TimeLimitExceededException()
        }
        latestExecutedTimestamp = now
    }

    private fun producer(command: String) {
        try {
            assertRateLimit()
            println("Execute: %s".format(command))
        } catch (timeLimitExceededException: TimeLimitExceededException) {
            println("Drop: %s".format(command))
        }
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
                        if( commands!!.isEmpty())
                            continue
                        producer(commands!!.poll())
                    } catch (exception: Exception) {
                        print(exception.message)
                    }
                }
            }
            runner!!.start()
        }
    }
}
