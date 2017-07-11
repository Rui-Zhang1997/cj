package cjnlp

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import scrapers.fullfilter
import java.io.File
import java.io.FileInputStream

data class Topic(val tag: List<String>, val texts: List<String>)

/********************************************************************************************************************
 * Class used to train up a set of basic tags and keywords. Currently used sources are:
 *  + Wikipedia
 *
 *  Directory structure is as follows
 *
 *  + <Directory Name>
 *      + training_files
 *          + ... <files used in training>
 *      + tags
 *          + <tag_name>_files <- files associated with this tag
 ********************************************************************************************************************/

fun <T>SimpleThread(f: (T) -> Unit): (T) -> ((T) -> Unit) {
    return { x: T -> {

    }}
}
class TrainingSet(fp: String) {

    val fp: String = fp
    val files: MutableMap<String, List<String>> = mutableMapOf()

    init {

    }
    fun run() {
        val parserThread: SimpleThread = SimpleThread { (name, data) -> files.put(name, fullfilter(arrayListOf(String(data))))}
        File("${fp}${File.pathSeparator}training_files").walkTopDown().iterator().forEach {
            val fin: FileInputStream = FileInputStream(it)
            val data: ByteArray = kotlin.ByteArray(it.length().toInt())
            fin.read(data)
            parserThread(Pair(it.nameWithoutExtension, String(data))).start()
        }
    }
}