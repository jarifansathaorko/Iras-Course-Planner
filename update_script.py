import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = """        // Days: Sun (A), Mon (M), Tue (T), Wed (W), Thu (R), Sat (S)
        val displayDays = listOf(
            Day.SUNDAY,
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.SATURDAY
        )

        val timeColWidth = 140f
        val dayColWidth = (matrixRight - matrixLeft - timeColWidth) / displayDays.size"""

replacement = """        val activeDays = courses.flatMap { com.example.domain.ScheduleHelper.parse(it.timeSlot).days }.toSet()
        val displayDays = if (activeDays.isEmpty()) {
            listOf(Day.SUNDAY, Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY)
        } else {
            listOf(Day.SUNDAY, Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY)
                .filter { it in activeDays }
        }

        val timeColWidth = 140f
        val dayColWidth = (matrixRight - matrixLeft - timeColWidth) / displayDays.size.coerceAtLeast(1)"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)
