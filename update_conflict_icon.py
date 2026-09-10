import re

with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "r") as f:
    content = f.read()

target = """        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ConflictRedBg)
                    .border(1.5.dp, ConflictRedBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Conflict Warning",
                    tint = ConflictRed,
                    modifier = Modifier.size(30.dp)
                )
            }
        },"""

replacement = """        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ConflictRed.copy(alpha = 0.1f))
                    .border(1.5.dp, ConflictRed.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Conflict Warning",
                    tint = ConflictRed,
                    modifier = Modifier.size(30.dp)
                )
            }
        },"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/ConflictAlertDialog.kt", "w") as f:
        f.write(content)
    print("Successfully replaced icon box.")
else:
    print("Target not found.")

