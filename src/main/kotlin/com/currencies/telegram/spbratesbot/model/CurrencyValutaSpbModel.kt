package com.currencies.telegram.spbratesbot.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


// Endpoint example:
//   https://valutaspb.ru/module/course/get_list_actual.php
// Response example:
//{"content":{"lastupdate":"14:45","list":{
// "usd":{"abbr":"usd","flag_list":{"buy_from_1":"76.10","buy_from_1000":"76.20","buy_from_10000":"76.35","buy_from_100000":"76.35","sell_from_1":"77.40","sell_from_1000":"77.25","sell_from_10000":"77.25","sell_from_100000":"77.25"}},
// "eur":{..}
// }}}

data class Content(
    var content: Response = Response()
)

data class Response(
    var lastupdate: String = "",
    var list: Map<String, Valuta> = mutableMapOf()
)

data class Valuta(
    var abbr: String? = "",
    var flag_list: List<Flag> = mutableListOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Flag(
    var buy_from_1: String? = null,
    var buy_from_1000: String? = null,
    var buy_from_10000: String? = null,
    var buy_from_100000: String? = null,

    var sell_from_1: String? = null,
    var sell_from_1000: String? = null,
    var sell_from_10000: String? = null,
    var sell_from_100000: String? = null,

    var cross_rate_sell: String? = null,
    var cross_rate_buy: String? = null
)
