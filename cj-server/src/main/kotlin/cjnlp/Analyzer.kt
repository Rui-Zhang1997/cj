package cjnlp

import cjcollections.cjmodels.Tag
import cjcollections.cjmodels.WordCorrToTag
import cjcollections.createModifiedLogistic
import cjutils.dbutils.getKeywordListForTags
import cjutils.dbutils.getRelatedTags
import cjutils.dbutils.getSampleKeywordsListForTag
import scrapers.fullfilter

/********************************************************************************************************************
 *  wELcOMe tO the MosT cRUDe TeXT CLasSIFIER (MCTC) EVer WrittEn!!!!! I HavE NO CluE
 *  WhAT thE FUCK I AM DOING BUT HERE WE gO!!!
 *
 *  The parameters are as follows:
 *
 *  @param strings          This is the text that is going to be put through the MCTC
 *  @param assignedTags     These are tags that were predefined by the user
 *
 *  The magic is in subjectifyMe. The zeroth step is to take all the words given in
 *  strings and put them in a map, with the key being the word and the value being the
 *  frequency of the word in the text (hitherto known as 0KV).
 *  Then, the first step is to get potential tags. The first
 *  part gets possible tags. If assignedTags is length 0, get all available tags stored
 *  in the database. If it is not length 0, get all tags in database directly related to
 *  it. Then, candidate keywords for those tags are pulled from the database
 *  (i.e. the top 10 keywords for each tag as well as 5 random words) and are presented
 *  to the determineCandidateTags function, which returns a list of tags that have
 *  been determined possibly pertain to the text (hitherto known as 1T). These are sent to getRelevanceData,
 *  which takes 0KV and 1T as parameters, and determines with what frequency each keyword
 *  in 1T appears in 0KV. Then the relationship is scored, based on the equations
 *  wordsModifier (scores the number of unique keywords that appear in the text)
 *  and wordCountModifier (scores the number of occurrences of each keyword).
 *  This result is finally passed to processRelevanceScores to determine what text meets
 *  the threshhold.
 *
 *  The other function, processOrphanedText, is used to figure out what to do with text
 *  that are untaggable (i.e. there are no tags that pertain to it in the db and there
 *  are no user-supplied tags).
 *
 *  The other function, processPotentialTags, is used to handle tags that failed to make
 *  the cutoff put were "close enough" to the mark that they should not be removed from
 *  consideration.
 *******************************************************************************************************************/

class RawTextProcessor(strings: List<String>, assignedTags: List<String> = arrayListOf()) {
    var strings: List<String>
    var assignedTags: List<String>
    val wordsModifier: (Double) -> Double = createModifiedLogistic(coeff = 8.0, div = 5.0, exp = 2, mod = 8.0)
    val wordCountModifier: (Double) -> Double = createModifiedLogistic(coeff = 0.5, div = 3.0, mod = 0.5)
    init {
        this.strings = strings
        this.assignedTags = assignedTags
    }

    fun determineCandidateTags(tags: List<Tag>): List<String> { //
        return mutableListOf<String>()
    }

    // eqns. in cjcollections.Algos.kt
    fun getScoreForTag(wc: MutableMap<String, Int>, tag: Tag): Double {
        var totalSum: Double = 0.0
        for (key in wc.keys) {
            totalSum += (wc.get(key)!!.toDouble() * tag.words.get(key)!!.correlation)
        }
        return wordsModifier(wc.size.toDouble()) + wordCountModifier(totalSum)
    }

    fun getRelevanceData(textwords: Map<String, Int>, tags: List<Tag>): Map<String, Double> {
        val relevance: MutableMap<String, Double> = mutableMapOf<String, Double>()
        // iterate over the words in all lists and if the word in the text has a match in the list,
        // add it to list to be calculated
        for (tag in tags) {
            val keywordCount: MutableMap<String, Int> = mutableMapOf<String, Int>()
            val words: Tag = tag
            for (w in textwords.keys) {
                if (words.contains(w)) {
                    keywordCount.put(w, textwords.get(w)!!)
                }
            }
            relevance.put(tag.tag, getScoreForTag(keywordCount, tag))
        }
        return relevance
    }

    fun subjectifyMe(): List<String> {
        val wordcount: Map<String, Int> = generateBOWForText(fullfilter(this.strings))
        val unknownTags: MutableList<UnknownTag> = mutableListOf()
        // get all tags that are possibly related
        // if reassigned tags are provided, use those and see all tags directly related to them. if not, get all other tags
        val relatedTags: List<String> = if (this.assignedTags.isEmpty()) getRelatedTags()
                                        else
                                        (this.assignedTags + this.assignedTags.flatMap { tag -> getRelatedTags(tag, 1) })
        val candidateKeywords: MutableList<Tag> = mutableListOf<Tag>()
        // for each tag, gather all the most likely keywords (top 10 for now)
        if (relatedTags.isNotEmpty()) {
            for (tag in relatedTags) {
                val sampleKeywords = getSampleKeywordsListForTag(tag)
                val keywordMap: MutableMap<String, WordCorrToTag> = mutableMapOf()
                for (keyword in sampleKeywords) {
                    keywordMap.put(keyword, WordCorrToTag(keyword, 1.0))
                }
                if (keywordMap.isEmpty()) {
                    unknownTags.add(UnknownTag(tag, this.strings))
                }
                candidateKeywords.add(Tag(tag, keywordMap))
            }
            // determines which tags are the most likely to be associated with this block of text
            val chosenTags: List<String> = determineCandidateTags(candidateKeywords)
            // calculate how relevant the tag is to the text
            val relevanceScores: Map<String, Double> = getRelevanceData(wordcount, getKeywordListForTags(chosenTags))
            val associatableTags: List<String> = processRelevanceScores(relevanceScores)
            if (associatableTags.isEmpty()) {
                processOrphanedText(this.strings)
            } else {
                processAllUnknownTags(unknownTags)
                return associatableTags
            }
        } else { // text was not tagged, and it does not have any associated tags stored in the database
            processOrphanedText(this.strings)
        }
        return arrayListOf()
    }
}

val THRESHOLD: Double = 0.6
val POTENTIAL: Double = 0.4

// the text is processed
fun processRelevanceScores(scores: Map<String, Double>): List<String> {
    val associatableTags: List<String> = scores.keys.filter { scores.get(it) ?: 0.0 >= THRESHOLD }
    val potentialTags: List<String> = scores.keys.filter { scores.get(it) ?: 0.0 >= POTENTIAL && scores.get(it) ?: 0.0 < THRESHOLD }
    processPotentialTags(potentialTags)
    return associatableTags
}

// the text is unclassifiable with the currently supplied tags
fun processOrphanedText(text: List<String>) {}

// handles tags that did not make the cut but may possibly still be correlate to the section of text
fun processPotentialTags(tag: List<String>) {
    // write them to a db cache
}
