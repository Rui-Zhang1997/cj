package cjnlp

import scrapers.fullfilter

fun generateBagOfWords(strings: List<String>): Map<String, Int> {
    val wc: MutableMap<String, Int> = mutableMapOf<String, Int>()
    for (word in strings) {
        if (wc.containsKey(word)) {
            wc.replace(word, wc.get(word)!! + 1)
        } else {
            wc.put(word, 1)
        }
    }
    return wc.toSortedMap()
}

fun generateBOWForText(strings: List<String>): Map<String, Int> {
    return generateBagOfWords(fullfilter(strings))
}