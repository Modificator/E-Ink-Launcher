package cn.modificator.launcher;

import android.content.pm.ResolveInfo;

public class ComparableResolveInfo implements Comparable<ComparableResolveInfo> {

    private int position = 0;
    private ResolveInfo resolveInfo;

    public int getPosition () {
        return position;
    }

    public void setPosition (int position) {
        this.position = position;
    }

    public ResolveInfo getResolveInfo () {
        return resolveInfo;
    }

    public void setResolveInfo (ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }

    public ComparableResolveInfo (ResolveInfo ri,int pos) {
        this.resolveInfo = ri;
        this.position = pos;
    }

    public int compareTo (ComparableResolveInfo o) {
        return this.position - o.position;
    }



}
