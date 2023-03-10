package mcprepack

import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object WorkContext {
    val workDir = Paths.get("work-mcprepack").toAbsolutePath().normalize()
    val cacheDir = Paths.get("cache-mcprepack").toAbsolutePath().normalize()

    fun setupWorkSpace() {
        println("# Setting up $workDir")
        workDir.toFile().deleteRecursively()
        Files.createDirectories(workDir)
    }

    fun file(name: String, ext: String? = null): Path {
        return Files.createTempFile(workDir, "$name-", if(ext != null) ".$ext" else "")
    }

    fun dir(name: String): Path {
        return Files.createTempFile(workDir, "$name-", "")
    }

    val mavens = listOf(
        "https://maven.minecraftforge.net/"
    )

    fun httpGet(url: String, into: Path): Boolean {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connect()
        val os = conn.inputStream
        if (conn.responseCode != 200) {
            conn.disconnect()
            return false
        }
        Files.createDirectories(into.parent)
        os.use { input ->
            Files.newOutputStream(into).use { output ->
                input.copyTo(output)
                return true
            }
        }
    }

    fun getArtifact(
        module: String,
        artifact: String,
        version: String,
        classifier: String = "",
        extension: String = "jar"
    ): Path? {
        val moduleDir = module.replace(".", "/")
        val extClassifier = if (classifier.isEmpty()) "" else "-$classifier"
        val path = "$moduleDir/$artifact/$version/$artifact-$version$extClassifier.$extension"
        val localSave = cacheDir.resolve(path)
        if (Files.exists(localSave)) return localSave
        for (maven in mavens) {
            if (httpGet("$maven/$path", localSave))
                return localSave
        }
        return null
    }

}

class Potential<T>(compute: () -> T) : ReadOnlyProperty<Any?, T> {
    private val t by lazy { compute() }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = t
    fun get() = t

    fun executeNow() {
        get()
    }
}

@OptIn(ExperimentalTime::class)
fun <T> lifecycle(name: String, block: () -> T): Potential<T> = Potential {
    var x: T
    println("> $name")
    val time = measureTime {
        x = block()
    }
    println("> $name done. Took $time")
    return@Potential x
}

@OptIn(ExperimentalTime::class)

fun <T : Any> lifecycleNonNull(name: String, block: () -> T?): Potential<T> = Potential {
    var x: T?
    println("> $name")
    val time = measureTime {
        x = block()
    }
    if (x == null) {
        println("! $name failed. Took $time")
        exitProcess(1)
    }
    println("> $name done. Took $time")
    return@Potential x as T
}
