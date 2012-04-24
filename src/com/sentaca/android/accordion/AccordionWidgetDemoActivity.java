/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sentaca.android.accordion.utils.FontUtils;
import com.sentaca.android.accordion.widget.AccordionView;

public class AccordionWidgetDemoActivity extends Activity {
  private static final String TAG = "AccordionWidgetDemoActivity";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final AccordionView v = (AccordionView) findViewById(R.id.accordion_view);

    LinearLayout ll = (LinearLayout) v.findViewById(R.id.example_get_by_id);
    TextView tv = new TextView(this);
    tv.setText("Added in runtime...");
    FontUtils.setCustomFont(tv, getAssets());
    ll.addView(tv);
  }
}