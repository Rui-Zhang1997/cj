package scrapers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

val stopwordsUrl: String = "http://www.ranks.nl/stopwords"
val stopwordsDoc: Document = Jsoup.connect(stopwordsUrl).get()

fun getStopwords(): Set<String> {
    val stopwords: MutableSet<String> = mutableSetOf<String>()
    val panels: Elements = stopwordsDoc.select("div.panel-default")
    for (panel in panels) {
        val tableColumns: Elements = panel.select("td")
        for (col in tableColumns) {
            stopwords.addAll(col.text().split(" "))
        }
    }
    return stopwords
}