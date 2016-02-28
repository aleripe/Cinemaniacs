/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.utilities;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Represents a simple cache for the costly palette computation
 */
public class PaletteCache {
    private static Map<Integer, Palette.Swatch> cache = new WeakHashMap<>();

    /** Get a swatch for the specified Bitmap and caches it by Id */
    public static Palette.Swatch getPaletteSwatch(final Integer id, final Bitmap bitmap) {
        if (!cache.containsKey(id)) {
            cache.put(id, Utilities.getDominantSwatch(Palette.from(bitmap).generate()));
        }

        return cache.get(id);
    }
}