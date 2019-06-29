package com.rodrigmatrix.sigaaufc.serializer

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
        Jsoup.parse(response).run {
            select("input[value]").forEach {
                if(it.attr("name").contains("idTurma")){
                    println(it.attr("value"))
                }
            }
            var elements = getElementsByClass("descricao")
            var periods = getElementsByClass("info")
            var count = 1
            elements.forEach {
                var names = it.select("a[id]")
                names.forEach { name ->
                    println(name.text())
                }
            }
            periods.forEach { period ->
                if(count % 2 == 0){
                    println(period.text())
                }
                count++
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