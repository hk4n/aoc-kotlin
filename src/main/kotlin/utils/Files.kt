package utils

import java.io.File
import java.nio.file.Paths

class Files {
    companion object {
        fun read(day: String, filename: String, year: String = "2022"): List<String> {
           val pwd = Paths.get("").toAbsolutePath().toString()
            val path: String = "$pwd/src/main/kotlin/$year/$day/$filename"

            return File(path).useLines { it.toList() }
        }
    }
}