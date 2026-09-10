package com.example.domain

import com.example.data.entity.CourseEntity

enum class Day(val code: Char, val shortName: String, val fullName: String, val order: Int) {
    SUNDAY('A', "Sun", "Sunday", 0),
    MONDAY('M', "Mon", "Monday", 1),
    TUESDAY('T', "Tue", "Tuesday", 2),
    WEDNESDAY('W', "Wed", "Wednesday", 3),
    THURSDAY('R', "Thu", "Thursday", 4),
    FRIDAY('F', "Fri", "Friday", 5),
    SATURDAY('S', "Sat", "Saturday", 6);

    val mask: Int get() = 1 shl order

    companion object {
        private val BY_CODE = entries.associateBy { it.code }
        fun fromChar(c: Char): Day? = BY_CODE[c.uppercaseChar()]
    }
}

data class ParsedSchedule(
    val raw: String,
    val days: Set<Day>,
    val startMinutes: Int, // minutes from midnight
    val endMinutes: Int,   // minutes from midnight
    val dayMask: Int = days.fold(0) { acc, d -> acc or d.mask }
) {
    val durationMinutes: Int get() = (endMinutes - startMinutes).coerceAtLeast(0)

    val formattedTime: String by lazy(LazyThreadSafetyMode.NONE) {
        if (days.isEmpty() || startMinutes >= endMinutes) raw
        else {
            val dayNames = days.sortedBy { it.order }.joinToString(", ") { it.shortName }
            "$dayNames ${ScheduleHelper.formatMinutesTo12Hr(startMinutes)} - ${ScheduleHelper.formatMinutesTo12Hr(endMinutes)}"
        }
    }

    val daysString: String by lazy(LazyThreadSafetyMode.NONE) {
        days.sortedBy { it.order }.joinToString(", ") { it.shortName }
    }

    val timeRange12Hr: String by lazy(LazyThreadSafetyMode.NONE) {
        "${ScheduleHelper.formatMinutesTo12Hr(startMinutes)} - ${ScheduleHelper.formatMinutesTo12Hr(endMinutes)}"
    }
}

sealed class ConflictResult {
    data class SameCourse(val existing: CourseEntity) : ConflictResult() {
        val message: String
            get() = "Already selected: Section ${existing.section} of ${existing.courseCode} is already in your schedule."
    }

    data class TimeOverlap(
        val existing: CourseEntity,
        val overlappingDays: List<Day>,
        val clashTimeDescription: String
    ) : ConflictResult() {
        val message: String
            get() {
                val daysStr = overlappingDays.joinToString(", ") { it.fullName }
                return "Time Conflict on $daysStr with ${existing.courseCode} (Sec ${existing.section}): ${existing.timeSlot} ($clashTimeDescription)"
            }
    }
}

object ScheduleHelper {

    private val scheduleParseCache = java.util.concurrent.ConcurrentHashMap<String, ParsedSchedule>(64)
    private val time12HrCache = java.util.concurrent.ConcurrentHashMap<Int, String>(128)

    fun parse(timeSlot: String): ParsedSchedule {
        return scheduleParseCache.getOrPut(timeSlot) {
            val parts = timeSlot.split(":")
            if (parts.size < 2) {
                ParsedSchedule(timeSlot, emptySet(), 0, 0)
            } else {
                val daysStr = parts[0].trim()
                val timeRange = parts[1].trim()

                val days = mutableSetOf<Day>()
                for (ch in daysStr) {
                    val day = Day.fromChar(ch)
                    if (day != null) {
                        days.add(day)
                    }
                }

                val timeParts = timeRange.split("-")
                if (timeParts.size < 2) {
                    ParsedSchedule(timeSlot, days, 0, 0)
                } else {
                    val startMins = parseHhmmToMinutes(timeParts[0].trim())
                    val endMins = parseHhmmToMinutes(timeParts[1].trim())
                    ParsedSchedule(timeSlot, days, startMins, endMins)
                }
            }
        }
    }

    private fun parseHhmmToMinutes(hhmm: String): Int {
        val clean = hhmm.filter { it.isDigit() }
        if (clean.length < 4) return 0
        val hour = clean.substring(0, 2).toIntOrNull() ?: 0
        val min = clean.substring(2, 4).toIntOrNull() ?: 0
        return hour * 60 + min
    }

    fun formatMinutesTo12Hr(totalMinutes: Int): String {
        return time12HrCache.getOrPut(totalMinutes) {
            val hour24 = totalMinutes / 60
            val min = totalMinutes % 60
            val amPm = if (hour24 >= 12) "PM" else "AM"
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }
            "%d:%02d %s".format(hour12, min, amPm)
        }
    }

    /**
     * Checks if newCourse conflicts with any course in currentSelection.
     * Uses bitmask day comparison for instantaneous O(1) checks.
     * Returns the first ConflictResult, or null if no conflict.
     */
    fun findConflict(
        candidate: CourseEntity,
        currentSelection: List<CourseEntity>
    ): ConflictResult? {
        if (currentSelection.isEmpty()) return null
        val candidateSchedule = parse(candidate.timeSlot)

        for (existing in currentSelection) {
            if (existing.id == candidate.id) continue

            // 1. Same course code conflict check (cannot register for two sections of the same course)
            if (existing.courseCode.equals(candidate.courseCode, ignoreCase = true)) {
                return ConflictResult.SameCourse(existing)
            }

            // 2. Schedule time overlap check using bitmask day comparison
            val existingSchedule = parse(existing.timeSlot)
            val sharedMask = candidateSchedule.dayMask and existingSchedule.dayMask
            if (sharedMask != 0) {
                val startMax = maxOf(candidateSchedule.startMinutes, existingSchedule.startMinutes)
                val endMin = minOf(candidateSchedule.endMinutes, existingSchedule.endMinutes)
                if (startMax < endMin) {
                    val clashDesc = "${formatMinutesTo12Hr(startMax)} - ${formatMinutesTo12Hr(endMin)}"
                    val overlappingDays = candidateSchedule.days.filter { (it.mask and sharedMask) != 0 }.sortedBy { it.order }
                    return ConflictResult.TimeOverlap(
                        existing = existing,
                        overlappingDays = overlappingDays,
                        clashTimeDescription = clashDesc
                    )
                }
            }
        }
        return null
    }

    /**
     * Checks if candidate conflicts with any other course in a list of courses.
     */
    fun hasAnyConflict(
        course: CourseEntity,
        allInSchedule: List<CourseEntity>
    ): Boolean {
        return findConflict(course, allInSchedule) != null
    }
}
