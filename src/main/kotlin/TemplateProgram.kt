import org.openrndr.KEY_ESCAPE
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.draw.loadFont
import org.openrndr.extra.osc.OSC
import java.io.BufferedWriter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

fun main() = application {
    configure {
        width = 800
        height = 200
    }

    program {
        val font = loadFont("data/fonts/default.otf", 20.0)
        val lastOscMsg = AtomicReference("")
        val counter = AtomicInteger(0)
        val dirty = AtomicBoolean(false)
        var msg = "Click to create a new .txt file"
        var file: BufferedWriter? = null
        var startTime = 0.0

        val osc = OSC(portIn = 9000)
        osc.listen("/*/*") { addr, it ->
            counter.incrementAndGet()
            dirty.set(true)
            lastOscMsg.set(
                listOf(
                    addr,
                    (seconds - startTime).toString(),
                    it.joinToString()
                ).joinToString()
            )
        }

        extend {
            val line = lastOscMsg.get()
            drawer.clear(ColorRGBa.PINK)
            drawer.fontMap = font
            drawer.fill = ColorRGBa.BLACK
            drawer.text(msg, 20.0, 25.0)
            drawer.text(line, 20.0, 50.0)
            drawer.text("Count: " + counter.get(), 20.0, 75.0)
            if(dirty.getAndSet(false)) {
                file?.appendLine(line)
            }
        }
        mouse.buttonDown.listen {
            if (file == null) {
                saveFileDialog(supportedExtensions = listOf("Text" to listOf("txt"))) {
                    msg = "Saving..."
                    file = it.bufferedWriter()
                    startTime = seconds
                }
            }
        }
        keyboard.keyDown.listen {
            when (it.key) {
                KEY_ESCAPE -> {
                    application.exit()
                    file?.flush()
                    file?.close()
                }
                else -> osc.send("/mouse/foo", "1")
            }
        }
    }
}
