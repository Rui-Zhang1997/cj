package cjcollections.cjmodels

data class WordCorrToTag(val word: String, val correlation: Double)
class Tag(tag: String, words: Map<String, WordCorrToTag>, relatedTags: List<String> = arrayListOf()) { // contains words that cooresponds to that tag
    val tag: String
    val words: Map<String, WordCorrToTag>
    val relatedTags: List<String>
    init {
        this.tag = tag
        this.words = words
        this.relatedTags = relatedTags
    }

    fun contains(w: String): Boolean {
        for (word in this.words.keys) {
            if (word == w) {
                return true
            }
        }
        return false
    }
}
data class Word(val word: String, val tags: List<WordCorrToTag>) // contains tags that are associated with each word