package mcprepack

data class CSVFile(val headers: List<String>, val entries: List<List<String>>) {
    val map = entries.map { headers.zip(it).toMap() }
    val indexedBySearge = map.associateBy { it["searge"] }
    val indexedByParamName = map.groupBy {
        val match = it["param"]?.let { paramRegex.matchEntire(it) } ?: return@groupBy null
        match.groupValues[1].toInt()
    }

    companion object {
        val paramRegex = "p_([0-9]+)_([0-9]+)_.*".toRegex()
    }
}
