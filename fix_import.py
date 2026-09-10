import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

target = "import androidx.compose.foundation.layout.height"
replacement = "import androidx.compose.foundation.layout.height\nimport androidx.compose.foundation.layout.heightIn"

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

