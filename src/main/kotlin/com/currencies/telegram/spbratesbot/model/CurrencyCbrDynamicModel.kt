package com.currencies.telegram.spbratesbot.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

// Endpoint example:
//   https://cbr.ru/scripts/XML_dynamic.asp?date_req1=22/02/2023&date_req2=22/02/2023&VAL_NM_RQ=R01235
// Response example:
//   <ValCurs ID="R01235" DateRange1="22.02.2023" DateRange2="22.02.2023" name="Foreign Currency Market Dynamic">
//      <Record Date="22.02.2023" Id="R01235">
//         <Nominal>1</Nominal>
//         <Value>74,8596</Value>
//      </Record>
//   </ValCurs>

@Root(name="ValCurs")
data class ValCursDynamic(
        @field:Attribute
        var ID: String = "",
        @field:Attribute
        var DateRange1: String = "",
        @field:Attribute
        var DateRange2: String = "",
        @field:Attribute
        var name: String = "",

        @field:ElementList(entry="Record", inline=true, required=false)
        var records: List<RecordDynamic> = mutableListOf()
)

@Root(name="Record")
data class RecordDynamic(
        @field:Attribute
        var Date: String = "",
        @field:Attribute
        var Id: String = "",

        @field:Element
        var Nominal: String = "",
        @field:Element
        var Value: String = ""
)