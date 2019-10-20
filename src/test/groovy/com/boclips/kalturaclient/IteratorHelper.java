package com.boclips.kalturaclient;

import com.boclips.kalturaclient.media.MediaEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorHelper {
    public static List<MediaEntry> toList(Iterator<List<MediaEntry>> iterator) {
        List<MediaEntry> allEntries = new ArrayList<>();

        while (iterator.hasNext()) {
            List<MediaEntry> page = iterator.next();
            allEntries.addAll(page);
        }

        return allEntries;
    }
}
