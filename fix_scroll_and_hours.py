import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# Fix horizontal scrolling width logic
target_scroll = r"val timeColWidth = 46\.dp\s+val dayColWidth = \(\(availableWidth - timeColWidth - 16\.dp\) / displayDays\.size\.coerceAtLeast\(1\)\) // 16\.dp for padding\s+Column\(modifier = Modifier\.padding\(8\.dp\)\) \{"
replacement_scroll = """val timeColWidth = 46.dp
                val minDayWidth = 72.dp
                val calculatedDayWidth = (availableWidth - timeColWidth - 16.dp) / displayDays.size.coerceAtLeast(1)
                val dayColWidth = maxOf(minDayWidth, calculatedDayWidth)
                val totalGridWidth = timeColWidth + (dayColWidth * displayDays.size)
                
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
                        .width(totalGridWidth)
                ) {"""
content = re.sub(target_scroll, replacement_scroll, content)

# Check if the time trimming logic is actually correct
# Earlier I set startHour to coerceIn(6, 20) and endHour to coerceIn(9, 22). Let's make sure it worked.
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

print("done")
