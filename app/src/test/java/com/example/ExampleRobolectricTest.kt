package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CourseDataCatalog
import com.example.data.entity.CourseEntity
import com.example.domain.ConflictResult
import com.example.domain.CourseSearchHelper
import com.example.domain.ScheduleHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Iras Course Planner", appName)
    }

    @Test
    fun `schedule bitmask representation accurately catches overlapping days`() {
        // "MW: 0800-0930" -> Monday, Wednesday
        // "M: 0800-0930" -> Monday only
        val schedMW = ScheduleHelper.parse("MW: 0800-0930")
        val schedM = ScheduleHelper.parse("M: 0800-0930")
        val schedTR = ScheduleHelper.parse("TR: 0800-0930") // Tuesday, Thursday

        assertTrue((schedMW.dayMask and schedM.dayMask) != 0)
        assertEquals(0, schedMW.dayMask and schedTR.dayMask)
    }

    @Test
    fun `course data catalog contains all university courses`() {
        assertTrue("Course catalog should contain over 500 sections", CourseDataCatalog.initialCourses.size >= 500)
    }

    @Test
    fun `schedule parser correctly extracts days and times`() {
        // "AR: 1440-1610" -> Sunday, Thursday from 14:40 to 16:10
        val parsed = ScheduleHelper.parse("AR: 1440-1610")
        assertEquals(2, parsed.days.size)
        assertEquals(14 * 60 + 40, parsed.startMinutes)
        assertEquals(16 * 60 + 10, parsed.endMinutes)
        assertEquals("Sun, Thu 2:40 PM - 4:10 PM", parsed.formattedTime)
    }

    @Test
    fun `schedule conflict detection detects overlapping classes`() {
        val course1 = CourseEntity(
            id = 1,
            courseCode = "CSE100",
            title = "Intro to CS",
            section = 1,
            timeSlot = "ST: 0800-0930",
            enrolled = 30,
            capacity = 35,
            credit = 3,
            faculty = "Prof A"
        )
        val course2Clashing = CourseEntity(
            id = 2,
            courseCode = "MAT110",
            title = "Calculus I",
            section = 1,
            timeSlot = "ST: 0800-0930",
            enrolled = 25,
            capacity = 30,
            credit = 3,
            faculty = "Prof B"
        )
        val course3NonClashing = CourseEntity(
            id = 3,
            courseCode = "PHY111",
            title = "Physics I",
            section = 1,
            timeSlot = "MW: 1120-1250",
            enrolled = 20,
            capacity = 30,
            credit = 3,
            faculty = "Prof C"
        )

        val conflict = ScheduleHelper.findConflict(course2Clashing, listOf(course1))
        assertNotNull(conflict)
        assertTrue(conflict is ConflictResult.TimeOverlap)

        val noConflict = ScheduleHelper.findConflict(course3NonClashing, listOf(course1))
        assertNull(noConflict)
    }

    @Test
    fun `same course different section causes same-course conflict`() {
        val course1Sec1 = CourseEntity(
            id = 10,
            courseCode = "CSE100",
            title = "Intro to CS",
            section = 1,
            timeSlot = "ST: 0800-0930",
            enrolled = 30,
            capacity = 35,
            credit = 3,
            faculty = "Prof A"
        )
        val course1Sec2 = CourseEntity(
            id = 11,
            courseCode = "CSE100",
            title = "Intro to CS",
            section = 2,
            timeSlot = "MW: 1440-1610",
            enrolled = 25,
            capacity = 35,
            credit = 3,
            faculty = "Prof D"
        )

        val conflict = ScheduleHelper.findConflict(course1Sec2, listOf(course1Sec1))
        assertNotNull(conflict)
        assertTrue(conflict is ConflictResult.SameCourse)
    }

    @Test
    fun `search by course code prefix cse1 returns all matching courses and sections`() {
        val results = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse1")
        assertTrue("Searching 'cse1' should return results", results.isNotEmpty())
        assertTrue("Should contain CSE100", results.any { it.courseCode == "CSE100" })
        assertTrue("Should contain multiple sections", results.size > 1)
    }

    @Test
    fun `search by course code prefix cse10 returns CSE100 sections`() {
        val results = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse10")
        assertTrue("Searching 'cse10' should return results", results.isNotEmpty())
        assertTrue("Should contain CSE100", results.any { it.courseCode == "CSE100" })
    }

    @Test
    fun `search by exact course code cse100 returns all sections of CSE100`() {
        val results = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse100")
        assertTrue("Searching 'cse100' should return results", results.isNotEmpty())
        val cse100Sections = results.filter { it.courseCode == "CSE100" }
        assertTrue("CSE100 should have multiple sections", cse100Sections.size >= 3)
    }

    @Test
    fun `search by course code and section cse100 2 returns only that specific section`() {
        val results = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse100 2")
        assertEquals("Should return exactly 1 section", 1, results.size)
        assertEquals("CSE100", results[0].courseCode)
        assertEquals(2, results[0].section)
    }

    @Test
    fun `search by course code with space and section cse 100 2 returns only section 2`() {
        val results = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse 100 2")
        assertEquals("Should return exactly 1 section", 1, results.size)
        assertEquals("CSE100", results[0].courseCode)
        assertEquals(2, results[0].section)
    }

    @Test
    fun `search by cse201 returns all sections and cse201 4 returns section 4`() {
        val allSecs = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse201")
        assertTrue("cse201 should return multiple sections", allSecs.size > 1)

        val sec4 = CourseSearchHelper.filterCourses(CourseDataCatalog.initialCourses, "cse201 4")
        assertEquals(1, sec4.size)
        assertEquals("CSE201", sec4[0].courseCode)
        assertEquals(4, sec4[0].section)
    }

    @Test
    fun `full section capacity check correctly flags 100 percent filled courses`() {
        val fullCourse = CourseEntity(
            id = 50,
            courseCode = "CSE100L",
            title = "Labwork for CSE 100",
            section = 3,
            timeSlot = "S: 1300-1430",
            enrolled = 31,
            capacity = 31,
            credit = 1,
            faculty = "Prof E"
        )
        val isFull = fullCourse.capacity > 0 && fullCourse.enrolled >= fullCourse.capacity
        assertTrue("Course with 31/31 enrolled must be identified as 100% full", isFull)
        assertEquals(0, (fullCourse.capacity - fullCourse.enrolled).coerceAtLeast(0))

        val openCourse = CourseEntity(
            id = 51,
            courseCode = "CSE100L",
            title = "Labwork for CSE 100",
            section = 4,
            timeSlot = "S: 1120-1250",
            enrolled = 14,
            capacity = 20,
            credit = 1,
            faculty = "Prof F"
        )
        val isOpenFull = openCourse.capacity > 0 && openCourse.enrolled >= openCourse.capacity
        assertTrue("Course with 14/20 enrolled should not be full", !isOpenFull)
        assertEquals(6, openCourse.capacity - openCourse.enrolled)
    }
}
