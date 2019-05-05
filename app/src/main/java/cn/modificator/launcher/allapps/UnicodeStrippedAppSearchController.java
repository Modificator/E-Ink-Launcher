package cn.modificator.launcher.allapps;

/**
 * The unicode stripped search controller.
 */
public class UnicodeStrippedAppSearchController extends AllAppsSearchBarController {

    public DefaultAppSearchAlgorithm onInitializeSearch() {
        return new UnicodeStrippedAppSearchAlgorithm(mApps.getUnfilteredApps());
    }
}
