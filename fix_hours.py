import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

target_hours = """    val startHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(8, 20)
    }
    val endHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 17 else ((parsedCourses.maxOf { it.endMinutes } / 60) + 1).coerceIn(9, 21)
    }"""

replacement_hours = """    val startHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(6, 20)
    }
    val endHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 17 else {
            val maxMins = parsedCourses.maxOf { it.endMinutes }
            ((maxMins + 59) / 60).coerceIn(9, 22)
        }
    }"""

content = content.replace(target_hours, replacement_hours)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

