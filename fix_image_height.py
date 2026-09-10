import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target_height = """    ): Bitmap {
        val width = 1440
        val baseHeaderHeight = 320
        val gridHeight = 780"""

replacement_height = """    ): Bitmap {
        val width = 1440
        val baseHeaderHeight = 320
        
        val parsedCourses = courses.map { com.example.domain.ScheduleHelper.parse(it.timeSlot) }.filter { it.startMinutes < it.endMinutes }
        val tmpStartHour = if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(6, 20)
        val tmpEndHour = if (parsedCourses.isEmpty()) 17 else {
            val maxMins = parsedCourses.maxOf { it.endMinutes }
            ((maxMins + 59) / 60).coerceIn(9, 22)
        }
        val gridHeight = 120 + ((tmpEndHour - tmpStartHour) * 110)
"""

content = content.replace(target_height, replacement_height)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

