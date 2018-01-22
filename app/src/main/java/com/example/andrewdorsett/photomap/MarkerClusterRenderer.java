package com.example.andrewdorsett.photomap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Set;

/**
 * Created by admin on 1/21/18.
 */

public class MarkerClusterRenderer extends DefaultClusterRenderer<MarkerGroup> {

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MarkerGroup> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    public void onClustersChanged(Set<? extends Cluster<MarkerGroup>> set) {
        super.onClustersChanged(set);
    }

    @Override
    public void setOnClusterClickListener(ClusterManager.OnClusterClickListener<MarkerGroup> onClusterClickListener) {
        super.setOnClusterClickListener(onClusterClickListener);
    }

    @Override
    public void setOnClusterInfoWindowClickListener(ClusterManager.OnClusterInfoWindowClickListener<MarkerGroup> onClusterInfoWindowClickListener) {
        super.setOnClusterInfoWindowClickListener(onClusterInfoWindowClickListener);
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<MarkerGroup> setOnClusterItemClickListener) {
        super.setOnClusterItemClickListener(setOnClusterItemClickListener);
    }

    @Override
    public void setOnClusterItemInfoWindowClickListener(ClusterManager.OnClusterItemInfoWindowClickListener<MarkerGroup> onClusterItemInfoWindowClickListener) {
        super.setOnClusterItemInfoWindowClickListener(onClusterItemInfoWindowClickListener);
    }

    @Override
    public void setAnimation(boolean b) {
        super.setAnimation(b);
    }

    @Override
    public void onAdd() {
        super.onAdd();
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MarkerGroup> cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > 1;
    }
}
