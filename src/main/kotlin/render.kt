import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.math.Vector2
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) = application {
    configure {
        width = 2400
        height = 400
    }
    program {
        val id = if (args.size == 1) args[0] else "01"

        val fileOSC = File("data/audio/$id.osc")

        listOf(fileOSC).forEach { f ->
            if (!f.isFile) {
                println("File ${f.path} not found.")
                exitProcess(0)
            }
        }

        val rows = fileOSC.readLines().map { line ->
            val parts = line.split(", ", ignoreCase = false, limit = 3)
            Row(parts[1].toDouble(), parts[0], parts[2])
        }

        val rt = renderTarget(width, height) {
            colorBuffer()
        }

        drawer.isolatedWithTarget(rt) {
            clear(ColorRGBa.TRANSPARENT)
            val volumes = mutableListOf<Vector2>()
            val pressures = mutableListOf<Vector2>()
            val midinotes = mutableMapOf<Int, Vector2>()
            val volBandHeight = height / 9.0

            rows.forEach { row ->
                val x = width * row.seconds / rows.last().seconds
                when (row.address[1]) {
                    // volumes
                    'v' -> {
                        volumes.addAll(
                            row.arguments.split(",").mapIndexed { i, v ->
                                val y = height - (i + v.toDouble() * 0.5) * volBandHeight
                                Vector2(x, y)
                            }
                        )
                    }

                    // pressure
                    'p' -> {
                        val (note, pressure) = row.arguments.split(", ").map {
                            it.toInt()
                        }
                        pressures.add(
                            Vector2(x, height - (7 + note / 127.0 + pressure / 300.0) * volBandHeight)
                        )                    }

                    // scene_launch
                    's' -> {
                        stroke = ColorRGBa.CYAN
                        lineSegment(x, 0.0, x, height * 1.0)
                    }

                    // midinote
                    'm' -> {
                        val (note, vol) = row.arguments.split(", ").map {
                            it.toInt()
                        }
                        if(vol > 0) {
                            midinotes[note] = Vector2(x, height - (7 + note / 127.0) * volBandHeight)
                        } else {
                            val from = midinotes[note]!!
                            val to = Vector2(x, height - (7 + note / 127.0) * volBandHeight)
                            midinotes.remove(note)
                            stroke = ColorRGBa.YELLOW
                            lineSegment(from, to)

                        }
                    }
                }
            }
            fill = ColorRGBa.MAGENTA
            points(volumes)
            fill = ColorRGBa.RED
            points(pressures)
        }

        rt.colorBuffer(0).saveToFile(File("data/audio/${id}_2.png"))

        extend {
            drawer.image(rt.colorBuffer(0))
        }
    }
}
