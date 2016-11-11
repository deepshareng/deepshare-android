package com.singulariti.deepshare.listeners;

public interface NewUsageFromMeListener extends OnFailListener {
    public void onNewUsageFromMe(int newInstall, int newOpen);
}
