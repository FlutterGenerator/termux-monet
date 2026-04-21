package com.termux.app.terminal.io;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import com.termux.terminal.TerminalSession;

public class TerminalToolbarViewPager {

    public static class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {

        final TermuxActivity mActivity;
        String mSavedTextInput;
        private EditText mEditText;

        public PageAdapter(TermuxActivity activity, String savedTextInput) {
            this.mActivity = activity;
            this.mSavedTextInput = savedTextInput;
        }

        public EditText getEditText() {
            return mEditText;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View layout;

            if (viewType == 0 || viewType == 1) {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_extra_keys, parent, false);
                ExtraKeysView extraKeysView = (ExtraKeysView) layout;
                extraKeysView.setExtraKeysViewClient(mActivity.getTermuxTerminalExtraKeys(viewType));
                extraKeysView.setButtonTextAllCaps(mActivity.getProperties().shouldExtraKeysTextBeAllCaps());
                mActivity.setExtraKeysView(extraKeysView, viewType);
                extraKeysView.reload(mActivity.getTermuxTerminalExtraKeys(viewType).getExtraKeysInfo(), mActivity.getTerminalToolbarDefaultHeight());
                // apply extra keys fix if enabled in prefs
                if (mActivity.getProperties().isUsingFullScreen() && mActivity.getProperties().isUsingFullScreenWorkAround()) {
                    FullScreenWorkAround.apply(mActivity);
                }
            } else {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_text_input, parent, false);

                final Button button = layout.findViewById(R.id.terminal_toolbar_text_input_button);
                button.setOnClickListener(v -> {
                    ViewPager2 pager = mActivity.getTerminalToolbarViewPager();
                    pager.setCurrentItem(0, true);
                });

                mEditText = layout.findViewById(R.id.terminal_toolbar_text_input);
                if (mSavedTextInput != null) {
                    mEditText.setText(mSavedTextInput);
                    mSavedTextInput = null;
                }
                mEditText.setOnEditorActionListener((v, actionId, event) -> {
                    TerminalSession session = mActivity.getCurrentSession();
                    if (session != null) {
                        if (session.isRunning()) {
                            String textToSend = mEditText.getText().toString();
                            if (textToSend.length() == 0)
                                textToSend = "\r";
                            session.write(textToSend);
                        } else {
                            mActivity.getTermuxTerminalSessionClient().removeFinishedSession(session);
                        }
                        mEditText.setText("");
                    }
                    return true;
                });
            }

            return new ViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // логика реализована в onCreateViewHolder через viewType
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public static class OnPageChangeListener extends ViewPager2.OnPageChangeCallback {

        final TermuxActivity mActivity;
        final ViewPager2 mTerminalToolbarViewPager;
        final PageAdapter mAdapter;

        public OnPageChangeListener(TermuxActivity activity, ViewPager2 viewPager, PageAdapter adapter) {
            this.mActivity = activity;
            this.mTerminalToolbarViewPager = viewPager;
            this.mAdapter = adapter;
        }

        @Override
        public void onPageSelected(int position) {
            mActivity.setTerminalToolbarHeight();
            if (position == 0 || position == 1) {
                mActivity.getTerminalView().requestFocus();
            } else {
                EditText editText = mAdapter.getEditText();
                if (editText != null)
                    editText.requestFocus();
            }
        }
    }
}
