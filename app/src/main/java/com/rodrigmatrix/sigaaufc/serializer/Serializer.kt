package com.rodrigmatrix.sigaaufc.serializer

import android.annotation.SuppressLint
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.*
import org.jsoup.Jsoup
import java.text.Normalizer

class Serializer {

    fun loginParse(response: String?): String{
        return when {
            response!!.contains("value=\"Continuar") -> "Continuar"
            response.contains("Menu Principal") -> "Menu Principal"
            response.contains("Usuário e/ou senha inválidos") -> "Aluno não encontrado"
            response.contains("Por favor, aguarde enquanto carregamos as suas") -> "Menu Principal"
            response.contains("Tentativa de acesso por aplicativo externo. Operação negada") -> "Tentativa de acesso por aplicativo externo."
            else -> "Erro ao efetuar login. Verifique os seus dados"
        }
    }

    @SuppressLint("DefaultLocale")
    fun parseClasses(response: String?): Pair<Student, MutableList<StudentClass>>{
        val classes = mutableListOf<StudentClass>()
        val turmaId = mutableListOf<String>()
        val names = mutableListOf<String>()
        val periodsList = mutableListOf<String>()
        val locationsList = mutableListOf<String>()
        val student = Student("", "", "",
            "", "", "", false, "", "")
        Jsoup.parse(response).run {
            select("input[value]").forEach {name ->
                if(name.attr("name").contains("idTurma")){
                    turmaId.add(name.attr("value"))
                }
            }
            val elements = getElementsByClass("descricao")
            val periods = getElementsByClass("info")


            elements.forEach {
                val el = it.select("a[id]")
                el.forEach { name ->
                    names.add(name.text())
                }
            }
            var count = 1
            periods.forEach { period ->
                if(count % 2 == 0){
                    periodsList.add(period.text())
                }
                else{
                    locationsList.add(period.text())
                }
                count++
            }
            var id = 1
            for(it in turmaId){
                val pairDates = parseDates(periodsList[id - 1])
                classes.add(
                    StudentClass(
                        turmaId[id - 1],
                        id,
                        false,
                        "",
                        "",
                        names[id - 1],
                        locationsList[id - 1],
                        pairDates.second,
                        pairDates.first,
                        0,
                        0
                    )
                )
                println(classes[id-1])
                id++
            }

            val studentName = select("div[class=nome_usuario]")
            student.name = studentName.text().toLowerCase().capitalizeWords()
            var matricula = response?.split("<td> Matr&#237;cula: </td>\n" + "\t\t\t\t\t\t<td> ")
            matricula = matricula!![1].split(" </td>")
            student.matricula = matricula[0]
            val curso = response?.split("<td> Curso: </td>\n" + "\t\t\t\t\t\t<td> ")
            val c = curso?.get(1)!!.split(" </td>")[0]
            student.course = c.toLowerCase().capitalizeWords()
            val linkProfilePic = select("img[src]")[0].attr("src")
            student.profilePic = linkProfilePic
            return Pair(student, classes)
        }
    }

    private fun parseDates(text: String): Pair<String, String>{
        val days = text.split(" (")[0]
        val period = text.split(" (")[1].removeSuffix(")")
        return Pair(days, period)
    }



    fun parsePreviousClasses(response: String?): MutableList<StudentClass>{
        val classes = mutableListOf<StudentClass>()
        val classItem =
            StudentClass("", 0, true, "", "", "", "", "", "", 0, 0)
        var index = 0
        var count = 1
        Jsoup.parse(response).run {
            select("td").forEach {
                if(index >= 8){
                    if(it.attr("class").contains("period")){
                        count = 0
                    }
                    else{
                        when(count){
                            1 -> {
                                classItem.code = it.text()
                            }
                            2 -> {
                                classItem.name = it.text()
                            }
                            5 -> {
                                classItem.credits = it.text()
                            }
                            6 -> {
                                classItem.days = it.text()
                            }
                            7 -> {
                                val script = it.select("a").attr("onclick").toString()
                                val idTurma = script.split("idTurma,")[1].split("',")[0]
                                classes.add(
                                    StudentClass(
                                        idTurma, classes.size + 1, true, classItem.credits,
                                        classItem.code, classItem.name, classItem.days, "", "", 0, 0
                                    )
                                )
                                count = 0
                            }
                        }
                    }
                    count++
                }
                index++
            }
        }
        classes.forEach {
            println(it)
        }
        return classes
    }

    fun parseNews(response: String?): MutableList<News>{
        val news = mutableListOf<News>()
        Jsoup.parse(response).run {
            val content = select("i")
            val dates = select("br")
//            content.forEach {
//                news.add(News("","1", "", it.text()))
//            }
            dates.forEach {
                println(it)
            }
        }
        return news
    }

    fun parseFiles(response: String?){

    }

    fun parseAttendance(response: String?){
        val missed = response!!.split("Total de Faltas: ")[1].split("<br/>")[0]
        val total = response!!.split("Faltas Permitido: ")[1].split("</div> ")[0]
        println(missed)
        println(total)
    }

    fun parseGrades(idTurma: String, response: String?): MutableList<Grade>{
        val grades = mutableListOf<Grade>()
        Jsoup.parse(response).run {
            val th = select("th")
            val tbody = select("tbody").select("td")
            var index = 0
            th.forEach {
                println(it.text())
                println(tbody[index].text())
                //grades.add(Grade("2", idTurma, it.text(), tbody[index].text()))
                index++
            }
        }
        return grades
    }



    @SuppressLint("DefaultLocale")
    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

    @SuppressLint("DefaultLocale")
    fun parseRU(response: String?): Triple<String, Pair<String, Int>, MutableList<HistoryRU>>{
        when {
            response!!.contains("O campo 'Matrícula atrelada ao cartão' é de preenchimento obrigatório.") -> {
                return Triple("Matrícula não encontrada", Pair("", 0), mutableListOf())
            }
            response.contains("Não existem dados a serem exibidos") -> {
                return Triple("Matrícula ou cartão não encontrados", Pair("", 0), mutableListOf())
            }
            response.contains("Refeições disponíveis") -> {
                val history = mutableListOf<HistoryRU>()
                val elem = HistoryRU(1, "", "", "", "")
                Jsoup.parse(response).run {
                    val operations = select("td[nowrap=nowrap]")
                    val name = operations[1].text().toLowerCase().capitalizeWords()
                    val credits = operations[3].text().toInt()
                    var count = 1
                    for((index, it) in operations.withIndex()){
                        if(index >= 4){
                            when(count){
                                1 -> {
                                    elem.id = index
                                    elem.date = it.text().removeRange(10, it.text().length)
                                    elem.time = it.text().removeRange(0, 11)
                                    count++
                                }
                                2 -> {
                                    elem.operation = it.text()
                                    count++
                                }
                                3 -> {
                                    elem.content = it.text()
                                    history.add(
                                        HistoryRU(
                                            index,
                                            elem.date,
                                            elem.time,
                                            elem.operation,
                                            elem.content
                                        )
                                    )
                                    count = 1
                                }
                            }
                        }
                    }
                    return Triple("Success", Pair(name, credits), history)
                }
            }
        }
        return Triple("Erro ao converter dados", Pair("", 1), mutableListOf())
    }
}