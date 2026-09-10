import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# Make the course code text wrap instead of maxLines=1, and format it nicely
target1 = """                                            Text(
                                                text = "${course.courseCode}-${course.section}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = parsed.timeRange12Hr,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 8.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )"""

replacement1 = """                                            Text(
                                                text = "${course.courseCode}-${course.section}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                lineHeight = 11.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = parsed.timeRange12Hr.replace(" - ", "\n"),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.95f),
                                                fontSize = 8.sp,
                                                lineHeight = 9.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )"""

content = content.replace(target1, replacement1)

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

