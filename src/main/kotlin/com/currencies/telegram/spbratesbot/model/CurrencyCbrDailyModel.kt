package com.currencies.telegram.spbratesbot.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

// Endpoint example:
//   https://cbr.ru/scripts/XML_daily.asp?date_req=26/02/2023
// Response example:
//   <ValCurs Date="23.02.2023" name="Foreign Currency Market">
//      <Valute ID="R01010">
//         <NumCode>036</NumCode>
//         <CharCode>AUD</CharCode>
//         <Nominal>1</Nominal>
//         <Name>Австралийский доллар</Name>
//         <Value>51,0933</Value>
//      </Valute>
//      <Valute ID="R01020A">
//         <NumCode>944</NumCode>
//         <CharCode>AZN</CharCode>
//         <Nominal>1</Nominal>
//         <Name>Азербайджанский манат</Name>
//         <Value>43,9463</Value>
//      </Valute>
//      ...
//   </ValCurs>

@Root(name="Valute")
data class ValuteDaily(
        @field:Attribute
        var ID: String = "",

        @field:Element
        var NumCode: String = "",
        @field:Element
        var CharCode: String = "",
        @field:Element
        var Nominal: String = "",
        @field:Element
        var Name: String = "",
        @field:Element
        var Value: String = ""
)

@Root(name="ValCurs")
data class ValCursDaily(
        @field:Attribute
        var Date: String = "",
        @field:Attribute
        var name: String = "",

        @field:ElementList(entry="Valute", inline=true, required=false)
        var valutes: List<ValuteDaily> = mutableListOf()
)