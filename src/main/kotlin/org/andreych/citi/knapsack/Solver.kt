package org.andreych.citi.knapsack

import org.andreych.citi.knapsack.domain.Row
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution
import org.chocosolver.solver.variables.IntVar

class Solver(rows: Map<Pair<Int, Int>, List<Row>>, limit: Int) {
    private val modelMap = HashMap<Model, MappingResult>(6)

    init {
        val (model, mappingResult) = makeModel(rows, limit)
        modelMap.put(model, mappingResult)
    }

    private fun makeModel(groupedRows: Map<Pair<Int, Int>, List<Row>>, limit: Int): Pair<Model, MappingResult> {
        val model = Model("Citi Cashback Pro knapsack model")

        val occurrencesList = ArrayList<IntVar>(groupedRows.size)
        val weights = ArrayList<Int>(groupedRows.size)
        val energies = ArrayList<Int>(groupedRows.size)

        val weightSum = model.intVar("Weight sum", 0, limit)
        val energySum = model.intVar("Energy sum", 0, 1_000_000_000)

        for ((_, rows) in groupedRows) {
            val (_, _, cost, value) = rows.first()
            occurrencesList.add(model.intVar("[$value:$cost]", 0, rows.size))
            weights.add(cost)
            energies.add(value)
        }

        val solution = Solution(model)
        model.solver.plugMonitor(IMonitorSolution { solution.record() })

        model.knapsack(occurrencesList.toTypedArray(), weightSum, energySum, weights.toIntArray(), energies.toIntArray()).post()
        model.setObjective(true, energySum)

        return Pair(model, MappingResult(occurrencesList, weightSum, energySum, solution))
    }

    fun solve() {

        val (model, mappingResult) = modelMap.entries.first()

        val solver = model.solver
        while (solver.solve()) {
        }

        val (occurrencesList, weightSum, energySum, solution) = mappingResult

        println("Cost sum is ${solution.getIntVal(weightSum)}")
        println("Value sum is ${solution.getIntVal(energySum)}")

        occurrencesList.forEach { v ->
            val amount = solution.getIntVal(v)
            if (amount > 0) println("Recrord: ${v.name}, amount: $amount")
        }
    }
}

private data class MappingResult(val occurrencesList: ArrayList<IntVar>, val weightSum: IntVar, val energySum: IntVar, val solution: Solution)