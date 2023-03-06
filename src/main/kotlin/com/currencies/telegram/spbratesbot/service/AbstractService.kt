package com.currencies.telegram.spbratesbot.service

import java.util.logging.Logger

abstract class AbstractService {

    val logger = Logger.getLogger(this.javaClass.name)

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    fun currencyToText(value: String?, nominal: String) =
        when {
            value == null -> "not published"
            nominal.isEmpty() -> "nominal is undefined"
            else -> {
                var valueStr = (value.replace(",", ".").toDouble() / nominal.toInt()).format(6)
                while (valueStr.last() == '0') {
                    valueStr = valueStr.dropLast(1)
                }
                if (valueStr.last() == ',') valueStr += "0"
                valueStr
            }
        }
}
