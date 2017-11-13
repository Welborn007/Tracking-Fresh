//package com.kesari.trackingfresh.MyOffers;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.sbi.upi.R;
//import com.sbi.upi.common.AppConstants;
//import com.sbi.upi.common.DeviceDetailsSingleton;
//import com.sbi.upi.common.LogUtil;
//import com.sbi.upi.common.UiUtil;
//import com.sbi.upi.common.data.models.response.banking.AccountDetail;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by Mohan Raj  on 2/20/2017.
// */
//
//public class AccountsHorizontalRecyclerAdapter extends RecyclerView.Adapter {
//    AccountListener accountListener;
//    private final String TAG = getClass().getSimpleName();
//    List<AccountDetail> accountDetails;
//    Context context;
//
//    int ITEM_ACCOUNT_TYPE = 0;
//    int ITEM_SETTINGS_TYPE = 1;
//
//    public AccountsHorizontalRecyclerAdapter(List<AccountDetail> accountDetails, AccountListener accountListener) {
//        this.accountDetails = accountDetails;
//        this.accountListener = accountListener;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        if (viewType == ITEM_SETTINGS_TYPE) {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings_card, parent, false);
//            itemView.getLayoutParams().width = (int) ((DeviceDetailsSingleton.getInstance().getScreenWidth()) / AppConstants.ACCOUNT_CARD_WIDTH);
//            return new SettingsViewHolder(itemView);
//
//        } else {
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_card, parent, false);
//            itemView.getLayoutParams().width = (int) ((DeviceDetailsSingleton.getInstance().getScreenWidth()) / AppConstants.ACCOUNT_CARD_WIDTH);
//            return new AccountViewHolder(itemView);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        LogUtil.info(TAG, "position : " + position + " accountDetails.size() : " + accountDetails.size());
//        if (position == accountDetails.size()) {
//            SettingsViewHolder settingsViewHolder = (SettingsViewHolder) holder;
//            settingsViewHolder.addAccountTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/open-sans.semibold.ttf"));
//            settingsViewHolder.accountSettingsTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/open-sans.semibold.ttf"));
//            UiUtil.setCompoundVectorDrawableWithIntrinsicBounds(settingsViewHolder.addAccountTextView, 0, R.drawable.ic_add_circular, 0, 0);
//            UiUtil.setCompoundVectorDrawableWithIntrinsicBounds(settingsViewHolder.accountSettingsTextView, R.drawable.ic_settings, 0, 0, 0);
//
//            settingsViewHolder.addAccountTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setFilterTouchesWhenObscured(true);
//
//                    accountListener.onAddAccountClicked();
//                }
//            });
//            settingsViewHolder.accountSettingsTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setFilterTouchesWhenObscured(true);
//
//                    accountListener.onAccountSettingsClicked();
//                }
//            });
//
//        } else {
//            final AccountViewHolder accountViewHolder = (AccountViewHolder) holder;
//            if (accountDetails.get(position).preferredFlag.equals(AppConstants.TRUE_FLAG)) {
//                accountViewHolder.primaryAccountImageView.setVisibility(View.VISIBLE);
//            } else
//                accountViewHolder.primaryAccountImageView.setVisibility(View.GONE);
//            accountViewHolder.bankNameTextView.setSelected(true);
//            accountViewHolder.bankNameTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/open-sans.semibold.ttf"));
//            accountViewHolder.accountActionTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/open-sans.semibold.ttf"));
//
//            accountViewHolder.bankLogoImageView.setImageResource(UiUtil.getBankLogoResId(accountDetails.get(position).bankCode));
//            accountViewHolder.bankNameTextView.setText(accountDetails.get(position).bankName);
//            accountViewHolder.accountNumberTextView.setText(accountDetails.get(position).accountNumber);
//            if (accountDetails.get(position).accountBalance != null && !accountDetails.get(position).accountBalance.isEmpty()) {
//                accountViewHolder.accountActionTextView.setText(context.getResources().getString(R.string.rupee) + " " + accountDetails.get(position).accountBalance);
//                accountViewHolder.refreshImageView.setVisibility(View.VISIBLE);
//
//            } else {
//                accountViewHolder.refreshImageView.setVisibility(View.GONE);
//                if (accountDetails.get(position).upiPinFlag != null && accountDetails.get(position).upiPinFlag.equals(AppConstants.YES_FLAG)) {
//                    accountViewHolder.accountActionTextView.setText(context.getString(R.string.view_balance));
//                } else {
//                    accountViewHolder.accountActionTextView.setText(context.getString(R.string.set_mpin));
//                }
//            }
//            accountViewHolder.accountActionTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setFilterTouchesWhenObscured(true);
//                    if (accountDetails.get(position).upiPinFlag != null && accountDetails.get(position).upiPinFlag.equals(AppConstants.YES_FLAG))
//                        accountViewHolder.accountActionTextView.setText(R.string.fetching_balance);
//                    accountListener.onAccountActionClicked(position);
//                }
//            });
//
//
//            accountViewHolder.refreshImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setFilterTouchesWhenObscured(true);
//                    accountViewHolder.refreshImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_anticlockwise));
//                    accountViewHolder.accountActionTextView.setText(R.string.fetching_balance);
//                    accountListener.onAccountActionClicked(position);
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return accountDetails.size() + 1;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position == accountDetails.size() ? ITEM_SETTINGS_TYPE : ITEM_ACCOUNT_TYPE;
//    }
//
//    class AccountViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.primaryAccountImageView)
//        ImageView primaryAccountImageView;
//        @BindView(R.id.refreshImageView)
//        ImageView refreshImageView;
//        @BindView(R.id.bankLogoImageView)
//        ImageView bankLogoImageView;
//        @BindView(R.id.bankNameTextView)
//        TextView bankNameTextView;
//        @BindView(R.id.accountNumberTextView)
//        TextView accountNumberTextView;
//        @BindView(R.id.accountActionTextView)
//        TextView accountActionTextView;
//        @BindView(R.id.itemLayout)
//        RelativeLayout accountLayout;
//
//
//        public AccountViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//
//    class SettingsViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.settingsLayout)
//        RelativeLayout settingsLayout;
//        @BindView(R.id.accountSettingsTextView)
//        TextView accountSettingsTextView;
//        @BindView(R.id.addAccountTextView)
//        TextView addAccountTextView;
//
//        public SettingsViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    interface AccountListener {
//        void onAccountActionClicked(int index);
//
//        void onAddAccountClicked();
//
//        void onAccountSettingsClicked();
//    }
//
//}
