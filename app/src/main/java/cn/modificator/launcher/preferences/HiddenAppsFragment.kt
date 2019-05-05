package cn.modificator.launcher.preferences

import android.content.Context
import android.os.Bundle
import android.os.Process
import android.view.*

import cn.modificator.launcher.LauncherAppState
import cn.modificator.launcher.MultiSelectRecyclerViewAdapter
import cn.modificator.launcher.R
import cn.modificator.launcher.compat.LauncherActivityInfoCompat
import cn.modificator.launcher.compat.LauncherAppsCompat

class HiddenAppsFragment : androidx.fragment.app.Fragment(), MultiSelectRecyclerViewAdapter.ItemClickListener {

    private lateinit var installedApps: List<LauncherActivityInfoCompat>
    private lateinit var adapter: MultiSelectRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_selectable_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        installedApps = getAppsList(context).apply { sortBy { it.label.toString() } }
        adapter = MultiSelectRecyclerViewAdapter(installedApps, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val hiddenApps = PreferenceProvider.getPreferences(context).hiddenAppsSet
        if (!hiddenApps.isEmpty()) {
            activity!!.title = hiddenApps.size.toString() + getString(R.string.hidden_app_selected)
        } else {
            activity!!.title = getString(R.string.hidden_app)
        }
    }

    override fun onItemClicked(position: Int) {
        val title = adapter.toggleSelection(position, installedApps[position].componentName.flattenToString())
        activity!!.title = title
    }

    private fun getAppsList(context: Context?) =
            LauncherAppsCompat.getInstance(context).getActivityList(null, Process.myUserHandle())

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_hide_apps, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_apply -> {
                adapter.addSelectionsToList(activity)
                LauncherAppState.getInstanceNoCreate().reloadAllApps()
                activity!!.onBackPressed()
                true
            }
            R.id.action_reset -> {
                activity!!.title = adapter.clearSelection()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}