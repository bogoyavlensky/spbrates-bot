package com.currencies.telegram.spbratesbot.service

import com.currencies.telegram.spbratesbot.model.Content
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.Scanner

@Service
class ValutaSPbService : AbstractService() {

    private fun parseDataToObjects(): Content {
        val url = URL("https://valutaspb.ru/module/course/get_list_actual.php")
        val scanner = Scanner(url.content as InputStream, "CP1251")
        var jsonString = ""
        while (scanner.hasNext()) {
            jsonString += scanner.nextLine()
        }
        var objects = Content()
        try {
            val mapper = jacksonObjectMapper()
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            objects = mapper.readValue(jsonString)
        } catch (e: IOException) {
            logger.severe("An Exception on parsing JSON document: $e")
        }
        return objects
    }

    fun currencies() =
        parseDataToObjects()
            .content.list
            .filter { it.key != "" }
            .map { it.value.abbr!!.toUpperCase() }
            .toList()
            .sorted()

    fun currencyRate(valutaCode: String): String {
        var messageData = "Unknown rates, please try again later."

        // manual fix for nominals
        val nominal = when (valutaCode) {
            "IDR", "UZS", "VND" -> 10000
            "KRW" -> 1000
            "AMD", "HUF", "JPY", "KGS", "KZT", "RSD" -> 100
            "CNY", "DKK", "CZK", "EGP", "HKD", "MDL", "NOK", "SEK", "THB", "TJS", "UAH", "ZAR" -> 10
            else -> 1
        }
        val data = parseDataToObjects().content
        data.list
            .filter { it.key != "" }
            .filter { it.value.abbr!!.toUpperCase() == valutaCode }
            .forEach {
                val valuta = it.value.flag_list
                if (valuta.isNotEmpty())
                    messageData = "Nominal 0-199: *${currencyToText(valuta[0].sell_from_1, "$nominal")}*\n" +
                            "Nominal 200-999: *${currencyToText(valuta[0].sell_from_1000, "$nominal")}*\n" +
                            "Nominal 1000-9999: *${currencyToText(valuta[0].sell_from_10000, "$nominal")}*\n" +
                            "Nominal from 10000: *${currencyToText(valuta[0].sell_from_100000, "$nominal")}*\n" +
                            "Last update: ${data.lastupdate} (Msk timezone)"
            }
        return "*ValutaSPb exchange rates for $valutaCode (sale):*\n$messageData"
    }
}