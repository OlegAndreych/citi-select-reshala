package org.andreych.citi.knapsack.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Объект, представляющий запись строки списка бонусов.
 */
data class Row(val date: LocalDate, val name: String, val cost: Int, val value: Int) {

    val ratio = BigDecimal(value).setScale(2) / BigDecimal(cost)

    companion object {

        private val PATTERN: Regex = Regex("^(?<date>\\d{2}/\\d{2}/\\d{4})(?<name>.*?)(?<cost>[\\d,]*?\\.\\d{2})\\s*?(?<value>[\\d,]*?\\.\\d{2}) \\w{3}\$")
        private val DATE_PATTERN = "dd/MM/yyyy"
        private val COST_MULTIPLICATOR = BigDecimal("4.3")

        fun parse(line: String): Row {

            val groups = Row.PATTERN.find(line)?.groups ?: throw IllegalArgumentException("Cannot parse line $line.")
            val date = LocalDate.parse(groups[1]?.value, DateTimeFormatter.ofPattern(DATE_PATTERN))
            val name = groups[2]!!.value
            val value = normalizeValue(groups)
            val cost = calculateCost(groups)

            return Row(date, name, cost, value)
        }

        private fun calculateCost(groups: MatchGroupCollection): Int {
            val value = groups[4]!!.value.replace(",", "")
            return BigDecimal(value).multiply(COST_MULTIPLICATOR).movePointRight(2).toInt()
        }

        private fun normalizeValue(groups: MatchGroupCollection): Int {
            val value = groups[4]!!.value.replace(",", "")
            return BigDecimal(value).movePointRight(2).toInt()
        }
    }
}