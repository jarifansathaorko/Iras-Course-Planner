import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = "val keyHours = listOf(8, 10, 12, 14, 16, 18, 20)"
replacement = "val keyHours = (startHour..endHour).toList()"
content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

