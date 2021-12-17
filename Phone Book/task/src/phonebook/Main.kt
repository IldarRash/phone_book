package phonebook

import java.io.File
import java.util.stream.Collectors.toMap
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.reflect.KFunction2

fun main() {
    val filePath = "/Users/irshaya5/Downloads/directory.txt"
    val findPath = "/Users/irshaya5/Downloads/find.txt"

    // Данные для сортировок

    val fileList = File(filePath)
        .readLines()
        .map { it.split(" ") }
        .map { Number(it[0], it.subList(1, it.size).joinToString(" ")) }

    val find = File(findPath)
        .readLines()

    val allLinesToFind = find.size


    // Линейный поиск

    println("Start searching (linear search)...")

    val findLinear = getTimeSearch(::linearSearch, fileList, find)
    println(
        "Found ${findLinear.first} / $allLinesToFind entries. Time taken:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                findLinear.second
            )
        }"
    )

    // Сортировка пузырьком и интервальный поиск

    println("Start searching (bubble sort + jump search)...")

    val startJumpSearch = System.currentTimeMillis()
    val resultBubble = getTimeSort(::bubbleSort, fileList)

    val resultSearch = getTimeSearch(::searchArray, resultBubble.first, find)

    println(
        "Found ${resultSearch.first} / $allLinesToFind entries. Time taken:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                System.currentTimeMillis() - startJumpSearch
            )
        }"
    )

    println(
        "Sorting time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultBubble.second
            )
        }"
    )

    println(
        "Searching time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultSearch.second
            )
        }"
    )


    // Быстрая сортировка и бинарный поиск
    println("Start searching (quick sort + binary search)...")

    val startQuickSearch = System.currentTimeMillis()
    val resultQuick = getTimeSort(::quicksort, fileList)

    val resultBinary = getTimeSearch(::binarySearch, resultQuick.first, find)


    println(
        "Found ${resultBinary.first} / $allLinesToFind entries. Time taken:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                System.currentTimeMillis() - startQuickSearch
            )
        }"
    )

    println(
        "Sorting time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultQuick.second
            )
        }"
    )

    println(
        "Searching time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultBinary.second
            )
        }"
    )

    // Быстрая сортировка и бинарный поиск
    println("\nStart searching (hash table)...")


    val startHashTable = System.currentTimeMillis()
    val resultCreateHash = getTimeSort(::createHashTable, fileList)

    val resultHash = searchHashMap(resultCreateHash.first, find)

    println(
        "Found ${resultBinary.first} / $allLinesToFind entries. Time taken:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                System.currentTimeMillis() - startHashTable
            )
        }"
    )

    println(
        "Creating time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultCreateHash.second
            )
        }"
    )

    println(
        "Searching time:${
            String.format(
                "%1\$tM min. %1\$tS sec. %1\$tL ms.",
                resultHash.second
            )
        }"
    )

}

fun <T> getTimeSort(func: (list: List<Number>) -> T, list: List<Number>): Pair<T, Long> {
    val startTime = System.currentTimeMillis()
    val result = func.invoke(list)
    val endTime = System.currentTimeMillis()
    return Pair(result, endTime - startTime)
}

fun getTimeSearch(
    func: KFunction2<List<Number>, List<String>, Int>,
    all: List<Number>,
    findList: List<String>
): Pair<Int, Long> {
    val startTime = System.currentTimeMillis()
    val result = func.invoke(all, findList)
    val endTime = System.currentTimeMillis()
    return Pair(result, endTime - startTime)
}

data class Number(val number: String, val name: String)

fun linearSearch(allList: List<Number>, findList: List<String>): Int {
    val count = findList.map {
        allList.map { elem -> elem.name == it }.first { pr -> pr }
    }
        .filter { it }
        .count()

    return count
}

fun bubbleSort(allList: List<Number>): List<Number> {
    val arr = allList.toTypedArray()
    var swap = true
    while (swap) {
        swap = false
        for (i in 0 until arr.size - 1) {
            if (arr[i].name >= arr[i + 1].name) {
                val temp = arr[i]
                arr[i] = arr[i + 1]
                arr[i + 1] = temp

                swap = true
            }
        }
    }
    return arr.asList()
}

fun searchArray(allList: List<Number>, findList: List<String>): Int {
    val count = findList.map {
        val result = searchJump(allList.toTypedArray(), it)
        return@map result != -1
    }
        .filter { elem -> elem }
        .count()

    return count
}

fun searchJump(arr: Array<Number>, x: String): Int {
    val n = arr.size

    var step = floor(sqrt(n.toDouble())).toInt()
    var prev = 0
    while (arr[min(step, n) - 1].name < x) {
        prev = step
        step += floor(sqrt(n.toDouble())).toInt()
        if (prev >= n) return -1
    }

    while (arr[prev].name < x) {
        prev++
        if (prev == min(step, n)) return -1
    }

    return if (arr[prev].name == x) prev else -1
}

fun quicksort(items: List<Number>): List<Number> {
    if (items.count() < 2) {
        return items
    }
    val pivot = items[items.count() / 2].name

    val equal = items.filter { it.name == pivot }

    val less = items.filter { it.name < pivot }

    val greater = items.filter { it.name > pivot }

    return quicksort(less) + equal + quicksort(greater)
}

fun binarySearch(allList: List<Number>, findList: List<String>): Int {
    fun binary(input: List<Number>, elem: String, low: Int, high: Int): Int {
        while (low <= high) {
            val mid = (low + high) / 2
            when {
                elem > input[mid].name -> return binary(input, elem, mid + 1, high)
                elem < input[mid].name -> return binary(input, elem, low, mid-1)
                elem == input[mid].name -> return mid // element found.
            }
        }
        return -1
    }

    val count = findList.map {
        val result = binary(allList, it, 0, allList.size - 1)
        return@map result != -1
    }
    .filter { elem -> elem }
    .count()

    return count
}

fun createHashTable(allList: List<Number>) =
    allList.map { it.name to it }.toMap()

fun searchHashMap(allList: Map<String, Number>, find: List<String>): Pair<Int, Long> {
    val startTime = System.currentTimeMillis()
    val result = find.map {
        return@map allList[it] != null
    }
    .filter { elem -> elem }
    .count()

    val endTime = System.currentTimeMillis()
    return Pair(result, endTime - startTime)
}
