package com.kinstalk.m4.publicapi.view.Toasty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinstalk.m4.publicapi.R;

/**
 * This file is part of Toasty.
 * <p>
 * Toasty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Toasty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Toasty.  If not, see <http://www.gnu.org/licenses/>.
 */

@SuppressLint("InflateParams")
public class Toasty {
    @ColorInt
    private static int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    @ColorInt
    private static int ERROR_COLOR = Color.parseColor("#D50000");
    @ColorInt
    private static int INFO_COLOR = Color.parseColor("#3F51B5");
    @ColorInt
    private static int SUCCESS_COLOR = Color.parseColor("#388E3C");
    @ColorInt
    private static int WARNING_COLOR = Color.parseColor("#FFA900");
    @ColorInt
    private static int NORMAL_COLOR = Color.parseColor("#353A3E");
    @ColorInt
    private static int FULL_SCREEN_BG_COLOR = Color.parseColor("#f20f1b24");


    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private static int textSize = 24; // in SP

    private static boolean tintIcon = false;

    private Toasty() {
        // avoiding instantiation
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration, boolean fullscreen) {
        return normal(context, message, duration, null, fullscreen);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon, boolean fullscreen) {
        return normal(context, message, duration, icon, true, fullscreen);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon, boolean withIcon, boolean fullscreen) {
        return custom(context, message, icon, NORMAL_COLOR, duration, withIcon, fullscreen);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, boolean fullscreen) {
        return success(context, message, Toast.LENGTH_SHORT, fullscreen);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean fullscreen) {
        return success(context, message, duration, true, fullscreen);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon, boolean fullscreen) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.mipmap.ic_check_white_48dp),
                SUCCESS_COLOR, duration, withIcon, fullscreen);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, boolean fullscreen) {
        return error(context, message, Toast.LENGTH_SHORT, fullscreen);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean fullscreen) {
        return error(context, message, duration, true, fullscreen);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon, boolean fullscreen) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.mipmap.ic_error_outline_white_48dp),
                ERROR_COLOR, duration, withIcon, fullscreen);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context, message, icon, -1, duration, withIcon, true);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, @DrawableRes int iconRes,
                               @ColorInt int tintColor, int duration,
                               boolean withIcon, boolean fullscreen) {
        return custom(context, message, ToastyUtils.getDrawable(context, iconRes),
                tintColor, duration, withIcon, fullscreen);
    }

    @SuppressLint("ShowToast")
    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               @ColorInt int tintColor, int duration,
                               boolean withIcon, boolean fullscreen) {
        final Toast currentToast = new Toast(context);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);


        if (fullscreen) {
            toastLayout.setBackgroundColor(FULL_SCREEN_BG_COLOR);
            currentToast.setGravity(Gravity.FILL, 0, 0);
        } else {

            Drawable drawableFrame = ToastyUtils.getDrawable(context, R.drawable.toast_frame);
            ToastyUtils.setBackground(toastLayout, drawableFrame);
            currentToast.setGravity(Gravity.CENTER, 0, 40);
        }

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            if (tintIcon)
                icon = ToastyUtils.tintIcon(icon, DEFAULT_TEXT_COLOR);
            ToastyUtils.setBackground(toastIcon, icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(DEFAULT_TEXT_COLOR);
        toastTextView.setTypeface(currentTypeface);
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        currentToast.setDuration(duration);
        currentToast.setView(toastLayout);
        return currentToast;
    }

    public static class Config {
        @ColorInt
        private int DEFAULT_TEXT_COLOR = Toasty.DEFAULT_TEXT_COLOR;
        @ColorInt
        private int ERROR_COLOR = Toasty.ERROR_COLOR;
        @ColorInt
        private int INFO_COLOR = Toasty.INFO_COLOR;
        @ColorInt
        private int SUCCESS_COLOR = Toasty.SUCCESS_COLOR;
        @ColorInt
        private int WARNING_COLOR = Toasty.WARNING_COLOR;

        private Typeface typeface = Toasty.currentTypeface;
        private int textSize = Toasty.textSize;

        private boolean tintIcon = Toasty.tintIcon;

        private Config() {
            // avoiding instantiation
        }

        @CheckResult
        public static Config getInstance() {
            return new Config();
        }

        public static void reset() {
            Toasty.DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
            Toasty.ERROR_COLOR = Color.parseColor("#D50000");
            Toasty.INFO_COLOR = Color.parseColor("#3F51B5");
            Toasty.SUCCESS_COLOR = Color.parseColor("#388E3C");
            Toasty.WARNING_COLOR = Color.parseColor("#FFA900");
            Toasty.currentTypeface = LOADED_TOAST_TYPEFACE;
            Toasty.textSize = 24;
            Toasty.tintIcon = true;
        }

        @CheckResult
        public Config setTextColor(@ColorInt int textColor) {
            DEFAULT_TEXT_COLOR = textColor;
            return this;
        }

        @CheckResult
        public Config setErrorColor(@ColorInt int errorColor) {
            ERROR_COLOR = errorColor;
            return this;
        }

        @CheckResult
        public Config setInfoColor(@ColorInt int infoColor) {
            INFO_COLOR = infoColor;
            return this;
        }

        @CheckResult
        public Config setSuccessColor(@ColorInt int successColor) {
            SUCCESS_COLOR = successColor;
            return this;
        }

        @CheckResult
        public Config setWarningColor(@ColorInt int warningColor) {
            WARNING_COLOR = warningColor;
            return this;
        }

        @CheckResult
        public Config setToastTypeface(@NonNull Typeface typeface) {
//            this.typeface = typeface;
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        public void apply() {
            Toasty.DEFAULT_TEXT_COLOR = DEFAULT_TEXT_COLOR;
            Toasty.ERROR_COLOR = ERROR_COLOR;
            Toasty.INFO_COLOR = INFO_COLOR;
            Toasty.SUCCESS_COLOR = SUCCESS_COLOR;
            Toasty.WARNING_COLOR = WARNING_COLOR;
            Toasty.currentTypeface = typeface;
            Toasty.textSize = textSize;
            Toasty.tintIcon = tintIcon;
        }
    }
}
