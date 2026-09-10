import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# Change the day columns loop to 'until endHour' so it doesn't draw an extra slot
target_grid = """                                    // Draw horizontal hour guidelines
                                    Column {
                                        for (hour in startHour..endHour) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(hourSlotHeightDp)"""
                                                    
replacement_grid = """                                    // Draw horizontal hour guidelines
                                    Column {
                                        for (hour in startHour until endHour) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(hourSlotHeightDp)"""

content = content.replace(target_grid, replacement_grid)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

