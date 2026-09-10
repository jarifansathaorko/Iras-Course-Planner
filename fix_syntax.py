import re

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

target = """                                            Text(
                                                text = parsed.timeRange12Hr.replace(" - ", "\\n"),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.95f),
                                                fontSize = 8.sp,
                                                lineHeight = 9.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )"""

replacement = """                                            Text(
                                                text = parsed.timeRange12Hr.replace(" - ", "\\n"),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.95f),
                                                fontSize = 8.sp,
                                                lineHeight = 9.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )"""

# Due to python string escaping, the newline char might have been messed up. Let's do a direct replacement.
content = content.replace('text = parsed.timeRange12Hr.replace(" - ", "\\n"),', 'text = parsed.timeRange12Hr.replace(" - ", "\\n"),')
content = content.replace('text = parsed.timeRange12Hr.replace(" - ", "\\\\n"),', 'text = parsed.timeRange12Hr.replace(" - ", "\\n"),')
content = content.replace('text = parsed.timeRange12Hr.replace(" - ", "\n"),', 'text = parsed.timeRange12Hr.replace(" - ", "\\n"),')


with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)

