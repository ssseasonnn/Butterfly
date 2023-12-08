package zlc.season.butterfly.launcher

import android.app.Activity
import android.os.Bundle
import zlc.season.butterfly.internal.ButterflyHelper.application
import zlc.season.butterfly.internal.key
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter

object DestinationLauncherManager {
    private const val OLD_ACTIVITY_KEY = "old_activity_key"

    private val launcherMap = mutableMapOf<String, MutableList<DestinationLauncher>>()
    private val saveInstanceStateMap = mutableMapOf<String, Boolean>()

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (savedInstanceState != null) {
                    val oldKey = savedInstanceState.getString(OLD_ACTIVITY_KEY)
                    if (!oldKey.isNullOrEmpty()) {
                        updateKey(oldKey, activity.key())
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                super.onActivitySaveInstanceState(activity, outState)
                val key = activity.key()
                if (containsKey(key)) {
                    outState.putString(OLD_ACTIVITY_KEY, key)
                }
                updateSaveInstanceState(key)
            }

            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                handleActivityDestroy(activity.key())
            }
        })
    }

    @Synchronized
    private fun updateSaveInstanceState(key: String) {
        saveInstanceStateMap[key] = true
    }

    @Synchronized
    private fun handleActivityDestroy(key: String) {
        if (saveInstanceStateMap[key] == null) {
            launcherMap.remove(key)
        }
        saveInstanceStateMap.remove(key)
    }

    @Synchronized
    private fun updateKey(oldKey: String, newKey: String) {
        val oldLauncher = launcherMap.remove(oldKey)
        oldLauncher?.let {
            launcherMap[newKey] = oldLauncher
        }
    }

    @Synchronized
    fun containsKey(key: String): Boolean {
        return launcherMap[key] != null
    }

    @Synchronized
    fun addLauncher(key: String, launcher: DestinationLauncher) {
        val list = launcherMap.getOrPut(key) { mutableListOf() }
        if (list.find { it.destinationData.route == launcher.destinationData.route } == null) {
            list.add(launcher)
        }
    }

    @Synchronized
    fun getLauncher(key: String, route: String): DestinationLauncher? {
        return launcherMap[key]?.find { it.destinationData.route == route }
    }
}