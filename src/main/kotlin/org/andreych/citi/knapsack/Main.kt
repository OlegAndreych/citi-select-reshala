package org.andreych.citi.knapsack

import org.andreych.citi.knapsack.domain.Row
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val path = Paths.get(args[0])

    println("Parsing limit")
    val limit = Files.newBufferedReader(path).useLines {
        val first: String = it.first()
        BigDecimal(first.replace(",", "")).movePointRight(2).toInt()
    }
    println("Limit is $limit")

    println("Parsing rows")
    val rows = Files.newBufferedReader(path).useLines { it.drop(1).map { Row.parse(it) }.toList() }
    println("Rows has been parsed")

    println("Solving started")
    val time = measureTimeMillis {
        val solver = Solver(rows, limit)
        solver.solve()
    }
    println("Solution has been found in $time millis")
}