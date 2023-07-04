import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class Freelancer(val name: String, val price: Int)

data class FreelancerData(val freelancers: List<Freelancer>)

fun nbMatchingTeams(teamSize: Int, budget: Int) {
    val gson = Gson()
    val filePath = "src/main/resources/freelancers.json"
    val file = File(filePath)

    if (!file.exists()) {
        println("Error: File not found.")
        return
    }

    val json = file.readText()
    val data: FreelancerData = gson.fromJson(json, object : TypeToken<FreelancerData>() {}.type)

    val freelancers = data.freelancers

    val matchingTeams = mutableListOf<List<Pair<String, Int>>>()
    findMatchingTeams(freelancers, teamSize, budget, mutableListOf(), 0, matchingTeams)

    println("Résultat: ${matchingTeams.size}")
    matchingTeams.forEach { team ->
        val teamString = team.joinToString(separator = "-") { (name, price) ->
            "$name($price)"
        }
        println("$teamString (total: $budget€)")
    }
}

fun findMatchingTeams(
    freelancers: List<Freelancer>,
    teamSize: Int,
    budget: Int,
    currentTeam: MutableList<Pair<String, Int>>,
    startIndex: Int,
    matchingTeams: MutableList<List<Pair<String, Int>>>
) {
    if (currentTeam.size == teamSize) {
        val totalPrice = currentTeam.sumBy { it.second }
        if (totalPrice == budget) {
            matchingTeams.add(currentTeam.toList())
        }
        return
    }

    for (i in startIndex until freelancers.size) {
        val freelancer = freelancers[i]
        if (freelancer.price <= budget) {
            currentTeam.add(freelancer.name to freelancer.price)
            findMatchingTeams(freelancers, teamSize, budget, currentTeam, i + 1, matchingTeams)
            currentTeam.removeAt(currentTeam.size - 1)
        }
    }
}


fun main(args: Array<String>) {
    var teamSize: Int?
    var budget: Int?
    var recalculate: Boolean

    do {
        while (true) {
            print("Enter team size: ")
            teamSize = readLine()?.toIntOrNull()
            if (teamSize != null && teamSize > 0) {
                break
            } else {
                println("Invalid input. Please enter a positive integer for team size.")
            }
        }

        while (true) {
            print("Enter budget: ")
            budget = readLine()?.toIntOrNull()
            if (budget != null && budget > 0) {
                break
            } else {
                println("Invalid input. Please enter a positive integer for budget.")
            }
        }

        nbMatchingTeams(teamSize!!, budget!!)

        print("Do you want to recalculate? (Y/N): ")
        val input = readLine()?.trim()?.toUpperCase()
        recalculate = input == "Y"
    } while (recalculate)
}
