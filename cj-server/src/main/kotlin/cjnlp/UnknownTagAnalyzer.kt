package cjnlp

class UnknownTag(tag: String, text: List<String>) {
    var tag: String
    var text: List<String>
    init {
        this.tag = tag
        this.text = text
    }
}

fun processAllUnknownTags(unknowns: List<UnknownTag>) {

}