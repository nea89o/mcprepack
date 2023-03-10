package mcprepack

import net.fabricmc.stitch.commands.tinyv2.TinyFile
import java.io.StringReader

sealed interface TypeDescriptor {
    fun mapClassName(tinyFile: TinyFile): TypeDescriptor {
        return when (this) {
            is Class ->
                Class(tinyFile.mapClassesByFirstNamespace()[this.name]?.classNames?.get(2) ?: this.name)

            else -> this
        }
    }

    fun toProguardString(): String {
        return when (this) {
            is Array -> this.elementType.toProguardString() + "[]"
            Boolean -> "boolean"
            Byte -> "byte"
            Char -> "char"
            is Class -> this.name.replace("/", ".")
            Double -> "double"
            Float -> "float"
            Int -> "int"
            Long -> "long"
            Short -> "short"
            Void -> "void"
        }
    }

    sealed interface FieldDescriptor : TypeDescriptor
    object Void : TypeDescriptor
    object Double : FieldDescriptor
    object Boolean : FieldDescriptor
    object Float : FieldDescriptor
    object Int : FieldDescriptor
    object Long : FieldDescriptor
    object Short : FieldDescriptor
    object Byte : FieldDescriptor
    object Char : FieldDescriptor
    data class Class(val name: String) : FieldDescriptor
    data class Array(val elementType: TypeDescriptor) : FieldDescriptor
}

data class FieldDescriptor(
    val type: TypeDescriptor.FieldDescriptor
) {
    companion object {
        fun parseFieldDescriptor(descriptor: String) =
            FieldDescriptor(readType(StringReader(descriptor)) as? TypeDescriptor.FieldDescriptor ?: error("Cannot have void as a field type"))
    }
}

data class MethodDescriptor(
    val arguments: List<TypeDescriptor.FieldDescriptor>,
    val returnType: TypeDescriptor
) {
    companion object {
        fun parseMethodDescriptor(descriptor: String): MethodDescriptor {
            val reader = StringReader(descriptor)
            require(reader.readChar() == '(')
            val arguments = mutableListOf<TypeDescriptor.FieldDescriptor>()
            while (reader.peek() != ')') {
                arguments.add(
                    readType(reader) as? TypeDescriptor.FieldDescriptor ?: error("Cannot have void type as argument")
                )
            }
            reader.readChar() // Consume the ')'
            return MethodDescriptor(arguments, readType(reader))
        }
    }
}

fun StringReader.readChar() = CharArray(1).let {
    if (read(it) < 0) null
    else it[0]
}

fun StringReader.readUntil(search: Char): String? {
    val s = StringBuilder()
    while (true) {
        val justRead = readChar() ?: return null
        if (justRead == search) return s.toString()
        s.append(justRead)
    }
}

fun StringReader.peek(): Char? {
    mark(0)
    val x = readChar()
    reset()
    return x
}


fun readType(reader: StringReader): TypeDescriptor {
    return when (reader.readChar()) {
        'L' -> TypeDescriptor.Class(reader.readUntil(';') ?: error("Unfinished class type descriptor"))
        'V' -> TypeDescriptor.Void
        'Z' -> TypeDescriptor.Boolean
        'B' -> TypeDescriptor.Byte
        'I' -> TypeDescriptor.Int
        'D' -> TypeDescriptor.Double
        'J' -> TypeDescriptor.Long
        'S' -> TypeDescriptor.Short
        'F' -> TypeDescriptor.Float
        'C' -> TypeDescriptor.Char
        '[' -> TypeDescriptor.Array(readType(reader))
        else -> error("Unknown or unfinished type descriptor")
    }
}
