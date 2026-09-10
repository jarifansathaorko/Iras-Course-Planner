import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Update the time boundary calculation
target1 = """    // Time boundary: 08:00 (480 mins) to 21:30 (1290 mins)
    val startDayMinutes = 8 * 60
    val endDayMinutes = 21 * 60 + 30
    val totalMinutes = endDayMinutes - startDayMinutes

    val hourSlotHeightDp = 64.dp
    val totalHeightDp = hourSlotHeightDp * 14 // 14 hours range"""

replacement1 = """    val parsedCourses = remember(courses) { courses.map { ScheduleHelper.parse(it.timeSlot) }.filter { it.startMinutes < it.endMinutes } }
    
    val startHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 8 else (parsedCourses.minOf { it.startMinutes } / 60).coerceIn(8, 20)
    }
    val endHour = remember(parsedCourses) {
        if (parsedCourses.isEmpty()) 17 else ((parsedCourses.maxOf { it.endMinutes } / 60) + 1).coerceIn(9, 21)
    }

    val startDayMinutes = startHour * 60
    val endDayMinutes = endHour * 60
    val totalMinutes = endDayMinutes - startDayMinutes

    // Compact mode: scale hour block based on how many hours we have to fit
    val hourCount = endHour - startHour
    val hourSlotHeightDp = 46.dp
    val totalHeightDp = hourSlotHeightDp * hourCount"""

content = content.replace(target1, replacement1)

# 2. Update the time column labels loop
target2 = """                            Column(
                                modifier = Modifier
                                    .width(timeColWidth)
                                    .fillMaxHeight()
                            ) {
                                for (hour in 8..21) {"""

replacement2 = """                            Column(
                                modifier = Modifier
                                    .width(timeColWidth)
                                    .fillMaxHeight()
                            ) {
                                for (hour in startHour..endHour) {"""

content = content.replace(target2, replacement2)

# 3. Update the hour separator lines loop
target3 = """                                    Column {
                                        for (hour in 8..21) {"""

replacement3 = """                                    Column {
                                        for (hour in startHour..endHour) {"""

content = content.replace(target3, replacement3)


# 4. Remove the hardcoded 340.dp height to allow it to be compact without unnecessary scrolling constraints if it fits.
target4 = """                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .verticalScroll(verticalScrollState)
                    ) {"""

replacement4 = """                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp) // Limits max height, but wraps compact if smaller
                            .verticalScroll(verticalScrollState)
                    ) {"""

content = content.replace(target4, replacement4)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

