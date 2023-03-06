package com.currencies.telegram.spbratesbot.utils

import java.time.format.DateTimeFormatter

object Constants {
    val FORMATTER_SHORT_DAY_TO_YEAR = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val FORMATTER_SHORT_YEAR_TO_DAY = DateTimeFormatter.ofPattern("yyyy.MM.dd")
}