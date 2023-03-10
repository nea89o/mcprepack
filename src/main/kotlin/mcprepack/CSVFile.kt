package mcprepack

data class CSVFile(val headers: List<String>, val entries: List<List<String>>) {
    val map = entries.map { headers.zip(it).toMap() }
}
