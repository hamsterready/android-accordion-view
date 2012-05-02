/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sentaca.android.accordion.R;
import com.sentaca.android.accordion.utils.FontUtils;

public class AccordionView extends LinearLayout {

  private boolean initialized = false;

  // -- from xml parameter
  private int headerLayoutId;
  private int headerFoldButton;
  private int headerLabel;
  private int sectionContainer;
  private int sectionContainerParent;
  private int sectionBottom;

  private String[] sectionHeaders;

  private View[] children;
  private View[] wrappedChildren;

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
    }

    if (headerLayoutId == 0 || headerLabel == 0 || sectionContainer == 0 || sectionContainerParent == 0 || sectionBottom == 0) {
      throw new IllegalArgumentException(
          "Please set all header_layout_id,  header_layout_label_id, section_container, section_container_parent and section_bottom attributes.");
    }

    setOrientation(VERTICAL);
  }

  @Override
  protected void onFinishInflate() {
    if (initialized) {
      super.onFinishInflate();
      return;
    }

    final int childCount = getChildCount();
    children = new View[childCount];
    wrappedChildren = new View[childCount];

    if (sectionHeaders.length != childCount) {
      throw new IllegalArgumentException("Section headers string array length must be equal to accordion view child count.");
    }

    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    for (int i = 0; i < childCount; i++) {
      children[i] = getChildAt(i);
    }
    removeAllViews();

    for (int i = 0; i < childCount; i++) {
      wrappedChildren[i] = getView(inflater, i);
      View header = getViewHeader(inflater, i);
      View footer = getViewFooter(inflater);
      addView(header);
      addView(wrappedChildren[i]);
      addView(footer);
    }

    initialized = true;

    super.onFinishInflate();
  }

  private View getViewFooter(LayoutInflater inflater) {
    return inflater.inflate(sectionBottom, null);
  }

  private View getViewHeader(LayoutInflater inflater, final int position) {
    final View view = inflater.inflate(headerLayoutId, null);
    ((TextView) view.findViewById(headerLabel)).setText(sectionHeaders[position]);

    FontUtils.setCustomFont(view, AccordionView.this.getContext().getAssets());

    // -- support for no fold button
    if (headerFoldButton == 0) {
      return view;
    }

    final View foldButton = view.findViewById(headerFoldButton);

    if (foldButton instanceof ToggleImageLabeledButton) {
      final ToggleImageLabeledButton toggleButton = (ToggleImageLabeledButton) foldButton;
      toggleButton.setState(wrappedChildren[position].getVisibility() == VISIBLE);
    }

    final OnClickListener onClickListener = new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (wrappedChildren[position].getVisibility() == VISIBLE) {
          wrappedChildren[position].setVisibility(GONE);
        } else {
          wrappedChildren[position].setVisibility(VISIBLE);
        }
      }
    };
    foldButton.setOnClickListener(onClickListener);
    view.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        onClickListener.onClick(v);

        if (foldButton instanceof ToggleImageLabeledButton) {
          final ToggleImageLabeledButton toggleButton = (ToggleImageLabeledButton) foldButton;
          toggleButton.setState(wrappedChildren[position].getVisibility() == VISIBLE);
        }

      }
    });

    return view;
  }

  private View getView(final LayoutInflater inflater, int i) {
    final View container = inflater.inflate(sectionContainer, null);
    container.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
    final ViewGroup newParent = (ViewGroup) container.findViewById(sectionContainerParent);
    newParent.addView(children[i]);
    FontUtils.setCustomFont(container, AccordionView.this.getContext().getAssets());
    if (container.getId() == -1) {
      container.setId(i);
    }
    return container;
  }

  public View getChildById(int id) {
    for (int i = 0; i < wrappedChildren.length; i++) {
      View v = wrappedChildren[i].findViewById(id);
      if (v != null) {
        return v;
      }
    }
    return null;
  }

}
