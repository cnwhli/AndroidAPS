package app.aaps.configuration.setupwizard.elements

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import app.aaps.configuration.setupwizard.SWDefinition
import app.aaps.interfaces.ui.UiInteraction
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class SWPreference(injector: HasAndroidInjector, private val definition: SWDefinition) : SWItem(injector, Type.PREFERENCE) {

    @Inject lateinit var uiInteraction: UiInteraction

    private var xml: Int = -1

    fun option(xml: Int): SWPreference {
        this.xml = xml
        return this
    }

    override fun generateDialog(layout: LinearLayout) {
        addConfiguration(layout, xml)
        super.generateDialog(layout)
    }

    private fun addConfiguration(layout: LinearLayout, xml: Int) {
        (Class.forName(uiInteraction.myPreferenceFragment.name).newInstance() as Fragment).also { fragment ->
            fragment.arguments = Bundle().also { it.putInt("id", xml) }
            definition.activity.supportFragmentManager.beginTransaction().run {
                replace(layout.id, fragment)
                commit()
            }
        }
    }
}