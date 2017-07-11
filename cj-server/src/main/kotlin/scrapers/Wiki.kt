package scrapers

import com.github.markhu.khttp.get
import khttp.responses.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

val wikiRoot: String = "https://en.wikipedia.org/wiki"
class WikiPage(article: String) {
    var page: Document

    init {
        page = Jsoup.connect("${wikiRoot}/${article}").get()
    }

    fun extractPlainText(): List<String> {
        val paragraphs: MutableList<String> = mutableListOf<String>()
        val paragraphElements: Elements = this.page.select("div.mw-parser-output p")
        for (p in paragraphElements) {
            val children: Elements = p.select("p")
            var pText: String = ""
            for (child in children) {
                for (c in child.childNodes()) {
                    when (c.nodeName()) {
                        "#text" -> pText += c.toString()
                        "a", "b" -> pText += c.childNode(0).toString()
                    }
                }
            }
            paragraphs.add(pText)
        }
        return paragraphs
    }
}
