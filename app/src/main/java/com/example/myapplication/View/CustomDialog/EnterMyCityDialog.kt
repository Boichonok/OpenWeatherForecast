package com.example.myapplication.View.CustomDialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.example.myapplication.R
import kotlinx.android.synthetic.main.add_my_city_dialog.*

class EnterMyCityDialog : Dialog {

    private lateinit var activity: Activity

    private lateinit var infoDialog: String
    private lateinit var hintEditText: String
    private lateinit var title: String
    private lateinit var okAction: (enteredCity: String) -> Unit

    constructor(activity: Activity) : super(activity) {
        this.activity = activity
    }

    private constructor(
        activity: Activity,
        infoDialog: String,
        title: String,
        hintEditText: String,
        okAction: (enteredCity: String) -> Unit
    ) : super(activity) {
        this.activity = activity
        this.infoDialog = infoDialog
        this.okAction = okAction
        this.hintEditText = hintEditText
        this.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_my_city_dialog)
        setHintEditText(hintEditText)
        setInfoDialog(infoDialog)
        setTitle(title)
        setOkButtonClickListener(okAction)
    }

    private fun setInfoDialog(infoDialog: String) {
        dialog_info.text = infoDialog
    }

    private fun setHintEditText(hint: String) {
        dialog_edit_city_name.hint = hint
    }

    private fun setTitle(title: String) {
        dialog_title.text = title
    }

    private fun setOkButtonClickListener(okAction: (enteredCity: String) -> Unit) {
        dialog_ok_button.setOnClickListener {
            var editedText = dialog_edit_city_name.text.toString()
            if (editedText != null) {
                okAction(editedText)
                dismiss()
            } else {
                dialog_info.text = "Please enter city name"
            }
        }
    }

    public fun showDialog()
    {
        window.attributes.windowAnimations = R.style.CustomDialogTheme
        show()
    }

    class Builder {
        private lateinit var activity: Activity

        private lateinit var infoDialog: String
        private lateinit var hintEditText: String
        private lateinit var title: String
        private lateinit var okAction: (enteredCity: String) -> Unit


        constructor(activity: Activity) {
            this.activity = activity
        }

        fun setInfoDialog(infoDialog: String): Builder {
            this.infoDialog = infoDialog
            return this
        }

        fun setHintEditText(hint: String): Builder {
            this.hintEditText = hint
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setOkButtonClickListener(okAction: (enteredCity: String) -> Unit): Builder {
            this.okAction = okAction
            return this
        }

        fun build(): EnterMyCityDialog = EnterMyCityDialog(activity,infoDialog,title,hintEditText,okAction)


    }
}