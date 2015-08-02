package com.gulu.album;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class TouchEventRecorderActivity extends BaseActivity {

	private TextView mFootprintView;
	private SpannableStringBuilder mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_event_recorder);

		mFootprintView = (TextView) findViewById(R.id.footprint_content);
		mContent = new SpannableStringBuilder();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int start = mContent.length();
		int end = start;
		mContent.append(actionToString(event.getAction()));
		end = mContent.length();

		debug("start=" + start + ",end=" + end);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mContent.setSpan(new BackgroundColorSpan(Color.GREEN), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			mContent.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			break;

		case MotionEvent.ACTION_MOVE:
			mContent.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			mContent.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			break;

		case MotionEvent.ACTION_UP:
			mContent.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			mContent.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}
		mContent.append("\n");
		mFootprintView.setText(mContent);

		if (action == MotionEvent.ACTION_UP) {
			mContent.clear();
		}

		return super.onTouchEvent(event);
	}

	public static String actionToString(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			return "ACTION_DOWN";
		case MotionEvent.ACTION_UP:
			return "ACTION_UP";
		case MotionEvent.ACTION_CANCEL:
			return "ACTION_CANCEL";
		case MotionEvent.ACTION_OUTSIDE:
			return "ACTION_OUTSIDE";
		case MotionEvent.ACTION_MOVE:
			return "ACTION_MOVE";
		case MotionEvent.ACTION_HOVER_MOVE:
			return "ACTION_HOVER_MOVE";
		case MotionEvent.ACTION_SCROLL:
			return "ACTION_SCROLL";
		case MotionEvent.ACTION_HOVER_ENTER:
			return "ACTION_HOVER_ENTER";
		case MotionEvent.ACTION_HOVER_EXIT:
			return "ACTION_HOVER_EXIT";
		}
		int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			return "ACTION_POINTER_DOWN(" + index + ")";
		case MotionEvent.ACTION_POINTER_UP:
			return "ACTION_POINTER_UP(" + index + ")";
		default:
			return Integer.toString(action);
		}
	}
}
