package com.singulariti.deepshare.listeners;

import java.util.HashMap;

public interface TaggedValueListener extends OnFailListener {
    public void onTaggedValueReturned(HashMap<String, Integer> tagToValue);
}
