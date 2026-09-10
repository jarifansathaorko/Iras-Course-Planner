import re

with open("app/src/main/java/com/example/ui/screens/CourseCatalogScreen.kt", "r") as f:
    content = f.read()

target = """    val cardBorderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isSameCourseConflict -> MaterialTheme.colorScheme.outlineVariant
        conflict != null -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val cardBgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isSameCourseConflict -> MaterialTheme.colorScheme.surface
        conflict != null -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }"""

replacement = """    val cardBorderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        conflict != null -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val cardBgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/CourseCatalogScreen.kt", "w") as f:
    f.write(content)

