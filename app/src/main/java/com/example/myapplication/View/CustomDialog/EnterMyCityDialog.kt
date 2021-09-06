package com.example.myapplication.View.CustomDialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.example.myapplication.R
import kotlinx.android.synthetic.main.add_my_city_dialog.*

class EnterMyCityDialog(activity: Activity) : Dialog(activity) {

    private lateinit var hintEditText: String
    private lateinit var title: String
    private lateinit var okAction: (enteredCity: String) -> Unit


    private constructor(
        activity: Activity,
        title: String,
        hintEditText: String,
        okAction: (enteredCity: String) -> Unit
    ) : this(activity) {
        this.okAction = okAction
        this.hintEditText = hintEditText
        this.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_my_city_dialog)
        setHintEditText(hintEditText)
        setTitle(title)
        setOkButtonClickListener(okAction)
    }

    private fun setHintEditText(hint: String) {
        dialog_edit_city_name.hint = hint
    }

    private fun setTitle(title: String) {
        dialog_title.text = title
    }

    private fun setOkButtonClickListener(okAction: (enteredCity: String) -> Unit) {
        dialog_ok_button.setOnClickListener {
            val editedText = dialog_edit_city_name.text.toString()
            okAction(editedText)
            dismiss()
        }
    }

    fun showDialog()
    {
        window?.attributes?.windowAnimations = R.style.CustomDialogTheme
        show()
    }

    class Builder(private val activity: Activity) {

        private lateinit var hintEditText: String
        private lateinit var title: String
        private lateinit var okAction: (enteredCity: String) -> Unit


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

        fun build(): EnterMyCityDialog = EnterMyCityDialog(activity,title,hintEditText,okAction)


    }
}