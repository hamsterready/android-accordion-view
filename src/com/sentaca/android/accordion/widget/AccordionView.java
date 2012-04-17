/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sentaca.android.accordion.R;
import com.sentaca.android.accordion.utils.FontUtils;

public class AccordionView extends LinearLayout {

  private boolean initialized = false;
  private View[] newChildren;
  private ListView listView;

  // -- from xml parameter
  private int headerLayoutId;
  private int headerFoldButton;
  private int headerLabel;
  private int sectionContainer;
  private int sectionContainerParent;
  private int sectionBottom;

  private Typeface customFont;
  private String[] sectionHeaders;
  private View[] originalChildren;

  public AccordionView(Context context, AttributeSet attrs) {
    super(context, attrs);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.accordion);
      headerLayoutId = a.getResourceId(R.styleable.accordion_header_layout_id, 0);
      headerFoldButton = a.getResourceId(R.styleable.accordion_header_layout_fold_button_id, 0);
      headerLabel = a.getResourceId(R.styleable.accordion_header_layout_label_id, 0);
      sectionContainer = a.getResourceId(R.styleable.accordion_section_container, 0);
      sectionContainerParent = a.getResourceId(R.styleable.accordion_section_container_parent, 0);
      sectionBottom = a.getResourceId(R.styleable.accordion_section_bottom, 0);
      int sectionheadersResourceId = a.getResourceId(R.styleable.accordion_section_headers, 0);
      if (sectionheadersResourceId == 0) {
        throw new IllegalArgumentException("Please set section_headers as reference to strings array.");
      }
      sectionHeaders = getResources().getStringArray(sectionheadersResourceId);

      final String customFontString = a.getString(R.styleable.accordion_custom_font);
      if (customFontString != null) {
        customFont = Typeface.createFromAsset(context.getAssets(), customFontString);
      }
    }

    if (headerLayoutId == 0 || headerLabel == 0 || sectionContainer == 0 || sectionContainerParent == 0 || sectionBottom == 0) {
      throw new IllegalArgumentException(
          "Please set all header_layout_id,  header_layout_label_id, section_container, section_container_parent and section_bottom attributes.");
    }

  }

  @Override
  protected void onFinishInflate() {
    if (initialized) {
      super.onFinishInflate();
      return;
    }

    final int childCount = getChildCount();
    if (sectionHeaders.length != childCount) {
      throw new IllegalArgumentException("Section headers string array length must be equal to accordion view child count.");
    }

    this.newChildren = new View[childCount];
    this.originalChildren = new View[childCount];

    final OnClickListener[] sectionListeners = new OnClickListener[childCount];

    final ArrayAdapter<String> headersAdapater = new ArrayAdapter<String>(getContext(), headerLayoutId, headerLabel) {
      public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        FontUtils.setCustomFont(view, customFont);

        // -- support for no fold button
        if (headerFoldButton == 0) {
          return view;
        }

        final View foldButton = view.findViewById(headerFoldButton);

        if (foldButton instanceof ToggleImageLabeledButton) {
          final ToggleImageLabeledButton toggleButton = (ToggleImageLabeledButton) foldButton;
          toggleButton.setState(newChildren[position].getVisibility() == VISIBLE);
        }

        OnClickListener onClickListener = new OnClickListener() {

          @Override
          public void onClick(View v) {
            if (sectionListeners[position] != null) {
              sectionListeners[position].onClick(v);
            } else {
              // TODO WARN here
            }
          }
        };
        foldButton.setOnClickListener(onClickListener);
        view.setOnClickListener(onClickListener);
        return view;
      };
    };

    for (int i = 0; i < childCount; i++) {
      newChildren[i] = getChildAt(i);
      originalChildren[i] = getChildAt(i);
    }

    removeAllViews();

    final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    for (int i = 0; i < childCount; i++) {
      final View container = inflater.inflate(sectionContainer, null);
      // cast, XXX check it later
      final ViewGroup newParent = (ViewGroup) container.findViewById(sectionContainerParent);
      newParent.addView(newChildren[i]);
      newChildren[i] = container;
      FontUtils.setCustomFont(newChildren[i], customFont);
      newChildren[i].setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
    }

    final SeparatedListAdapter adapter = new SeparatedListAdapter(getContext(), headersAdapater);

    for (int i = 0; i < childCount; i++) {
      final int childIndex = i;
      final String sectionKey = sectionHeaders[i];
      adapter.addSection(sectionKey, new BaseAdapter() {

        @Override
        public View getView(int position, View arg1, ViewGroup arg2) {
          if (newChildren[childIndex].getVisibility() == GONE && position == 0 || position == 1) {
            return inflater.inflate(sectionBottom, null);
          }

          return newChildren[childIndex];
        }

        @Override
        public long getItemId(int arg0) {
          return 0;
        }

        @Override
        public Object getItem(int arg0) {
          return null;
        }

        @Override
        public int getCount() {
          if (newChildren[childIndex].getVisibility() == GONE) {
            return 1;
          }
          return 2;
        }
      });

      sectionListeners[childIndex] = new OnClickListener() {

        @Override
        public void onClick(View v) {
          final BaseAdapter sectionAdapter = (BaseAdapter) adapter.getSections().get(sectionKey);

          if (newChildren[childIndex].getVisibility() == VISIBLE) {
            newChildren[childIndex].setVisibility(GONE);
          } else {
            newChildren[childIndex].setVisibility(VISIBLE);
          }
          sectionAdapter.notifyDataSetChanged();

        }
      };
    }

    listView = new ListView(getContext());
    listView.setDivider(null);
    listView.setDividerHeight(0);
    listView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
    listView.setAdapter(adapter);
    listView.setFocusable(false);
    addView(listView);
    initialized = true;

    super.onFinishInflate();
  }

  public View getChildById(int id) {
    for (int i = 0; i < originalChildren.length; i++) {
      if (originalChildren[i].getId() == id) {
        return originalChildren[i];
      }
    }
    return null;
  }

  public int getOriginalChildCount() {
    return originalChildren.length;
  }
}
