import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Update parsedCourses logic to cache CourseEntity alongside parsed schedule
content = content.replace(
    "val parsedCourses = remember(courses) { courses.map { ScheduleHelper.parse(it.timeSlot) }.filter { it.startMinutes < it.endMinutes } }",
    "val courseAndParsed = remember(courses) { courses.map { it to ScheduleHelper.parse(it.timeSlot) }.filter { it.second.startMinutes < it.second.endMinutes } }"
)
content = content.replace(
    "val startHour = remember(parsedCourses) {",
    "val startHour = remember(courseAndParsed) {"
)
content = content.replace(
    "if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(6, 20)",
    "if (courseAndParsed.isEmpty()) 8 else (courseAndParsed.minOf { it.second.startMinutes } / 60).coerceIn(6, 20)"
)
content = content.replace(
    "val endHour = remember(parsedCourses) {",
    "val endHour = remember(courseAndParsed) {"
)
content = content.replace(
    "if (parsedCourses.isEmpty()) 17 else {",
    "if (courseAndParsed.isEmpty()) 17 else {"
)
content = content.replace(
    "val maxMins = parsedCourses.maxOf { it.endMinutes }",
    "val maxMins = courseAndParsed.maxOf { it.second.endMinutes }"
)
content = content.replace(
    "val gaps = remember(parsedCourses) {",
    "val gaps = remember(courseAndParsed) {"
)
content = content.replace(
    "val intervals = parsedCourses.map { it.startMinutes to it.endMinutes }.sortedBy { it.first }",
    "val intervals = courseAndParsed.map { it.second.startMinutes to it.second.endMinutes }.sortedBy { it.first }"
)

# 2. Fix the loop to use courseAndParsed instead of mapping courses on every render
old_loop = """                        // Render Course Class Blocks on Top
                        for ((cIndex, course) in courses.withIndex()) {
                            val parsed = ScheduleHelper.parse(course.timeSlot)
                            if (parsed.days.isEmpty() || parsed.startMinutes >= parsed.endMinutes) continue"""

new_loop = """                        // Render Course Class Blocks on Top
                        for ((cIndex, pair) in courseAndParsed.withIndex()) {
                            val course = pair.first
                            val parsed = pair.second"""
content = content.replace(old_loop, new_loop)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
