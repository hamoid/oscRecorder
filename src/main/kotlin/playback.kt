import org.openrndr.KEY_ENTER
import org.openrndr.KEY_ESCAPE
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.osc.OSC
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) = application {
    program {
        if(args.size != 1) {
            println("Usage: ./gradlew run -Popenrndr.application=PlaybackKt --args='osc-file-???.txt'")
            exitProcess(0)
        }
        val file = File(args[0])

        if(!file.isFile) {
            println("File ${args[0]} not found")
            exitProcess(0)
        }

        var counter = 0
        val reader = file.bufferedReader()

//        val lastMessages = mutableMapOf<String, String>()
//        val writer = file.bufferedWriter()
//        var startTime = -1.0

//        val osc = OSC(portIn = 57575)
//        osc.listen("/*") { addr, args ->
//            if (addr == "/start") startTime = seconds
//
//            if (startTime >= 0) {
//                val t = String.format("%.3f", seconds - startTime)
//                val line = listOf(addr, t, args.joinToString()).joinToString()
//                synchronized(this) {
//                    counter++
//                    lastMessages[addr] = line
//                }
//            }
//        }

        extend {
//            drawer.clear(ColorRGBa.PINK)
//            drawer.fill = ColorRGBa.BLACK
//            var c: Int
//            synchronized(this) {
//                c = counter
//                lastMessages.forEach { (_, line) -> writer.appendLine(line) }
//                lastMessages.clear()
//            }
//            if(startTime >= 0) {
//                drawer.text("> Recording OSC (press ESC to finish)", 20.0, 50.0)
//                drawer.text("Message count: $c", 20.0, 75.0)
//                drawer.text("Time: ${seconds - startTime}", 20.0, 100.0)
//            } else {
//                drawer.text("Waiting for /start message", 20.0, 75.0)
//            }
        }
//        mouse.moved.listen {
//            osc.send("/mouse", it.position.x.toFloat(), it.position.y.toFloat())
//        }
//        keyboard.keyDown.listen {
//            when (it.key) {
//                KEY_ESCAPE -> {
//                    writer.flush()
//                    writer.close()
//                    println("Done writing to ${file.absolutePath}")
//                    application.exit()
//                }
//
//                KEY_ENTER -> osc.send("/start", 1)
//
//                else -> osc.send("/key", it.key)
//            }
//        }
    }
}
