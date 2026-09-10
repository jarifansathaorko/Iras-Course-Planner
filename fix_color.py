import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = "color = Color.WHITE"
replacement = "color = android.graphics.Color.WHITE"

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

