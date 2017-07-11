package cjutils.dbutils

import cjcollections.cjmodels.Tag

fun getKeywordListForTags(keywords: List<String> = arrayListOf()): List<Tag> {
    return mutableListOf<Tag>()
}

fun getRelatedTags(tag: String = "", level: Int = 1): List<String> {
    return arrayListOf()
}

fun getSampleKeywordsListForTag(tag: String): List<String> {
    return arrayListOf()
}
