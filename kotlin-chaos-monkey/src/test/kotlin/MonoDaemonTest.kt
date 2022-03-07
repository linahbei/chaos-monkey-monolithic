import mono.CommandData
import mono.MonoDaemon
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class MonoDaemonTest {
    private val dummyCommandCost = 4
    private val commandsIn: Queue<CommandData> = LinkedList()
    private val commandsOut: Queue<CommandData> = LinkedList()
    private val monoDaemon: MonoDaemon = MonoDaemon(commandsIn, commandsOut)

    @Test
    fun test_only_one_command_then_status_executed() {
        monoDaemon.run()
        monoDaemon.add("1")
        Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * dummyCommandCost * 1000)
        monoDaemon.stop()
        assertEquals(commandsOut.size, 1)
        assertEquals(commandsOut.poll().status, MonoDaemon.STATUS_EXECUTED)
    }

    @Test
    fun test_over_rate_limit_command_status_not_keep_added() {
        monoDaemon.run()
        monoDaemon.add("1")
        monoDaemon.add("2")
        Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * dummyCommandCost * 1000)
        monoDaemon.stop()
        assertEquals(commandsOut.size, 2)
        assertEquals(commandsOut.poll().status, MonoDaemon.STATUS_EXECUTED)
        assertNotEquals(commandsOut.poll().status, MonoDaemon.STATUS_ADDED)
    }

    @Test
    fun test_in_rate_limit_commands_status_executed() {
        monoDaemon.run()
        monoDaemon.add("1")
        Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * 1000)
        monoDaemon.add("2")
        Thread.sleep(MonoDaemon.RATE_LIMIT.toLong() * dummyCommandCost * 1000)
        monoDaemon.stop()
        assertEquals(commandsOut.size, 2)
        assertEquals(commandsOut.poll().status, MonoDaemon.STATUS_EXECUTED)
        assertEquals(commandsOut.poll().status, MonoDaemon.STATUS_EXECUTED)
    }
}