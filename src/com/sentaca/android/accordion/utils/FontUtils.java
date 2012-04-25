/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
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
      if (tagString.contains(TAG_BOLD)) {
        c.setTypeface(bold);
        return;
      }
      if (tagString.contains(TAG_CONDENSED)) {
        c.setTypeface(condensed);
        return;
      }
      if (tagString.contains(TAG_LIGHT)) {
        c.setTypeface(light);
        return;
      }
    }
    c.setTypeface(normal);
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
      normal = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Regular.ttf");
      bold = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Bold.ttf");
      condensed = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Condensed.ttf");
      light = Typeface.createFromAsset(assetsManager, "fonts/roboto/Roboto-Light.ttf");
    }
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
