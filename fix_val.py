import re

with open("app/src/main/java/com/example/util/PlanImageExporter.kt", "r") as f:
    content = f.read()

# 'textPaint.alpha' does not exist, Paint.alpha is a property setter. Wait, 'alpha' inside apply block for Paint is valid in Kotlin.
# Let's check why "val cannot be reassigned" happened. It must be `textPaint.textAlign = Paint.Align.LEFT` or `textPaint.textSize = 22f` if textPaint is a val? No, properties of val can be mutated. 
# Wait, look at line 455: `alpha = 230`. Is `alpha` a property or method? In Android `Paint.alpha` is an int property. `setAlpha()` is the java method. Kotlin exposes `alpha = 230` as valid.
# Oh! "val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { ... alpha = 230 }"
# Wait, `val secPaint` is at line 450. Where is line 455?
