/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.utils;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontUtils {
  private static void processsViewGroup(ViewGroup v, Typeface tf, final int len) {

    for (int i = 0; i < len; i++) {
      final View c = v.getChildAt(i);
      if (c instanceof TextView) {
        setCustomFont((TextView) c, tf);
      } else if (c instanceof ViewGroup) {
        setCustomFont((ViewGroup) c, tf);
      }
    }
  }

  private static void setCustomFont(TextView c, Typeface tf) {
    c.setTypeface(tf);
  }

  public static void setCustomFont(View topView, Typeface tf) {
    if (tf == null || topView == null) {
      return;
    }

    if (topView instanceof ViewGroup) {
      setCustomFont((ViewGroup) topView, tf);
    } else if (topView instanceof TextView) {
      setCustomFont((TextView) topView, tf);
    }
  }

  private static void setCustomFont(ViewGroup v, Typeface tf) {
    final int len = v.getChildCount();
    processsViewGroup(v, tf, len);
  }
}
