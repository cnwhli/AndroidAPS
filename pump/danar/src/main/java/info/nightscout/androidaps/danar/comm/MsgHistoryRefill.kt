package info.nightscout.androidaps.danar.comm

import app.aaps.interfaces.logging.LTag
import dagger.android.HasAndroidInjector

class MsgHistoryRefill(
    injector: HasAndroidInjector
) : MsgHistoryAll(injector) {

    init {
        setCommand(0x3108)
        aapsLogger.debug(LTag.PUMPCOMM, "New message")
    }
    // Handle message taken from MsgHistoryAll
}