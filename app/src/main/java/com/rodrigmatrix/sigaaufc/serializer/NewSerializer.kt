package com.rodrigmatrix.sigaaufc.serializer

import android.annotation.SuppressLint
import com.rodrigmatrix.sigaaufc.internal.util.capitalizeWords
import com.rodrigmatrix.sigaaufc.persistence.entity.*
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_ERROR
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_SUCCESS
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_VINCULO
import org.jsoup.Jsoup
import java.lang.IndexOutOfBoundsException
import kotlin.random.Random

class NewSerializer {


    fun parseLogin(response: String?): LoginStatus{
        return when {
            response!!.contains("value=\"Continuar") -> LoginStatus(LOGIN_SUCCESS, "", response)
            response.contains("Menu Principal") -> LoginStatus(LOGIN_SUCCESS, "Menu Principal", response)
            response.contains("Usuário e/ou senha inválidos") -> LoginStatus(LOGIN_ERROR, "Aluno e/ou senha não encontrado", response)
            response.contains("Por favor, aguarde enquanto carregamos as suas") -> LoginStatus(LOGIN_SUCCESS, "Menu Principal", response)
            response.contains("Tentativa de acesso por aplicativo externo. Operação negada") -> LoginStatus(LOGIN_ERROR, "Tentativa de acesso por aplicativo externo.", response)
            response.contains("nculo ativo com a universidade") -> LoginStatus(LOGIN_VINCULO, "Vinculo", response)
            else -> LoginStatus(LOGIN_ERROR, "Erro ao efetuar login. Por favor me envie um email (tela sobre)", response)
        }
    }

