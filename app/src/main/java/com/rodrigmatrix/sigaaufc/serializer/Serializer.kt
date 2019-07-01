package com.rodrigmatrix.sigaaufc.serializer

import com.rodrigmatrix.sigaaufc.persistence.Classes
import com.rodrigmatrix.sigaaufc.persistence.HistoryRU
import org.jsoup.Jsoup

class Serializer {

    fun loginParse(response: String?): String{
        return when {
            response!!.contains("value=\"Continuar") -> "Continuar"
            response.contains("Menu Principal") -> "Menu Principal"
            response.contains("Usuário e/ou senha inválidos") -> "Aluno não encontrado"
            else -> "Erro Login"
        }
    }

    fun parseClasses(response: String?){
        var classes = mutableListOf<Classes>()
        var turmaId = mutableListOf<String>()
        var names = mutableListOf<String>()
        var periodsList = mutableListOf<String>()
        Jsoup.parse(response).run {
            select("input[value]").forEach {name ->
                if(name.attr("name").contains("idTurma")){
                    turmaId.add(name.attr("value"))
                    //println(name.attr("value"))
                }
            }
            var elements = getElementsByClass("descricao")
            var periods = getElementsByClass("info")


            elements.forEach {
                var el = it.select("a[id]")
                el.forEach { name ->
                    //println(name.text())
                    names.add(name.text())
                }
            }
            var count = 1
            periods.forEach { period ->
                if(count % 2 == 0){
                    periodsList.add(period.text())
                }
                count++
            }
            var id = 1
            for(it in turmaId){
                classes.add(Classes(id, turmaId[id-1].toInt(), names[id-1], periodsList[id-1], 0, 0))
                println(classes[id-1])
                id++
            }
            var studentName = select("div[class=nome_usuario]")
            println(studentName.text())
            var matricula = response?.split("<td> Matr&#237;cula: </td>\n" + "\t\t\t\t\t\t<td> ")
            matricula = matricula!![1].split(" </td>")
            println(matricula[0])
            var curso = response?.split("<td> Curso: </td>\n" + "\t\t\t\t\t\t<td> ")
            curso = curso?.get(1)!!.split(" </td>")
            println(curso[0])
        }
    }

    fun parseRU(response: String?): Triple<String, Pair<String, Int>, MutableList<HistoryRU>>{
        when {
            response!!.contains("O campo 'Matrícula atrelada ao cartão' é de preenchimento obrigatório.") -> {
                return Triple("Matrícula não encontrada", Pair("", 0), mutableListOf())
            }
            response!!.contains("Não existem dados a serem exibidos") -> {
                return Triple("Não existem dados a serem exibidos", Pair("", 0), mutableListOf())
            }
            response!!.contains("Refeições disponíveis") -> {
                var history = mutableListOf<HistoryRU>()
                var elem = HistoryRU(1, "", "", "", "")
                Jsoup.parse(response).run {
                    var operations = select("td[nowrap=nowrap]")
                    var name = operations[1].text()
                    var credits = operations[3].text().toInt()
                    var count = 1
                    for((index, it) in operations.withIndex()){
                        if(index >= 4){
                            when(count){
                                1 -> {
                                    elem.id = index
                                    elem.date = it.text().removeRange(10, it.text().length)
                                    elem.time = it.text().removeRange(0, 10)
                                    count++
                                }
                                2 -> {
                                    elem.operation = it.text()
                                    count++
                                }
                                3 -> {
                                    elem.content = it.text()
                                    history.add(HistoryRU(index, elem.date, elem.time, elem.operation, elem.content))
                                    count = 1
                                }
                            }
                        }
                    }
                    return Triple("Success", Pair(name, credits), history)
                }
            }
        }
        return Triple("Error", Pair("", 1), mutableListOf())
    }
}