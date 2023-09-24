package info.nightscout.plugins.aps.openAPSSMBDynamicISF

import android.content.Context
import app.aaps.annotations.OpenForTesting
import app.aaps.interfaces.aps.DetermineBasalAdapter
import app.aaps.interfaces.bgQualityCheck.BgQualityCheck
import app.aaps.interfaces.constraints.Constraint
import app.aaps.interfaces.constraints.ConstraintsChecker
import app.aaps.interfaces.iob.GlucoseStatusProvider
import app.aaps.interfaces.iob.IobCobCalculator
import app.aaps.interfaces.logging.AAPSLogger
import app.aaps.interfaces.plugin.ActivePlugin
import app.aaps.interfaces.profile.ProfileFunction
import app.aaps.interfaces.profiling.Profiler
import app.aaps.interfaces.resources.ResourceHelper
import app.aaps.interfaces.rx.bus.RxBus
import app.aaps.interfaces.sharedPreferences.SP
import app.aaps.interfaces.stats.TddCalculator
import app.aaps.interfaces.utils.DateUtil
import app.aaps.interfaces.utils.HardLimits
import dagger.android.HasAndroidInjector
import info.nightscout.database.impl.AppRepository
import info.nightscout.plugins.aps.R
import info.nightscout.plugins.aps.openAPSSMB.DetermineBasalAdapterSMBJS
import info.nightscout.plugins.aps.openAPSSMB.OpenAPSSMBPlugin
import info.nightscout.plugins.aps.utils.ScriptReader
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class OpenAPSSMBDynamicISFPlugin @Inject constructor(
    injector: HasAndroidInjector,
    aapsLogger: AAPSLogger,
    rxBus: RxBus,
    constraintChecker: ConstraintsChecker,
    rh: ResourceHelper,
    profileFunction: ProfileFunction,
    context: Context,
    activePlugin: ActivePlugin,
    iobCobCalculator: IobCobCalculator,
    hardLimits: HardLimits,
    profiler: Profiler,
    sp: SP,
    dateUtil: DateUtil,
    repository: AppRepository,
    glucoseStatusProvider: GlucoseStatusProvider,
    bgQualityCheck: BgQualityCheck,
    tddCalculator: TddCalculator
) : OpenAPSSMBPlugin(
    injector,
    aapsLogger,
    rxBus,
    constraintChecker,
    rh,
    profileFunction,
    context,
    activePlugin,
    iobCobCalculator,
    hardLimits,
    profiler,
    sp,
    dateUtil,
    repository,
    glucoseStatusProvider,
    bgQualityCheck,
    tddCalculator
) {

    init {
        pluginDescription
            .pluginName(R.string.openaps_smb_dynamic_isf)
            .description(R.string.description_smb_dynamic_isf)
            .shortName(R.string.dynisf_shortname)
            .preferencesId(R.xml.pref_openapssmbdynamicisf)
            .setDefault(false)
    }

    // If there is no TDD data fallback to SMB as ISF calculation may be really off
    override fun provideDetermineBasalAdapter(): DetermineBasalAdapter =
        if (tdd1D == null || tdd7D == null || tddLast4H == null || tddLast8to4H == null || tddLast24H == null || !dynIsfEnabled.value())
            DetermineBasalAdapterSMBJS(ScriptReader(context), injector)
        else DetermineBasalAdapterSMBDynamicISFJS(ScriptReader(context), injector)

    override fun isAutosensModeEnabled(value: Constraint<Boolean>): Constraint<Boolean> {
        value.set(false, rh.gs(R.string.autosens_disabled_in_dyn_isf), this)
        return value
    }
}