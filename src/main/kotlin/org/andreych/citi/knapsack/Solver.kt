package org.andreych.citi.knapsack

import org.andreych.citi.knapsack.domain.Row
import org.chocosolver.solver.Model
import org.chocosolver.solver.ParallelPortfolio
import org.chocosolver.solver.Solution
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution
import org.chocosolver.solver.variables.IntVar

class Solver(rows: Map<Pair<Int, Int>, List<Row>>, limit: Int) {

    companion object {
        const val MODELS_AMOUNT = 8
    }

    private val modelMap = HashMap<Model, MappingResult>(8)

    init {

        val (model, mappingResult) = makeModel(rows, limit)
        for (i in 1..MODELS_AMOUNT){
            modelMap[model] = mappingResult
        }
    }

    private fun makeModel(groupedRows: Map<Pair<Int, Int>, List<Row>>, limit: Int): Pair<Model, MappingResult> {
        val model = Model("Citi Cashback Pro knapsack model")

        val occurrencesList = ArrayList<IntVar>(groupedRows.size)
        val weights = ArrayList<Int>(groupedRows.size)
        val energies = ArrayList<Int>(groupedRows.size)

        val weightSum = model.intVar("Weight sum", 0, limit)
        val energySum = model.intVar("Energy sum", 0, 1_000_000)

        for ((_, rows) in groupedRows) {
            val (_, _, cost, value) = rows.first()
            occurrencesList.add(model.intVar("[$value:$cost]", 0, rows.size))
            weights.add(cost)
            energies.add(value)
        }

        val weightsArray = weights.toIntArray()
        val energiesArray = energies.toIntArray()

        val solution = Solution(model)
        val solver = model.solver
        solver.plugMonitor(IMonitorSolution { solution.record() })
        solver.setDBTLearning(true, false)
        solver.limitTime(60000L)

        model.knapsack(occurrencesList.toTypedArray(), weightSum, energySum, weightsArray, energiesArray).post()
        model.setObjective(true, energySum)

        return Pair(model, MappingResult(occurrencesList, weightSum, energySum, solution))
    }

    fun solve() {

        val portfolio = ParallelPortfolio()
        modelMap.entries.forEach { portfolio.addModel(it.key) }

        while (portfolio.solve()) {
            val bestModel = portfolio.bestModel
            val solver = bestModel.solver
            solver.printStatistics()
            val mappingResult = modelMap[bestModel]
            mappingResult?.let { printResult(it) }
        }
    }

    private fun printResult(mappingResult: MappingResult) {
        val (occurrencesList, weightSum, energySum, solution) = mappingResult

        println("Cost sum is ${solution.getIntVal(weightSum)}")
        println("Value sum is ${solution.getIntVal(energySum)}")

        occurrencesList.forEach { v ->
            val amount = solution.getIntVal(v)
            if (amount > 0) println("Record: ${v.name}, amount: $amount")
        }
    }
}

private data class MappingResult(
    val occurrencesList: ArrayList<IntVar>,
    val weightSum: IntVar,
    val energySum: IntVar,
    val solution: Solution
)