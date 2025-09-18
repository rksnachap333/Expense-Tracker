package me.rajesh.expensetracker.utils

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

object ToastUtils {

    fun showSuccess(context: Context, message: String, long: Boolean = false) {
        Toasty.success(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT, true).show()
    }

    fun showError(context: Context, message: String, long: Boolean = false) {
        Toasty.error(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT, true).show()
    }

    fun showInfo(context: Context, message: String, long: Boolean = false) {
        Toasty.info(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT, true).show()
    }

    fun showWarning(context: Context, message: String, long: Boolean = false) {
        Toasty.warning(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT, true).show()
    }
}