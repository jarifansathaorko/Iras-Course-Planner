import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

content = content.replace("""                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
                        .width(totalGridWidth)
                ) {""", """                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(totalGridWidth)
                ) {""")

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
