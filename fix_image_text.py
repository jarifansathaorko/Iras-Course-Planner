import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = """                    // Draw Course Code & Section inside block
                    textPaint.textSize = 22f
                    textPaint.textAlign = Paint.Align.CENTER
                    val textY = blockTop + ((blockBottom - blockTop) / 2f) + 7f
                    canvas.drawText("${course.courseCode} - ${course.section}", blockRect.centerX(), textY, textPaint)
                    textPaint.textAlign = Paint.Align.LEFT"""

replacement = """                    // Draw Course Code & Section inside block
                    textPaint.textSize = 22f
                    textPaint.textAlign = Paint.Align.CENTER
                    
                    val centerY = blockTop + ((blockBottom - blockTop) / 2f)
                    
                    // Draw Course Code
                    canvas.drawText(course.courseCode, blockRect.centerX(), centerY - 4f, textPaint)
                    
                    // Draw Section
                    val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.WHITE
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                        textSize = 18f
                        textAlign = Paint.Align.CENTER
                        alpha = 230
                    }
                    canvas.drawText("Sec ${course.section}", blockRect.centerX(), centerY + 20f, secPaint)
                    
                    textPaint.textAlign = Paint.Align.LEFT"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

