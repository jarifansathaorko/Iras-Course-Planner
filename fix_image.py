import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target1 = """        // Time limits: 08:00 (480 mins) to 21:30 (1290 mins)
        val startDayMinutes = 8 * 60      // 480
        val endDayMinutes = 21 * 60 + 30  // 1290"""

replacement1 = """        val parsedCourses = courses.map { com.example.domain.ScheduleHelper.parse(it.timeSlot) }.filter { it.startMinutes < it.endMinutes }
        val startHour = if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(8, 20)
        val endHour = if (parsedCourses.isEmpty()) 17 else ((parsedCourses.maxOf { it.endMinutes } / 60) + 1).coerceIn(9, 21)

        val startDayMinutes = startHour * 60
        val endDayMinutes = endHour * 60"""

content = content.replace(target1, replacement1)

target2 = """            for (hour in 8..21) {"""
replacement2 = """            for (hour in startHour..endHour) {"""

content = content.replace(target2, replacement2)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

