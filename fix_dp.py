import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

content = content.replace(
    "val totalHeightDp = (((endDayMinutes - startDayMinutes) / 60f) + 1) * hourSlotHeightDp.value.dp",
    "val totalHeightDp = ((((endDayMinutes - startDayMinutes) / 60f) + 1) * hourSlotHeightDp.value).dp"
)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
