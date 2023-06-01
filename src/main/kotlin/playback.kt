import org.openrndr.KEY_ESCAPE
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import org.openrndr.extra.minim.minim
import org.openrndr.extra.osc.OSC
import org.openrndr.math.Matrix55
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) = application {
    configure {
        width = 2400
        height = 400
    }
    program {
        val osc = OSC(portIn = 50000, portOut = 57575)

        val id = if (args.size == 1) args[0] else "01"

        val fileMP3 = File("data/audio/$id.mp3")
        val fileOSC = File("data/audio/$id.osc")
        val filePNG = loadImage("data/audio/$id.png")
        val filePNG2 = loadImage("data/audio/${id}_2.png")

        listOf(fileMP3, fileOSC).forEach { f ->
            if (!f.isFile) {
                println("File ${f.path} not found.")
                exitProcess(0)
            }
        }

        val minim = minim().also {
            if (it.lineOut == null) {
                println("Can't create audio output.")
                exitProcess(0)
            }
        }

        val player = minim.loadFile(fileMP3.absolutePath)
        val songLength = player.metaData.length().toDouble()

        val rows = fileOSC.readLines().map { line ->
            val parts = line.split(", ", ignoreCase = false, limit = 3)
            Row(parts[1].toDouble(), parts[0], parts[2])
        }

        var rowId = 0
        var rowCurrent = rows[rowId]
        var rowClick = rows[rowId]
        var clickSeconds = seconds

        extend {
            drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.shade(0.5))
            drawer.image(filePNG)

            drawer.drawStyle.colorMatrix = Matrix55.IDENTITY
            drawer.image(filePNG2)
            if (player.isPlaying) {
                val t = player.position() / songLength
                drawer.stroke = ColorRGBa.PINK
                drawer.lineSegment(width * t, 0.0, width * t, height * 1.0)

                val offsetSeconds = seconds - clickSeconds
                val advanceUpToSeconds = rowClick.seconds + offsetSeconds
                while (rowCurrent.seconds < advanceUpToSeconds) {
                    rowId++
                    rowCurrent = rows[rowId]
                    val addr = rowCurrent.address
                    when (addr[1]) {
                        // volumes
                        'v' -> osc.send(addr, *rowCurrent.arguments.split(",").map {
                            it.toFloat()
                        }.toTypedArray())

                        // pressure
                        'p' -> osc.send(addr, *rowCurrent.arguments.split(", ").map {
                            it.toInt()
                        }.toTypedArray())

                        // scene_launch
                        's' -> osc.send(addr, 1)

                        // midinote
                        'm' -> osc.send(addr, *rowCurrent.arguments.split(", ").map {
                            it.toInt()
                        }.toTypedArray())

                    }
                }
            }
        }
        mouse.buttonUp.listen {
            val s = (songLength * it.position.x / width) * 0.001

            clickSeconds = seconds

            rowId = rows.binarySearch { row ->
                (row.seconds - s).toInt()
            }

            rowClick = rows[rowId]
            rowCurrent = rows[rowId]

            player.play((rowClick.seconds * 1000).toInt())
        }

        keyboard.keyDown.listen {
            when (it.key) {
                KEY_ESCAPE -> application.exit()
                KEY_SPACEBAR -> if(player.isPlaying) player.pause() else player.play()
            }
        }
    }
}
