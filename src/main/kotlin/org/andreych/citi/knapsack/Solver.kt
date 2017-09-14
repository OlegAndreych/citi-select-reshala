package org.andreych.citi.knapsack

import org.andreych.citi.knapsack.domain.Row
import org.chocosolver.solver.Model
import org.chocosolver.solver.ParallelPortfolio
import org.chocosolver.solver.Solution
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution
import org.chocosolver.solver.variables.IntVar

class Solver(rows: Collection<Row>, limit: Int) {
    private val portfolio = ParallelPortfolio()
    private val modelMap = HashMap<Model, MappingResult>(6)

    init {
        for (i in 0..5) {
            val (model, mappingResult) = makeModel(rows, limit, i)
            portfolio.addModel(model)
            modelMap.put(model, mappingResult)
        }
    }

    private fun makeModel(rows: Collection<Row>, limit: Int, i: Int): Pair<Model, MappingResult> {
        val model = Model("Citi Cashback Pro knapsack model $i")

        val occurrencesList = ArrayList<IntVar>(rows.size)
        val weightSum = model.intVar("Weight sum", 0, limit)
        val energySum = model.intVar("Energy sum", 0, 1_000_000_000)

        val weights = ArrayList<Int>(rows.size)
        val energies = ArrayList<Int>(rows.size)

        for ((date, name, cost, value) in rows) {
            occurrencesList.add(model.boolVar("[$date] [$name] [$value:$cost]"))
            weights.add(cost)
            energies.add(value)
        }

        model.knapsack(occurrencesList.toTypedArray(), weightSum, energySum, weights.toIntArray(), energies.toIntArray()).post()
        model.setObjective(true, energySum)

        val solution = Solution(model)
        model.solver.plugMonitor(IMonitorSolution { solution.record() })

        return Pair(model, MappingResult(occurrencesList, weightSum, energySum, solution))
    }

    fun solve() {

        while (portfolio.solve()) {
        }

        val model = portfolio.bestModel

        val (occurrencesList, weightSum, energySum, solution) = modelMap[model] ?: throw RuntimeException("Somehow model ${model.name} has not been found.")

        println("Cost sum is ${solution.getIntVal(weightSum)}")
        println("Value sum is ${solution.getIntVal(energySum)}")

        occurrencesList.forEach { v ->
            if (solution.getIntVal(v) == 1) println(v.name)
        }
    }
}

private data class MappingResult(val occurrencesList: ArrayList<IntVar>, val weightSum: IntVar, val energySum: IntVar, val solution: Solution)