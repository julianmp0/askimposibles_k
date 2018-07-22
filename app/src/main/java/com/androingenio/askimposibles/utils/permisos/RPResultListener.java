package com.androingenio.askimposibles.utils.permisos;

/**
 * Created by julianmartinez on 7/11/17.
 */

public interface RPResultListener {
    void onPermissionGranted();

    void onPermissionDenied();
}
