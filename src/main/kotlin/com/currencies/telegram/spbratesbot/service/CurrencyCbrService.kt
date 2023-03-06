package com.currencies.telegram.spbratesbot.service

import com.currencies.telegram.spbratesbot.model.ValCursDaily
import com.currencies.telegram.spbratesbot.model.ValCursDynamic
import com.currencies.telegram.spbratesbot.utils.Constants
import org.simpleframework.xml.core.Persister
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Scanner

@Service
class CurrencyCbrService : AbstractService() {

    fun currencyRateDynamic(date: LocalDateTime, idValuta: String): ValCursDynamic {
        val current = date.format(Constants.FORMATTER_SHORT_DAY_TO_YEAR)
        val url =
            URL("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=$current&date_req2=$current&VAL_NM_RQ=$idValuta")
        val scanner = Scanner(url.content as InputStream, "CP1251")
        var xmlString = ""
        while (scanner.hasNext()) {
            xmlString += scanner.nextLine()
        }
        var objects = ValCursDynamic()
        try {
            val serializer = Persister()
            objects = serializer.read(ValCursDynamic::class.java, xmlString)
        } catch (e: IOException) {
            logger.severe("An Exception on parsing XML document: $e")
        }
        return objects
    }

    fun currenciesRateDaily(date: LocalDateTime): ValCursDaily {
        val current = date.format(Constants.FORMATTER_SHORT_DAY_TO_YEAR)
        val url = URL("https://cbr.ru/scripts/XML_daily.asp?date_req=$current")
        val scanner = Scanner(url.content as InputStream, "CP1251")
        var xmlString = ""
        while (scanner.hasNext()) {
            xmlString += scanner.nextLine()
        }
        var objects = ValCursDaily()
        try {
            val serializer = Persister()
            objects = serializer.read(ValCursDaily::class.java, xmlString)
        } catch (e: IOException) {
            logger.severe("An Exception on parsing XML document: $e")
        }
        return objects
    }

    fun currencyRate(date: LocalDateTime, currency: String): String {
        // get today value
        var rate = "not published"
        val todayCurrency = currenciesRateDaily(date)
            .valutes
            .filter { it.CharCode == currency }
        if (todayCurrency.isNotEmpty()) {
            rate = currencyToText(todayCurrency[0].Value, todayCurrency[0].Nominal)
        }

        // get next day value
        var rateTmrw = "not published"
        val today = LocalDate.now().atStartOfDay()
        if (today != date) {
            val tomorrowCurrency = currenciesRateDaily(date.plusDays(1))
                .valutes
                .filter { it.CharCode == currency }
            if (tomorrowCurrency.isNotEmpty()) {
                rateTmrw = currencyToText(tomorrowCurrency[0].Value, tomorrowCurrency[0].Nominal)
            }
        } else {
            val tomorrowCurrency = currencyRateDynamic(date.plusDays(1), todayCurrency[0].ID).records
            if (tomorrowCurrency.isNotEmpty()) {
                rateTmrw = currencyToText(tomorrowCurrency[0].Value, tomorrowCurrency[0].Nominal)
            }
        }
        return "*The CBR determines $currency:*" + "\n" +
                "exchange rate is *$rate*" + "\n" +
                "next date exchange rate is *$rateTmrw*" + "\n"
    }
}