import cjcollections.SortedLinkedList
import cjnlp.generateBOWForText
import scrapers.WikiPage

val page1 = "Latent_semantic_analysis"
val page2 = "Latent_Dirichlet_allocation"
val page3 = "Pachinko_allocation"
fun main(args: Array<String>) {
    val p1 = WikiPage(page1)
    val p2 = WikiPage(page2)
    val p3 = WikiPage(page3)
    val kc1 = generateBOWForText(p1.extractPlainText())
    val kc2 = generateBOWForText(p2.extractPlainText())
    val kc3 = generateBOWForText(p3.extractPlainText())
    val keysetIntersection = kc1.keys.intersect(kc2.keys).intersect(kc3.keys)
    for (key in keysetIntersection) {
        println(key)
    }
}
