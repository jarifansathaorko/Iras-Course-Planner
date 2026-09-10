package com.example.data

import android.content.Context
import com.example.data.entity.CourseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.absoluteValue

object UpdateManager {
    
    // Simulate checking for a version
    suspend fun checkForUpdate(currentVersion: Int): Int {
        // In real Firebase: This would check Firestore document for the latest version.
        // For now, returning a dummy higher version to trigger the UI if needed.
        return currentVersion + 1 
    }

    suspend fun downloadAndParseCsv(urlString: String): List<CourseEntity> = withContext(Dispatchers.IO) {
        val courses = mutableListOf<CourseEntity>()
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var isFirstLine = true
            
            reader.forEachLine { line ->
                if (isFirstLine) {
                    isFirstLine = false // Skip header: Course,Title,Sec,Time,Enrolled,Capacity,Credit,Faculty
                    return@forEachLine
                }
                
                // Use a simple split (assuming no commas inside quotes in this specific CSV format)
                // If there are commas in titles, a proper CSV parser (like openCSV) should be used.
                // Looking at the data, commas in faculty or titles might exist, so we should be careful.
                // A basic regex for CSV/TSV splitting considering quotes:
                val delimiter = if (line.contains('\t')) "\t" else ","
                val tokens = line.split("$delimiter(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()).map { it.removeSurrounding("\"").trim() }
                
                if (tokens.size >= 8) {
                    val courseCode = tokens[0]
                    val title = tokens[1]
                    val section = tokens[2].toIntOrNull() ?: 1
                    val timeSlot = tokens[3]
                    val enrolled = tokens[4].toIntOrNull() ?: 0
                    val capacity = tokens[5].toIntOrNull() ?: 0
                    val credit = tokens[6].toIntOrNull() ?: 3
                    val faculty = tokens[7]
                    
                    // Generate deterministic ID so saved plans don't break if CSV order changes
                    val id = "$courseCode-$section".hashCode().absoluteValue
                    
                    courses.add(
                        CourseEntity(
                            id = id,
                            courseCode = courseCode,
                            title = title,
                            section = section,
                            timeSlot = timeSlot,
                            enrolled = enrolled,
                            capacity = capacity,
                            credit = credit,
                            faculty = faculty
                        )
                    )
                }
            }
            reader.close()
        }
        return@withContext courses
    }
}
