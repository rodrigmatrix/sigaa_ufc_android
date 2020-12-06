package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.firebase.PROFILE_BUTTON
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.internal.util.showProfileDialog
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance.AttendanceFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.files.FilesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.fragment.NewsFragment
import kotlinx.android.synthetic.main.activity_class_selected.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance

class ClassActivity : ScopedActivity() {

    private lateinit var viewModel: ClassViewModel
    private val viewModelFactory: ClassViewModelFactory by instance()
    private val studentDao: StudentDao by instance()

    private lateinit var sectionsPagerAdapter: ClassPagerAdapter
    lateinit var idTurma: String
    lateinit var id: String
    private var isPrevious = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_selected)
        loadProfilePic()
        sectionsPagerAdapter = ClassPagerAdapter(this, supportFragmentManager)
        idTurma = intent.getStringExtra("idTurma")!!
        id = intent.getStringExtra("id")!!
        isPrevious = intent.getBooleanExtra("isPrevious", false)
        println("idturma activity string $idTurma")
        val bundle = Bundle()
        bundle.putString("idTurma", idTurma)
        bundle.putBoolean("isPrevious", isPrevious)
        val attendanceFragment = AttendanceFragment()
        val gradesFragment = GradesFragment()
        val newsFragment = NewsFragment()
        val filesFragment = FilesFragment()
        attendanceFragment.arguments = bundle
        newsFragment.arguments = bundle
        gradesFragment.arguments = bundle
        filesFragment.arguments = bundle
        sectionsPagerAdapter.addFragment(filesFragment)
        sectionsPagerAdapter.addFragment(gradesFragment)
        sectionsPagerAdapter.addFragment(newsFragment)
        sectionsPagerAdapter.addFragment(attendanceFragment)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 4
        val tabs: TabLayout = findViewById(R.id.tabs)
        title = "Disciplina"
        tabs.setupWithViewPager(viewPager)
        viewModel = ViewModelProvider(this, viewModelFactory)[ClassViewModel::class.java]
        setTabs()

        setClass()
    }

    private fun loadProfilePic() = launch{
        try {
            val student = withContext(Dispatchers.IO) {
                studentDao.getStudentAsync()
            }
            val profilePic = student.profilePic
            if(profilePic != "/sigaa/img/no_picture.png"){
                GlideApp.with(this@ClassActivity)
                    .load("https://si3.ufc.br/$profilePic")
                    .into(profile_pic)
            }
            else{
                profile_pic.setImageResource(R.drawable.avatar_circle_blue)
            }
            profile_pic_card.setOnClickListener {
                events.addEvent(PROFILE_BUTTON)
                showProfileDialog(profile_pic_card, student)
            }
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun observeClass(){
        launch {
            viewModel.getCurrentClass(idTurma).observe(this@ClassActivity, Observer {
                if(it == null) return@Observer
                title = try {
                    it.name.split(" - ")[1]
                } catch(e: IndexOutOfBoundsException){
                    it.name
                }

            })
        }
    }

    private fun observePreviousClass(){
        launch {
            viewModel.getPreviousClass(idTurma).observe(this@ClassActivity, Observer {
                if(it == null) return@Observer
                title = try {
                    it.name.split(" - ")[1]
                } catch(e: IndexOutOfBoundsException){
                    it.name
                }

            })
        }
    }

    private fun setTabs(){
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_download)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_assessment)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_news)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_attendance)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            sectionsPagerAdapter.removeFragments()
            finish()
        }
    }

    private fun setClass(){
        if(isPrevious){
            observePreviousClass()
            launch(handler) {
                viewModel.fetchPreviousClasses()
                viewModel.setPreviousClass(id, idTurma)
                runOnUiThread {
                    progress_sigaa.isVisible = false
                }
            }
        }
        else{
            observeClass()
            launch(handler) {
                viewModel.fetchCurrentClasses()
                viewModel.setClass(id, idTurma)
                runOnUiThread {
                    progress_sigaa.isVisible = false
                }
            }
        }
    }

    override fun onBackPressed(){
        sectionsPagerAdapter.removeFragments()
        finish()
    }

    override fun onDestroy() {
        sectionsPagerAdapter.removeFragments()
        super.onDestroy()
    }
}