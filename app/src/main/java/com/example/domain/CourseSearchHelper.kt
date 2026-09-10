package com.example.domain

import com.example.data.entity.CourseEntity

object CourseSearchHelper {

    private val EXPLICIT_SEC_REGEX = Regex("""^(.*?)\s*(?:sec|section)\.?\s*(\d{1,2})$""", RegexOption.IGNORE_CASE)
    private val DELIMITED_SEC_REGEX = Regex("""^(.*?)(?:\s+|[\-_/:\.,]+)(\d{1,2})$""")
    private val TRAILING_SEC_REGEX = Regex("""\s*(?:sec|section)\.?\s*$""", RegexOption.IGNORE_CASE)

    /**
     * Filters courses according to university course selection requirements:
     * - If searched via course code (e.g. "CSE", "CSE1", "CSE10", "CSE100", "CSE 100", "CSE201"),
     *   returns all sections and their details.
     * - If searched via course code and section (e.g. "CSE100 2", "CSE100 sec 2", "CSE 100 2", "CSE201 4"),
     *   returns only that specific section details.
     * - Also supports filtering by title, faculty, and department.
     */
    fun filterCourses(
        courses: List<CourseEntity>,
        query: String,
        dept: String = "ALL",
        dropdownCode: String? = null,
        dropdownSec: Int? = null
    ): List<CourseEntity> {
        val hasDeptFilter = dept != "ALL"

        // Dropdown filter if used
        if (!dropdownCode.isNullOrBlank()) {
            return courses.filter { course ->
                (!hasDeptFilter || course.courseCode.startsWith(dept, ignoreCase = true)) &&
                        course.courseCode.equals(dropdownCode, ignoreCase = true) &&
                        (dropdownSec == null || course.section == dropdownSec)
            }
        }

        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return if (hasDeptFilter) {
                courses.filter { it.courseCode.startsWith(dept, ignoreCase = true) }
            } else {
                courses
            }
        }

        // Check if query specifies course code AND section
        // 1. Explicit keyword: "CSE100 sec 2", "CSE100 section 2", "CSE 100 sec 2"
        val explicitMatch = EXPLICIT_SEC_REGEX.find(trimmed)
        val delimitedMatch = if (explicitMatch == null) DELIMITED_SEC_REGEX.find(trimmed) else null

        val (matchedCode, matchedSection) = when {
            explicitMatch != null -> {
                val rawCode = explicitMatch.groupValues[1].replace(" ", "").uppercase()
                val sec = explicitMatch.groupValues[2].toIntOrNull()
                Pair(rawCode, sec)
            }
            delimitedMatch != null -> {
                val rawCode = delimitedMatch.groupValues[1].replace(" ", "").uppercase()
                val sec = delimitedMatch.groupValues[2].toIntOrNull()
                if (rawCode.any { it.isDigit() } && sec != null && sec in 1..99) {
                    Pair(rawCode, sec)
                } else {
                    Pair(null, null)
                }
            }
            else -> Pair(null, null)
        }

        return if (matchedCode != null && matchedSection != null) {
            courses.filter { course ->
                (!hasDeptFilter || course.courseCode.startsWith(dept, ignoreCase = true)) &&
                        course.courseCode.equals(matchedCode, ignoreCase = true) &&
                        course.section == matchedSection
            }
        } else {
            val sanitizedQuery = trimmed.replace(TRAILING_SEC_REGEX, "")
            val queryWithoutSpaces = sanitizedQuery.replace(" ", "")

            val filtered = courses.filter { course ->
                if (hasDeptFilter && !course.courseCode.startsWith(dept, ignoreCase = true)) {
                    false
                } else {
                    val codeWithoutSpaces = course.courseCode.replace(" ", "")
                    codeWithoutSpaces.contains(queryWithoutSpaces, ignoreCase = true) ||
                            course.courseCode.contains(sanitizedQuery, ignoreCase = true) ||
                            course.title.contains(trimmed, ignoreCase = true) ||
                            course.faculty.contains(trimmed, ignoreCase = true)
                }
            }

            filtered.sortedWith(
                compareBy<CourseEntity> {
                    val codeNoSpace = it.courseCode.replace(" ", "")
                    when {
                        codeNoSpace.equals(queryWithoutSpaces, ignoreCase = true) -> 0
                        codeNoSpace.startsWith(queryWithoutSpaces, ignoreCase = true) -> 1
                        else -> 2
                    }
                }.thenBy { it.courseCode }
                    .thenBy { it.section }
            )
        }
    }
}
