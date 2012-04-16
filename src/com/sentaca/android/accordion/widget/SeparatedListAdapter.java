/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.sentaca.android.accordion.widget;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sentaca.android.accordion.R;

public class SeparatedListAdapter extends BaseAdapter {

  private final Map<String, ListAdapter> sections = new LinkedHashMap<String, ListAdapter>();
  private final ArrayAdapter<String> headers;
  public final static int TYPE_SECTION_HEADER = 0;

  private DataSetObserver mDataSetObserver = new DataSetObserver() {
    @Override
    public void onChanged() {
      notifyDataSetChanged();
    }
  };

  public SeparatedListAdapter(ArrayAdapter<String> headers) {
    super();
    this.headers = headers;
  }

  public SeparatedListAdapter(Context context, int headerLayoutId) {
    super();
    headers = new ArrayAdapter<String>(context, headerLayoutId);
  }

  public SeparatedListAdapter(Context context, int headerLayoutId, int textViewId) {
    super();
    headers = new ArrayAdapter<String>(context, headerLayoutId, textViewId);
  }

  public SeparatedListAdapter(Context context, ArrayAdapter<String> headersAdapter) {
    super();
    headers = headersAdapter;
  }

  public void addSection(String section, ListAdapter adapter) {
    this.headers.add(section);
    this.sections.put(section, adapter);

    // Register an observer so we can call notifyDataSetChanged() when our
    // children adapters are modified, otherwise no change will be visible.
    adapter.registerDataSetObserver(mDataSetObserver);
  }

  public boolean areAllItemsSelectable() {
    return false;
  }

  public int getCount() {
    // total together all sections, plus one for each section header
    int total = 0;
    for (Adapter adapter : this.sections.values()) {
      total += adapter.getCount() + 1;
    }
    return total;
  }

  public Object getItem(int position) {
    for (Object section : this.sections.keySet()) {
      Adapter adapter = sections.get(section);
      int size = adapter.getCount() + 1;

      // check if position inside this section
      if (position == 0) {
        return section;
      }
      if (position < size) {
        return adapter.getItem(position - 1);
      }

      // otherwise jump into next section
      position -= size;
    }
    return null;
  }

  public long getItemId(int position) {
    return position;
  }

  public int getItemViewType(int position) {
    int type = 1;
    for (Object section : this.sections.keySet()) {
      Adapter adapter = sections.get(section);
      int size = adapter.getCount() + 1;

      // check if position inside this section
      if (position == 0) {
        return TYPE_SECTION_HEADER;
      }
      if (position < size) {
        return type + adapter.getItemViewType(position - 1);
      }

      // otherwise jump into next section
      position -= size;
      type += adapter.getViewTypeCount();
    }
    return -1;
  }

  public Map<String, ListAdapter> getSections() {
    return sections;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    int sectionnum = 0;
    for (Object section : this.sections.keySet()) {
      Adapter adapter = sections.get(section);
      int size = adapter.getCount() + 1;

      // check if position inside this section
      if (position == 0) {
        if (!(convertView instanceof TextView)) {
          convertView = null;
        }
        return headers.getView(sectionnum, convertView, parent);
      }
      if (position < size) {
        if (!(convertView instanceof LinearLayout)) {
          convertView = null;
        }
        return adapter.getView(position - 1, convertView, parent);
      }

      // otherwise jump into next section
      position -= size;
      sectionnum++;
    }
    return null;
  }

  public int getViewTypeCount() {
    // assume that headers count as one, then total all sections
    int total = 1;
    for (Adapter adapter : this.sections.values())
      total += adapter.getViewTypeCount();
    return total;
  }

  public boolean isEnabled(int position) {
    if (getItemViewType(position) == TYPE_SECTION_HEADER) {
      return false;
    }

    int positionToUse = position;
    for (Object section : this.sections.keySet()) {
      ListAdapter adapter = sections.get(section);
      int size = adapter.getCount();
      positionToUse--;

      // check if position inside this section
      if (positionToUse < size) {
        return adapter.isEnabled(positionToUse);
      }

      positionToUse -= size;
    }

    // we should never be here
    return true;
  }

  public void removeObservers() {
    for (Map.Entry<String, ListAdapter> it : sections.entrySet()) {
      it.getValue().unregisterDataSetObserver(mDataSetObserver);
    }
  }

  public void removeSection(String section) {
    this.headers.remove(section);
    ListAdapter adpater = this.sections.remove(section);
    if (adpater != null) {
      adpater.unregisterDataSetObserver(mDataSetObserver);
    }
  }

}
