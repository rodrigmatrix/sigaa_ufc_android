package com.rodrigmatrix.sigaaufc.serializer

import com.rodrigmatrix.sigaaufc.persistence.Classes
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

    fun parseRU(response: String?){
        var creditos = response?.split("<td nowrap=\"nowrap\">Créditos:</td>\n" + "\t\t\t\t\t\t<td nowrap=\"nowrap\">")
        creditos = creditos!![1].split("</td>")
        println(creditos)
    }
}