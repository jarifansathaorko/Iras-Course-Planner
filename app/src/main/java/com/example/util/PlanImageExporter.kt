package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.example.R
import com.example.data.entity.CourseEntity
import com.example.domain.Day
import com.example.domain.ScheduleHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PlanImageExporter {

    private val COURSE_PALETTE = intArrayOf(
        0xFF1D4ED8.toInt(), // Deep Blue
        0xFF0D9488.toInt(), // Teal
        0xFF7C3AED.toInt(), // Violet
        0xFFB45309.toInt(), // Amber Brown
        0xFFBE123C.toInt(), // Rose Red
        0xFF047857.toInt(), // Emerald Green
        0xFF4338CA.toInt(), // Indigo
        0xFFC2410C.toInt(), // Orange
    )

    fun exportAndSharePlan(
        context: Context,
        planNumber: Int,
        planName: String,
        courses: List<CourseEntity>
    ) {
        val bitmap = renderPlanBitmap(context, planNumber, planName, courses)
        val file = saveBitmapToCache(context, bitmap, planNumber)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$planName Schedule")
            putExtra(
                Intent.EXTRA_TEXT,
                "Here is my course schedule for $planName (${courses.size} courses, ${courses.sumOf { it.credit }} credits)."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share $planName Schedule")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun renderPlanBitmap(
        context: Context,
        planNumber: Int,
        planName: String,
        courses: List<CourseEntity>
    ): Bitmap {
        val width = 1440
        val baseHeaderHeight = 320
        val gridHeight = 780
        val cardHeaderHeight = 90
        val courseItemHeight = 136
        val courseItemSpacing = 14
        val cardBottomPadding = 40
        val tableHeight = cardHeaderHeight + (courses.size * (courseItemHeight + courseItemSpacing)).coerceAtLeast(140) + cardBottomPadding
        val footerHeight = 140
        val padding = 48
        val totalHeight = baseHeaderHeight + gridHeight + tableHeight + footerHeight + (padding * 4)

        val bitmap = createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(0xFFF1F5F9.toInt())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Header Card
        val headerRect = RectF(
            padding.toFloat(),
            padding.toFloat(),
            (width - padding).toFloat(),
            (padding + baseHeaderHeight).toFloat()
        )
        paint.color = 0xFF0F172A.toInt()
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(headerRect, 32f, 32f, paint)

        // Header Title
        paint.color = 0xFF38BDF8.toInt()
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("IRAS COURSE PLANNER", headerRect.left + 48f, headerRect.top + 80f, paint)

        // Plan Name
        paint.color = Color.WHITE
        paint.textSize = 64f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(planName, headerRect.left + 48f, headerRect.top + 165f, paint)

        // Subtitle info
        paint.color = 0xFF94A3B8.toInt()
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val totalCredits = courses.sumOf { it.credit }
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        canvas.drawText(
            "${courses.size} Courses  •  $totalCredits Credits  •  Generated $dateStr",
            headerRect.left + 48f,
            headerRect.top + 235f,
            paint
        )

        // Gold Accent Badge
        val badgeRect = RectF(headerRect.right - 280f, headerRect.top + 60f, headerRect.right - 48f, headerRect.top + 130f)
        paint.color = 0xFFD97706.toInt()
        canvas.drawRoundRect(badgeRect, 20f, 20f, paint)
        paint.color = Color.WHITE
        paint.textSize = 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("PLAN $planNumber", badgeRect.centerX(), badgeRect.centerY() + 11f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Draw Iras Logo inside Header
        val logoDrawable = ContextCompat.getDrawable(context, R.drawable.ic_iras_logo)
        logoDrawable?.let {
            val logoSize = 110
            val logoX = (badgeRect.left - logoSize - 28f).toInt()
            val logoY = (headerRect.top + 40f).toInt()
            it.setBounds(logoX, logoY, logoX + logoSize, logoY + logoSize)
            it.draw(canvas)
        }

        // 2. Weekly Timetable Card
        var currentY = headerRect.bottom + padding
        val gridCardRect = RectF(
            padding.toFloat(),
            currentY,
            (width - padding).toFloat(),
            currentY + gridHeight
        )
        paint.color = Color.WHITE
        canvas.drawRoundRect(gridCardRect, 32f, 32f, paint)

        // Grid Title
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 38f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Weekly Class Timetable", gridCardRect.left + 40f, gridCardRect.top + 60f, paint)

        // Draw Timetable Matrix
        drawTimetableGrid(canvas, gridCardRect, courses)

        // 3. Course List Table Card
        currentY = gridCardRect.bottom + padding
        val tableCardRect = RectF(
            padding.toFloat(),
            currentY,
            (width - padding).toFloat(),
            currentY + tableHeight
        )
        paint.color = Color.WHITE
        canvas.drawRoundRect(tableCardRect, 32f, 32f, paint)

        // Draw Table Header & Rows
        drawCourseTable(canvas, tableCardRect, courses, cardHeaderHeight)

        // 4. Footer
        val footerY = tableCardRect.bottom + padding + 40f
        paint.color = 0xFF64748B.toInt()
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "Iras Course Planner • University Course Selection & Timetable Planner",
            (width / 2).toFloat(),
            footerY,
            paint
        )

        return bitmap
    }

    private fun drawTimetableGrid(canvas: Canvas, rect: RectF, courses: List<CourseEntity>) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val matrixLeft = rect.left + 30f
        val matrixRight = rect.right - 30f
        val matrixTop = rect.top + 90f
        val matrixBottom = rect.bottom - 30f

        // Days: Sun (A), Mon (M), Tue (T), Wed (W), Thu (R), Sat (S)
        val displayDays = listOf(
            Day.SUNDAY,
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.SATURDAY
        )

        val timeColWidth = 140f
        val dayColWidth = (matrixRight - matrixLeft - timeColWidth) / displayDays.size
        val dayHeaderHeight = 54f
        val gridContentTop = matrixTop + dayHeaderHeight

        // Time limits: 08:00 (480 mins) to 21:30 (1290 mins)
        val startDayMinutes = 8 * 60      // 480
        val endDayMinutes = 21 * 60 + 30  // 1290
        val totalMinutesRange = endDayMinutes - startDayMinutes

        // Draw Day Column Headers
        paint.color = 0xFF0F172A.toInt()
        paint.style = Paint.Style.FILL
        val headerBarRect = RectF(matrixLeft + timeColWidth, matrixTop, matrixRight, gridContentTop)
        canvas.drawRoundRect(headerBarRect, 12f, 12f, paint)

        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER

        for ((idx, day) in displayDays.withIndex()) {
            val colCenterX = matrixLeft + timeColWidth + (idx * dayColWidth) + (dayColWidth / 2f)
            canvas.drawText(day.shortName.uppercase(), colCenterX, matrixTop + 37f, paint)
        }
        paint.textAlign = Paint.Align.LEFT

        // Draw Hour Grid Lines & Labels
        val keyHours = listOf(8, 10, 12, 14, 16, 18, 20)
        paint.color = 0xFFE2E8F0.toInt()
        paint.strokeWidth = 2f

        for (hour in keyHours) {
            val minuteVal = hour * 60
            val fraction = (minuteVal - startDayMinutes).toFloat() / totalMinutesRange.toFloat()
            val yPos = gridContentTop + fraction * (matrixBottom - gridContentTop)

            // Hour Line
            paint.style = Paint.Style.STROKE
            paint.color = 0xFFE2E8F0.toInt()
            canvas.drawLine(matrixLeft + timeColWidth, yPos, matrixRight, yPos, paint)

            // Hour Label
            val hourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF64748B.toInt()
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            val amPm = if (hour >= 12) "PM" else "AM"
            val h12 = if (hour > 12) hour - 12 else hour
            canvas.drawText("%d:00 %s".format(h12, amPm), matrixLeft + timeColWidth - 14f, yPos + 8f, hourPaint)
        }

        // Draw Course Time Blocks
        val blockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        for ((cIndex, course) in courses.withIndex()) {
            val parsed = ScheduleHelper.parse(course.timeSlot)
            if (parsed.days.isEmpty() || parsed.startMinutes >= parsed.endMinutes) continue

            val color = COURSE_PALETTE[cIndex % COURSE_PALETTE.size]
            blockPaint.color = color
            blockPaint.style = Paint.Style.FILL

            val topFraction = (parsed.startMinutes - startDayMinutes).toFloat() / totalMinutesRange.toFloat()
            val bottomFraction = (parsed.endMinutes - startDayMinutes).toFloat() / totalMinutesRange.toFloat()

            val blockTop = gridContentTop + (topFraction * (matrixBottom - gridContentTop)).coerceAtLeast(0f)
            val blockBottom = gridContentTop + (bottomFraction * (matrixBottom - gridContentTop)).coerceAtMost(matrixBottom - gridContentTop)

            for (day in parsed.days) {
                val dayIdx = displayDays.indexOf(day)
                if (dayIdx >= 0) {
                    val blockLeft = matrixLeft + timeColWidth + (dayIdx * dayColWidth) + 4f
                    val blockRight = blockLeft + dayColWidth - 8f
                    val blockRect = RectF(blockLeft, blockTop, blockRight, blockBottom)

                    canvas.drawRoundRect(blockRect, 10f, 10f, blockPaint)

                    // Draw Course Code & Section inside block
                    textPaint.textSize = 22f
                    textPaint.textAlign = Paint.Align.CENTER
                    val textY = blockTop + ((blockBottom - blockTop) / 2f) + 7f
                    canvas.drawText("${course.courseCode} - ${course.section}", blockRect.centerX(), textY, textPaint)
                    textPaint.textAlign = Paint.Align.LEFT
                }
            }
        }
    }

    private fun drawCourseTable(
        canvas: Canvas,
        rect: RectF,
        courses: List<CourseEntity>,
        headerHeight: Int
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val cardLeft = rect.left + 36f
        val cardRight = rect.right - 36f

        // Card Section Header
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 36f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val totalCredits = courses.sumOf { it.credit }
        canvas.drawText("Course Details & Schedule", cardLeft, rect.top + 60f, paint)

        paint.color = 0xFF64748B.toInt()
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${courses.size} Courses  •  $totalCredits Credits", cardRight, rect.top + 60f, paint)
        paint.textAlign = Paint.Align.LEFT

        val courseItemHeight = 136f
        val courseItemSpacing = 14f
        var currentY = rect.top + headerHeight.toFloat()

        if (courses.isEmpty()) {
            paint.color = 0xFF94A3B8.toInt()
            paint.textSize = 30f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("No courses selected in this plan yet.", (cardLeft + cardRight) / 2f, currentY + 60f, paint)
            paint.textAlign = Paint.Align.LEFT
            return
        }

        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 22f
        }

        for ((idx, course) in courses.withIndex()) {
            val itemRect = RectF(cardLeft, currentY, cardRight, currentY + courseItemHeight)
            val courseColor = COURSE_PALETTE[idx % COURSE_PALETTE.size]

            // Card background: crisp surface with subtle border
            paint.style = Paint.Style.FILL
            paint.color = if (idx % 2 == 0) 0xFFFFFFFF.toInt() else 0xFFF8FAFC.toInt()
            canvas.drawRoundRect(itemRect, 18f, 18f, paint)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = 0xFFE2E8F0.toInt()
            canvas.drawRoundRect(itemRect, 18f, 18f, paint)
            paint.style = Paint.Style.FILL

            // Left vertical accent stripe matching the timetable block color
            val stripeRect = RectF(cardLeft, currentY, cardLeft + 12f, currentY + courseItemHeight)
            paint.color = courseColor
            canvas.drawRoundRect(stripeRect, 6f, 6f, paint)

            val contentLeft = cardLeft + 32f
            val rightColLeft = cardLeft + 680f

            // --- Row 1 (Top): Dot + Course Code + Section Badge + Credits Badge | Schedule Time ---
            val line1Y = currentY + 48f

            // Colored Dot
            paint.color = courseColor
            canvas.drawCircle(contentLeft + 8f, line1Y - 9f, 9f, paint)

            // Course Code
            paint.color = 0xFF0F172A.toInt()
            paint.textSize = 32f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(course.courseCode, contentLeft + 28f, line1Y, paint)

            val codeWidth = paint.measureText(course.courseCode)
            var badgeX = contentLeft + 28f + codeWidth + 18f

            // Section Pill Badge (e.g. "Sec 2")
            pillPaint.color = 0xFFE0F2FE.toInt() // Sky 100
            val secText = "Sec ${course.section}"
            val secTextWidth = badgeTextPaint.measureText(secText)
            val secBadgeRect = RectF(badgeX, line1Y - 27f, badgeX + secTextWidth + 24f, line1Y + 9f)
            canvas.drawRoundRect(secBadgeRect, 10f, 10f, pillPaint)
            badgeTextPaint.color = 0xFF0284C7.toInt() // Sky 600
            canvas.drawText(secText, badgeX + 12f, line1Y - 2f, badgeTextPaint)

            // Credits Pill Badge (e.g. "3 Credits")
            badgeX = secBadgeRect.right + 12f
            pillPaint.color = 0xFFF1F5F9.toInt() // Slate 100
            val crText = if (course.credit == 1) "1 Credit" else "${course.credit} Credits"
            val crTextWidth = badgeTextPaint.measureText(crText)
            val crBadgeRect = RectF(badgeX, line1Y - 27f, badgeX + crTextWidth + 24f, line1Y + 9f)
            canvas.drawRoundRect(crBadgeRect, 10f, 10f, pillPaint)
            badgeTextPaint.color = 0xFF475569.toInt() // Slate 600
            canvas.drawText(crText, badgeX + 12f, line1Y - 2f, badgeTextPaint)

            // Schedule on Right Column
            val parsed = ScheduleHelper.parse(course.timeSlot)
            val scheduleText = if (parsed.days.isNotEmpty()) {
                "${parsed.daysString}  ${parsed.timeRange12Hr}"
            } else {
                course.timeSlot
            }
            paint.color = 0xFF0F172A.toInt()
            paint.textSize = 26f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(scheduleText, rightColLeft, line1Y, paint)

            // --- Row 2 (Bottom): Full Course Title | Faculty Instructor ---
            val line2Y = currentY + 102f

            // Course Title
            paint.color = 0xFF334155.toInt() // Slate 700
            paint.textSize = 25f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val titleMaxChars = 46
            val displayTitle = if (course.title.length > titleMaxChars) {
                course.title.take(titleMaxChars - 1) + "…"
            } else {
                course.title
            }
            canvas.drawText(displayTitle, contentLeft + 28f, line2Y, paint)

            // Faculty Instructor
            val facultyText = if (course.faculty.isNotBlank() && course.faculty != "00 TBA TBA") {
                "Faculty: ${course.faculty}"
            } else {
                "Faculty: TBA"
            }
            paint.color = 0xFF64748B.toInt() // Slate 500
            paint.textSize = 24f
            val facultyMaxChars = 36
            val displayFaculty = if (facultyText.length > facultyMaxChars) {
                facultyText.take(facultyMaxChars - 1) + "…"
            } else {
                facultyText
            }
            canvas.drawText(displayFaculty, rightColLeft, line2Y, paint)

            currentY += courseItemHeight + courseItemSpacing
        }
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, planNumber: Int): File {
        val cacheDir = File(context.cacheDir, "shared_images")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, "schedule_plan_${planNumber}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
        return file
    }
}
