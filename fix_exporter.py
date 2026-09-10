import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

target = """                    // Draw Course Code & Section inside block
                    textPaint.textSize = 22f
                    textPaint.textAlign = Paint.Align.CENTER
                    
                    val centerY = blockTop + ((blockBottom - blockTop) / 2f)
                    
                    // Draw Course Code
                    canvas.drawText(course.courseCode, blockRect.centerX(), centerY - 4f, textPaint)
                    
                    // Draw Section
                    val secPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    secPaint.color = android.graphics.Color.WHITE
                    secPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    secPaint.textSize = 18f
                    secPaint.textAlign = Paint.Align.CENTER
                    secPaint.alpha = 230
                    canvas.drawText("Sec ${course.section}", blockRect.centerX(), centerY + 20f, secPaint)
                    
                    textPaint.textAlign = Paint.Align.LEFT"""

replacement = """                    // Time Band Background
                    val bandRect = RectF(blockRect.left, blockRect.top, blockRect.right, blockRect.top + 34f)
                    val bandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    bandPaint.color = android.graphics.Color.BLACK
                    bandPaint.alpha = 50
                    val bandPath = android.graphics.Path().apply {
                        addRoundRect(bandRect, floatArrayOf(10f, 10f, 10f, 10f, 0f, 0f, 0f, 0f), android.graphics.Path.Direction.CW)
                    }
                    canvas.drawPath(bandPath, bandPaint)

                    // Draw Time Range
                    val timePaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    timePaint.color = android.graphics.Color.WHITE
                    timePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    timePaint.textSize = 15f
                    timePaint.textAlign = Paint.Align.CENTER
                    val timeString = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", "")
                    canvas.drawText(timeString, blockRect.centerX(), blockRect.top + 24f, timePaint)

                    // Draw Course Code & Section inside block
                    textPaint.textSize = 22f
                    textPaint.textAlign = Paint.Align.CENTER
                    
                    // Calculate center space below the time band
                    val contentTop = blockRect.top + 34f
                    val contentCenterY = contentTop + ((blockBottom - contentTop) / 2f)
                    
                    // Draw Course Code
                    canvas.drawText(course.courseCode, blockRect.centerX(), contentCenterY - 4f, textPaint)
                    
                    // Draw Section
                    val secPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    secPaint.color = android.graphics.Color.WHITE
                    secPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    secPaint.textSize = 18f
                    secPaint.textAlign = Paint.Align.CENTER
                    secPaint.alpha = 230
                    canvas.drawText("Sec ${course.section}", blockRect.centerX(), contentCenterY + 20f, secPaint)
                    
                    textPaint.textAlign = Paint.Align.LEFT"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "w") as f:
    f.write(content)

