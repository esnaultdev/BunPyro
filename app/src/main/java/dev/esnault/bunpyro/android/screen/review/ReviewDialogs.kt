package dev.esnault.bunpyro.android.screen.review

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import dev.esnault.bunpyro.android.screen.review.ReviewViewState.DialogMessage
import dev.esnault.bunpyro.R
import android.view.KeyEvent
import com.afollestad.materialdialogs.callbacks.onDismiss


class ReviewDialogs(private val listener: Listener, private val context: Context) {

    data class Listener(
        val onDismiss: () -> Unit,
        val onQuitConfirm: () -> Unit,
        val onSyncRetry: () -> Unit,
        val onSyncQuit: () -> Unit
    )

    private var dialog: MaterialDialog? = null

    fun showDialog(dialogMessage: DialogMessage?) {
        when (dialogMessage) {
            is DialogMessage.QuitConfirm -> showQuitConfirmDialog()
            is DialogMessage.SyncError -> showSyncErrorDialog()
            null -> dismissDialog()
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    private fun dismissDialogSilently() {
        dialog?.apply {
            setOnDismissListener(null)
            dismiss()
        }
        dialog = null
    }

    private fun showQuitConfirmDialog() {
        dismissDialogSilently()
        dialog = MaterialDialog(context)
            .show {
                title(R.string.reviews_dialog_quitWarning_title)
                message(R.string.reviews_dialog_quitWarning_message)
                negativeButton(R.string.reviews_dialog_quitWarning_cancel)
                positiveButton(R.string.reviews_dialog_quitWarning_ok) {
                    listener.onQuitConfirm()
                }
                onDismiss { listener.onDismiss() }
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss()
                        listener.onQuitConfirm()
                        true
                    } else {
                        false
                    }
                }
            }
    }

    private fun showSyncErrorDialog() {
        dismissDialogSilently()
        dialog = MaterialDialog(context)
            .show {
                title(R.string.reviews_dialog_syncError_title)
                message(R.string.reviews_dialog_syncError_message)
                negativeButton(R.string.reviews_dialog_syncError_cancel) {
                    listener.onSyncQuit()
                }
                positiveButton(R.string.reviews_dialog_syncError_ok) {
                    listener.onSyncRetry()
                }
                onDismiss { listener.onDismiss() }
                cancelable(false)
            }
    }
}