    @SuppressLint("DefaultLocale")
    fun parseClasses(response: String?): List<StudentClass>{
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
                val jId = if(classes.size == 0){
                    ""
                } else{
                    "j_id_${classes.size}"
                }
                classes.add(
                    StudentClass(
                        turmaId[id - 1],
                        jId,
                        false,
                        "",
                        "",
                        names[id - 1].toLowerCase().capitalizeWords(),
                        locationsList[id - 1],
                        pairDates.second,
                        pairDates.first,
                        0,
                        0
                    )
                )
                id++
            }
            try {
                val studentName = select("div[class=nome_usuario]")
                student.name = studentName.text().toLowerCase().capitalizeWords()
                val linkProfilePic = select("img[src]")[0].attr("src")
                student.profilePic = linkProfilePic
                Jsoup.parse(response).run {
                    val table = select("table")[0].select("tbody")[0]
                    try {
                        val matricula = table.select("tr")[0].select("td")[1]
                        val course = table.select("tr")[1].select("td")[1]
                        val nivel = table.select("tr")[2].select("td")[1]
                        val email = table.select("tr")[4].select("td")[1]
                        val entrada = table.select("tr")[5].select("td")[1]
                        student.course = course.text().toLowerCase().capitalizeWords()
                        student.matricula = matricula.text()
                        student.nivel = nivel.text().toLowerCase().capitalizeWords()
                        student.email = email.text()
                        student.entrada = entrada.text()
                    }
                    catch(e: Exception){
                        e.printStackTrace()
                    }
                }
            }catch(e: IndexOutOfBoundsException){
                e.printStackTrace()
            }
            return classes.toList()
        }
    }

    fun getVinculos(res: String?): List<Vinculo>{
        return Jsoup.parse(res).run {
            val names = select("span[class=col-xs-2]")
            val active = select("span[class=col-xs-1 text-center]")
            val content = select("span[class=col-xs-6]")
            val ids = mutableListOf<Vinculo>()
            select("a[href]").forEach {
                println(it.attr("href"))
                if(it.attr("href").contains("sigaa/escolhaVinculo.do?dispatch=escolher&vinculo=")){
                    val idVinculo = it.attr("href").split("?dispatch=escolher&vinculo=")[1].split("\"")[0]
                    println(idVinculo)
                    ids.add(Vinculo("", "", "", idVinculo))
                }
            }
            for((index, value) in names.withIndex()){
                ids[index].name = value.text()
                ids[index].status = active[index+1].text()
                ids[index].content = content[index+1].text()
            }
            println(ids)
            return@run ids.toList()
        }
    }

    fun parseIraRequestId(response: String?): Pair<String, String> {
        return try {
            val id = response!!.split("<input type=\"hidden\" name=\"id\" value=\"")[1].split("\"")[0]
            println(id)
            val script = response!!.split("de IRA', '")[1].split("'")[0]
            println(script)
            Pair(id, script)
        }catch(e: IndexOutOfBoundsException){
            println(e)
            Pair("", "")
        }
    }

    fun parseIra(response: String?): MutableList<Ira>{
        val iraList = mutableListOf<Ira>()
        Jsoup.parse(response).run {
            val elements = getElementsByClass("item").select("td")
            val ira = Ira("", "", 0.0, 0.0)
            var index = 1
            elements.forEach {
                when(index){
                    1 -> {
                        ira.period = it.text()
                    }
                    2 -> {
                        ira.iraI = it.text().toDouble()
                    }
                    3 -> {
                        ira.iraG = it.text().toDouble()
                    }
                    4 -> {
                        iraList.add(
                            Ira(
                                Random.nextDouble().toString(),
                                ira.period,
                                ira.iraI,
                                ira.iraG
                            )
                        )
                        index = 0
                    }
                }
                index++
            }
        }
        return iraList
    }

    fun parseGradesRequestId(response: String?): String{
        return Jsoup.parse(response).run {
            val script= select("a")[9].attr("onclick")
            return@run script.split("forms['formMenu'],'")[1].split(",")[0]
        }
    }

    fun parseAttendanceRequestId(response: String?): String{
        return Jsoup.parse(response).run {
            val script= select("a")[8].attr("onclick")
            return@run script.split("forms['formMenu'],'")[1].split(",")[0]
        }
    }

    fun parseNewsRequestId(response: String?): String{
        return Jsoup.parse(response).run {
            val script= select("a")[7].attr("onclick")
            return@run script.split("['formMenu'],'")[1].split(",")[0]
        }
    }

    private fun parseDates(text: String): Pair<String, String>{
        return if(text != "" && text.contains("(")){
            val days = text.split(" (")[0]
            val period = text.split(" (")[1].removeSuffix(")")
            Pair(days, period)
        } else{
            Pair(text, "")
        }
    }

    fun parsePreviousClasses(response: String?): MutableList<StudentClass>{
        val classes = mutableListOf<StudentClass>()
        val classItem = StudentClass("", "", true, "", "", "", "", "", "", 0, 0)
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
                                val id = if(classes.size == 0){
                                    ""
                                } else{
                                    "j_id_${classes.size+1}"
                                }
                                classes.add(
                                    StudentClass(
                                        idTurma, id, true, classItem.credits,
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

    fun parseNews(response: String?, idTurma: String): MutableList<News>{
        val news = mutableListOf<News>()
        Jsoup.parse(response).run {
            val tbody = select("tbody")
            val title = tbody.select("td[class=first]")
            val dates = tbody.select("td[class=width75]")
            val onclick = tbody.select("td[class=icon]").select("a")
            var dateIndex = 0
            for((index, it) in title.withIndex()){
                val pair = getNewsId(onclick[index].attr("onclick"))
                news.add(
                    News(pair.first,
                        pair.second,
                        pair.third,
                        idTurma,
                        it.text(),
                        dates[dateIndex].text(),"")
                )
                dateIndex += 2
            }
        }
        return news
    }

    fun parseNewsContent(response: String?): String{
        var content = ""
        val html = response!!.split("<body>")[2].split("</body>")[0]
        Jsoup.parse(html).run {
            val p = select("p")
            for(it in p){
                println(it.text())
                content += " ${it.text()}"
            }
            if(content == ""){
                content = "Erro ao exibir notícia"
            }
        }
        return content
    }

    fun parseFiles(response: String?, idTurma: String): List<File>{
        return Jsoup.parse(response).run {
            val files = mutableListOf<File>()
            val div = select("div[class=item]")
            val span = div.select("span")
            val a = span.select("a")
            val img = a.select("img")
            for((index, it) in a.withIndex()){
                val onclick = it.attr("onclick")
                if(onclick.contains("idInserirMaterialArquivo")){
                    val src = parseFileFormat(img[index].attr("src"))
                    val key = parseKey(onclick)
                    val pair = parseFileId(onclick)
                    var name = span[index].text()
                    if(name.contains(".docx")){
                        name = name.replace(".docx", "")
                        name += ".docx"
                    }
                    else{
                        name = name.replace(".$src", "")
                        name += ".$src"
                    }
                    files.add(File(pair.second, idTurma, name, "${pair.first}/$key"))
                }
            }
            return@run files.toList()
        }
    }

    private fun parseKey(onclick: String): String{
        return onclick.split(",key,")[1].split("','")[0]
    }

    private fun parseFileFormat(res: String): String{
        return res.split("/porta_arquivos/icones/")[1].split(".png")[0]
    }

    private fun parseFileId(js: String): Pair<String, String>{
        val requestId = js.split("['formAva'],'")[1].split(",id,")[0].split(",formAva:")[0]
        val id = js.split(",id,")[1].split(",key")[0]
        return Pair(requestId, id)
    }

    fun parseAttendance(response: String?): Attendance {
        return try {
            val missed = response!!.split("Total de Faltas: ")[1].split("<br/>")[0].toInt()
            val total = response.split("Faltas Permitido: ")[1].split("</div> ")[0].toInt()
            println("missed: $missed")
            println("total: $total")
            Attendance(total, missed)
        }catch(e: IndexOutOfBoundsException){
            Attendance(0, 0)
        }
    }

    fun parseGrades(response: String?, idTurma: String): MutableList<Grade> {
        val grades = mutableListOf<Grade>()
        Jsoup.parse(response).run {
            val th = select("th")
            val td = select("td")
            var index = 0
            th.forEach {
                if(index >= 2){
                    if(td[2].text() == "Imprimir") {
                        grades.add(
                            Grade(
                                "$index$idTurma",
                                idTurma,
                                it.text(),
                                "")
                        )
                    }
                    else {
                        val grade = try {
                            td[index].text()
                        }catch(e: IndexOutOfBoundsException){
                            "Erro ao visualizar nota"
                        }
                        grades.add(
                            Grade(
                                "$index$idTurma",
                                idTurma,
                                it.text(),
                                grade)
                        )
                    }
                }
                index++
            }
        }
        return grades
    }

    private fun getNewsId(script: String): Triple<String, String, String>{
        return try {
            val newsId = script.split(",id,")[1].split("',")[0]
            val requestId1 = script.split(".forms['")[1].split("']")[0]
            val requestId2 = script.split("'],'")[1].split(",")[0]
            Triple(newsId, requestId1, requestId2)
        }catch(e: IndexOutOfBoundsException){
            Triple("", "", "")
        }
    }

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