package com.taxi.nanny.utils.location.permission;

import java.util.ArrayList;

public interface PermissionGranted
{
    void showPermissionAlert(ArrayList<String> permissionList, int code);
}
