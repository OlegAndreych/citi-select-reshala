package org.andreych.citi.knapsack.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Объект, представляющий запись строки списка бонусов.
 */
data class Row(val date: LocalDate, val name: String, val cost: Int, val value: Int) {

    companion object {

        private val PATTERN: Regex = Regex("^(?<date>\\d{2}/\\d{2}/\\d{4})(?<name>.*?)(?<cost>[\\d,]*?\\.\\d{2})\\s*?(?<value>[\\d,]*?\\.\\d{2}) \\w{3}\$")
        private val DATE_PATTERN = "dd/MM/yyyy"

        fun parse(line: String): Row {

            val groups = Row.PATTERN.find(line)?.groups ?: throw IllegalArgumentException("Cannot parse line $line.")
            val date = LocalDate.parse(groups["date"]?.value, DateTimeFormatter.ofPattern(DATE_PATTERN))
            val name = groups["name"]!!.value
            val cost = matchToInt(groups, "cost")
            val value = matchToInt(groups, "value")

            return Row(date, name, cost, value)
        }

        private fun matchToInt(groups: MatchGroupCollection, s: String): Int {
            val value = groups[s]!!.value.replace(",", "")
            return BigDecimal(value).movePointRight(2).toInt()
        }
    }
}