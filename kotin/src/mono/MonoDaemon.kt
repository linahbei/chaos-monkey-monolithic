package mono

public class MonoDaemon {
    companion object {
        const val EXIT_OK = 0
    }

    public fun run(): Int {
        println("Hello, And Goodbye!")
        return MonoDaemon.EXIT_OK
    }
}
