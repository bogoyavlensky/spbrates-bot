package com.currencies.telegram.spbratesbot.service

import com.currencies.telegram.spbratesbot.utils.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.logging.Logger

@Service
class TelegramBotService : TelegramLongPollingBot() {

    @Autowired
    private lateinit var currencyCbrService: CurrencyCbrService

    @Autowired
    private lateinit var valutaSPbService: ValutaSPbService

    private val logger = Logger.getLogger(this.javaClass.name)

    @Value("\${bot.name}")
    private val botName: String = ""

    @Value("\${bot.token}")
    private val botToken: String = ""

    @Value("\${logger.enabled}")
    private val loggerEnabled: String = "false"

    override fun getBotUsername(): String = botName

    override fun getBotToken(): String = botToken

    override fun onUpdateReceived(update: Update) {
        var expectedDate = LocalDate.now().atStartOfDay()
        var message = ""
        if (update.hasMessage() && update.message.hasText()) {
            // log action
            if (loggerEnabled == "true") {
                logger.info("User ${update.message.chat.userName} (${update.message.chat.firstName} ${update.message.chat.lastName}) asked '${update.message.text}'")
            }

            // split command and arguments
            val commandArgs = update.message.text.split(" ")
            val command = commandArgs[0]
            val expectedCurrency = commandArgs[0].toUpperCase().drop(1)
            if (commandArgs.size == 2) {
                try {
                    expectedDate = LocalDate.parse(commandArgs[1], Constants.FORMATTER_SHORT_YEAR_TO_DAY).atStartOfDay()
                } catch (ex: DateTimeParseException) {
                    message = "Incorrect date format, please check /help"
                }
            }
            val currencyCodes = updateActualCurrencies(expectedDate)
            when {
                message != "" -> {}

                command == "/start" -> {
                    message = "Hi, ${update.message.chat.userName}, nice to meet you!" + "\n" +
                            "Enter the currency whose official exchange rate you want to know." + "\n" +
                            "For example: /USD. Full currencies list on /currencylist"
                }

                command == "/help" -> {
                    message = "Commands list:" + "\n" +
                            "/help = this guide" + "\n" +
                            "/currencylist = available currensies" + "\n" +
                            "/XXX = returns currency rate" + "\n" +
                            "/XXX yyyy.mm.dd = returns only CBR rate" + "\n\n" +
                            "Information:" + "\n" +
                            "CBR published official rates on https://www.cbr.ru/currency_base/daily/" + "\n" +
                            "Note: next date rate published approximately at 14-00 (Msk timezone)" + "\n" +
                            "ValutaSPb is one of exchanges at Saint-Petersburg www.valutaspb.ru" + "\n"
                }

                command == "/currencylist" -> {
                    message = "Popular currencies: " + "\n" +
                            "/USD /EUR /CNY" + "\n" + "\n" +
                            "Available currencies:"
                    var counter = 0
                    currencyCodes.forEach {
                        if (counter % 5 == 0) message += "\n"
                        message += "/" + it + " "
                        counter += 1
                    }
                }

                (expectedCurrency in currencyCodes) -> {
                    val today = LocalDate.now().atStartOfDay()
                    if (expectedDate != today) {
                        message = "For date ${expectedDate.format(Constants.FORMATTER_SHORT_YEAR_TO_DAY)}" + "\n" + "\n"
                    }
                    message += currencyCbrService.currencyRate(expectedDate, expectedCurrency)
                    if (expectedDate == today) {
                        message += "\n" + valutaSPbService.currencyRate(expectedCurrency)
                    }
                }

                else -> {
                    message = "Have not found such a currency or command." + "\n" + "Please, check /help"
                }
            }
            sendMessage(update.message.chatId!!, message)
        }
    }

    private fun updateActualCurrencies(date: LocalDateTime): List<String> {
        val cbrCodes = currencyCbrService.currenciesRateDaily(date)
            .valutes
            .map { it.CharCode }
        val valutaSpbCodes = valutaSPbService.currencies()
        return (cbrCodes + valutaSpbCodes).distinct().sorted()
    }

    private fun sendMessage(chatId: Long, textToSend: String) {
        val sendMessage = SendMessage()
            .apply {
                this.chatId = chatId.toString()
                this.text = textToSend
                this.parseMode = "markdown"
            }
        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            throw RuntimeException("Something wrong with telegram bot ...")
        }
    }
}