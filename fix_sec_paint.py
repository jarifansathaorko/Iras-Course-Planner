import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = """                    val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                        textSize = 18f
                        textAlign = Paint.Align.CENTER
                        alpha = 230
                    }"""

replacement = """                    val secPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    secPaint.color = android.graphics.Color.WHITE
                    secPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    secPaint.textSize = 18f
                    secPaint.textAlign = Paint.Align.CENTER
                    secPaint.alpha = 230"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

