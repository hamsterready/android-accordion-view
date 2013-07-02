/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontUtils {
  public static final String TAG_LIGHT = "light";

  public static final String TAG_CONDENSED = "condensed";

  public static final String TAG_BOLD = "bold";

  private static final int ICE_CREAM_SANDWITCH = 14;

  private static Typeface normal;

  private static Typeface bold;

  private static Typeface condensed;

  private static Typeface light;

  private static void processsViewGroup(ViewGroup v, final int len) {

    for (int i = 0; i < len; i++) {
      final View c = v.getChildAt(i);
      if (c instanceof TextView) {
        setCustomFont((TextView) c);
      } else if (c instanceof ViewGroup) {
        setCustomFont((ViewGroup) c);
      }
    }
  }

  private static void setCustomFont(TextView c) {
    Object tag = c.getTag();
    if (tag instanceof String) {
      final String tagString = (String) tag;
      if (tagString.contains(TAG_BOLD) && bold != null) {
        c.setTypeface(bold);
        return;
      }
      if (tagString.contains(TAG_CONDENSED) && condensed != null) {
        c.setTypeface(condensed);
        return;
      }
      if (tagString.contains(TAG_LIGHT) && light != null) {
        c.setTypeface(light);
        return;
      }
    }
    if(normal != null) {
      c.setTypeface(normal);
    }
  }

  public static void setCustomFont(View topView, AssetManager assetsManager) {
    if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWITCH) {
      return;
    }
    initTypefaces(assetsManager);

    if (topView instanceof ViewGroup) {
      setCustomFont((ViewGroup) topView);
    } else if (topView instanceof TextView) {
      setCustomFont((TextView) topView);
    }
  }

  private static void initTypefaces(AssetManager assetsManager) {
    if (normal == null || bold == null || condensed == null || light == null) {
      normal = loadTypeface(assetsManager, "fonts/roboto/Roboto-Regular.ttf");
      bold = loadTypeface(assetsManager, "fonts/roboto/Roboto-Bold.ttf");
      condensed = loadTypeface(assetsManager, "fonts/roboto/Roboto-Condensed.ttf");
      light = loadTypeface(assetsManager, "fonts/roboto/Roboto-Light.ttf");
    }
  }
  
  private static Typeface loadTypeface(AssetManager assetsManager, String path) {
    try {
      return Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Regular.ttf"); 
	} catch(RuntimeException e) {
	  // May occur rarely, on a few devices
	  Log.d("SentacaAccordionView", "Unable to load Typeface from " + path, e);
	}
	return null;
  }

  private static void setCustomFont(ViewGroup v) {
    final int len = v.getChildCount();
    processsViewGroup(v, len);
  }

  public static Typeface getTypefaceNormal(AssetManager assetsManager) {
    initTypefaces(assetsManager);
    return normal;
  }
}
