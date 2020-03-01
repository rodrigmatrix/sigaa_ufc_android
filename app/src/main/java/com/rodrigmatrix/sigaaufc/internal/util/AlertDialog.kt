package com.rodrigmatrix.sigaaufc.internal.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.dialog_header_profile.view.*
import java.lang.Exception

@SuppressLint("SetTextI18n")
fun Context.showProfileDialog(layout: View, student: Student){
    if(student.name == ""){
        return
    }
    val builder = MaterialAlertDialogBuilder(this)
    val inflater = LayoutInflater.from(builder.context)
    val view = inflater.inflate(R.layout.dialog_header_profile, null)
    view.apply {
        student_name.text = student.name
        login_text.text = "Login: ${student.login}"
        matricula_text.text = "Matrícula: ${student.matricula}"
        if(student.course != ""){
            course_text.visibility = View.VISIBLE
            course_text.text = "Curso: ${student.course}"
        }
        else{
            course_text.visibility = View.GONE
        }
        if(student.nivel != ""){
            nivel_text.text = "Nível: ${student.nivel}"
            nivel_text.visibility = View.VISIBLE
        }
        else{
            nivel_text.visibility = View.GONE
        }
        if(student.email != ""){
            email_text.text = "E-Mail: ${student.email}"
            email_text.visibility = View.VISIBLE
        }
        else{
            email_text.visibility = View.GONE
        }
        if(student.entrada != ""){
            entrada_text.text = "Entrada: ${student.entrada}"
            entrada_text.visibility = View.VISIBLE
        }
        else{
            entrada_text.visibility = View.GONE
        }

    }
    val profilePic = student.profilePic
    if(profilePic != "/sigaa/img/no_picture.png" && profilePic != ""){
        GlideApp.with(this)
            .load("https://si3.ufc.br/$profilePic")
            .into(view.profile_pic)
    }
    else{
        view.profile_pic.setImageResource(R.drawable.avatar_circle_blue)
    }
    builder.apply {
        setView(view)
        setPositiveButton("Ok") { d, _ ->
            d.dismiss()
        }
    }
    val dialog = builder.create()
    dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    val window = dialog.window
    if (window != null) {
        val layoutParamss = window.attributes
        layoutParamss.windowAnimations = R.style.AlertDialogAnimationDown
        layoutParamss.gravity = Gravity.TOP
        layoutParamss.y = (layout.y + 20).toInt()
    }
    try {
        dialog.show()
    } catch(e: Exception) {
        e.printStackTrace()
    }
}