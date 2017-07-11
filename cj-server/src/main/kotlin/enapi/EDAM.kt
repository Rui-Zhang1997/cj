package enapi

import com.evernote.auth.EvernoteAuth
import com.evernote.auth.EvernoteService
import com.evernote.clients.ClientFactory
import com.evernote.clients.NoteStoreClient
import com.evernote.clients.UserStoreClient
import kotlin.system.exitProcess

class EDAMAuth(token: String){
    var evernoteAuth: EvernoteAuth
    var factory: ClientFactory
    var userStore: UserStoreClient
    var noteStore: NoteStoreClient
    init {
        evernoteAuth = EvernoteAuth(EvernoteService.SANDBOX, token)
        factory = ClientFactory(evernoteAuth)
        userStore = factory.createUserStoreClient()
        val versionOk: Boolean = userStore.checkVersion("EDAMSAMPLE", com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR)
        if (!versionOk) {
            println("Incompatible client protocol")
            exitProcess(1)
        }
        noteStore = factory.createNoteStoreClient()
    }
}

fun createEDAM(token: String): EDAMAuth {
    return EDAMAuth(token)
}
