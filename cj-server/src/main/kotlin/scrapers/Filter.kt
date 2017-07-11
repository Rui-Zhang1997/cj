package scrapers
class StringStack(strings: MutableList<String>) {
    var strings: MutableList<String>
    init {
        this.strings = strings
    }
    fun pop(): String? {
        if (strings.size == 0) {
            return null
        }
        val ret = strings.first()
        strings.removeAt(0)
        return ret
    }

    fun peek(): String? {
        if (strings.size == 0) { return null }
        return strings.first()
    }

    fun push(s: String) {
        strings.add(0, s)
    }

    fun getStringList(): List<String> {
        return strings
    }
}

fun removeNoneAlphanumerics(strings: List<String>): List<String> {
    val stopwords: Set<String> = getStopwords()
    val filtered: MutableList<String> = mutableListOf<String>()
    val ss: StringStack = StringStack(strings.toMutableList())
    while (ss.peek() != null) {
        var s = ss.pop()
        if (s != null) {
            var slist = s.replace("-_", " ").split(" ")
            for (word in slist) {
                val w = word.replace(Regex("[^a-zA-Z0-9]"), "")
                if (!w.trim().equals(""))
                    filtered.add(w.trim().toLowerCase())
            }
        }
    }
    return filtered
}

fun removeStopwords(strings: List<String>): List<String> {
    val stopwords: Set<String> = getStopwords()
    return strings.filter { word -> !stopwords.contains(word) }
}

fun fullfilter(strings: List<String>): List<String> {
    return removeStopwords(removeNoneAlphanumerics(strings))
}
