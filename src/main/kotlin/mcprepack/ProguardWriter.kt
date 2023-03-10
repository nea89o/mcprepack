package mcprepack

import java.io.Writer

data class ProguardWriter(val writer: Writer) {
    fun visitClass(sourceName: String, targetName: String) {
        writer.write(sourceName)
        writer.write(" -> ")
        writer.write(targetName)
        writer.write(":\n")
    }

    fun visitField(sourceName: String, targetName: String, proguardType: String) {
        writer.write("    ")
        writer.write(proguardType)
        writer.write(" ")
        writer.write(sourceName)
        writer.write(" -> ")
        writer.write(targetName)
        writer.write("\n")
    }

    fun visitMethod(sourceName: String, targetName: String, arguments: List<String>, returnType: String) {
        writer.write("    ")
        writer.write(returnType)
        writer.write(" ")
        writer.write(sourceName)
        writer.write("(")
        writer.write(arguments.joinToString(","))
        writer.write(") -> ")
        writer.write(targetName)
        writer.write("\n")
    }

}