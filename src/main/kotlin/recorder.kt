import org.openrndr.KEY_ENTER
import org.openrndr.KEY_ESCAPE
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.osc.OSC
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

fun main() = application {
    program {
        val counter = AtomicInteger(0)
        val file = File("${System.currentTimeMillis()}.osc")
        val writer = file.bufferedWriter()
        var startTime = -1.0
        var doQuit = false

        val osc = OSC(portIn = 57575)
        osc.listen("/*") { addr, args ->
            if (addr == "/scene_launch" && startTime < 0) startTime = seconds

            if (startTime >= 0) {
                val t = String.format("%.3f", seconds - startTime)
                val line = listOf(addr, t, args.joinToString()).joinToString()
                counter.incrementAndGet()
                writer.appendLine(line)
            }
        }

        extend {
            drawer.clear(ColorRGBa.PINK)
            drawer.fill = ColorRGBa.BLACK
            if(doQuit) {
                synchronized(this) {
                    writer.flush()
                    writer.close()
                }
                println("Done writing to ${file.absolutePath}")
                exitProcess(0)
            }
            if (startTime >= 0) {
                drawer.text("> Recording OSC (press ESC to finish)", 20.0, 50.0)
                drawer.text("Message count: ${counter.get()}", 20.0, 75.0)
                drawer.text("Time: ${seconds - startTime}", 20.0, 100.0)
            } else {
                drawer.text("Waiting for /scene_launch message", 20.0, 75.0)
            }
        }
        mouse.moved.listen {
            osc.send("/mouse", it.position.x.toFloat(), it.position.y.toFloat())
        }
        keyboard.keyDown.listen {
            when (it.key) {
                KEY_ESCAPE -> doQuit = true

                KEY_ENTER -> osc.send("/scene_launch", 1)

                else -> osc.send("/key", it.key)
            }
        }
    }
}
