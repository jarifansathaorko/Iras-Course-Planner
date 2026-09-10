import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = """                val textX = blockRect.left + 16f
                var textY = blockRect.top + 36f

                // Course Code
                paint.color = Color.White.toInt()
                paint.textSize = 22f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${course.courseCode}-${course.section}", textX, textY, paint)

                // Time
                textY += 32f
                paint.textSize = 18f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.alpha = 230 // 90% opacity
                canvas.drawText(parsed.timeRange12Hr, textX, textY, paint)
                paint.alpha = 255"""

replacement = """                val textX = blockRect.left + 16f
                var textY = blockRect.top + 34f

                // Course Code
                paint.color = Color.White.toInt()
                paint.textSize = 22f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${course.courseCode}-${course.section}", textX, textY, paint)

                // Time (Split into two lines for compact blocks)
                paint.textSize = 18f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.alpha = 230 // 90% opacity
                
                val times = parsed.timeRange12Hr.split(" - ")
                if (times.size == 2) {
                    textY += 26f
                    canvas.drawText(times[0], textX, textY, paint)
                    textY += 22f
                    canvas.drawText(times[1], textX, textY, paint)
                } else {
                    textY += 28f
                    canvas.drawText(parsed.timeRange12Hr, textX, textY, paint)
                }
                paint.alpha = 255"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

