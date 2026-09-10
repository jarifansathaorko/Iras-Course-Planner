import re
with open("app/src/main/java/com/example/ui/viewmodel/CourseViewModel.kt", "r") as f:
    content = f.read()

target = """                            getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                .edit().putInt("local_csv_version", v).apply()"""

replacement = """                            getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                .edit { putInt("local_csv_version", v) }"""

content = content.replace(target, replacement)

# Add import
import_str = "import androidx.core.content.edit\n"
if import_str not in content:
    content = content.replace("import android.app.Application", "import android.app.Application\n" + import_str)

with open("app/src/main/java/com/example/ui/viewmodel/CourseViewModel.kt", "w") as f:
    f.write(content)
