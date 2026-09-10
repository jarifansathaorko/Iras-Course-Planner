import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

target_scroll = """                val timeColWidth = 46.dp
                val dayColWidth = ((availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)) // 16.dp for padding
                
                Column(modifier = Modifier.padding(8.dp)) {"""

replacement_scroll = """                val timeColWidth = 46.dp
                val minDayWidth = 60.dp
                val calculatedDayWidth = (availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)
                val dayColWidth = maxOf(minDayWidth, calculatedDayWidth)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)
                
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
                        .width(totalGridWidth)
                ) {"""

content = content.replace(target_scroll, replacement_scroll)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

