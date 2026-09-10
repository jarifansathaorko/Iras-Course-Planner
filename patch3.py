import re
with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "r") as f:
    content = f.read()

# 1. Height tuning
content = content.replace("val hourSlotHeightDp = 56.dp", "val hourSlotHeightDp = 80.dp")

# 2. Card modifier
content = content.replace("""        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {""", """        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {""")

# 3. BoxWithConstraints modifier
content = content.replace("""            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {""", """            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {""")

# 4. Column modifier
content = content.replace("""                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(totalGridWidth)
                ) {""", """                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(totalGridWidth)
                        .fillMaxHeight()
                ) {""")

# 5. Box height constraint removal
content = content.replace("""                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 480.dp) // Limits max height, but wraps compact if smaller
                            .verticalScroll(verticalScrollState)
                    ) {""", """                    // Scrollable Time Grid & Course Blocks (Vertical Only)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(verticalScrollState)
                    ) {""")

# 6. Font sizes inside Course Info
content = content.replace("""                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Course info
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }""", """                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black.copy(alpha = 0.2f))
                                                    .padding(horizontal = 2.dp, vertical = 3.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = parsed.timeRange12Hr.replace(" AM", "").replace(" PM", ""),
                                                    color = Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Course info
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = course.courseCode,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(1.dp))
                                                Text(
                                                    text = "Sec ${course.section}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                            }""")

with open("app/src/main/java/com/example/ui/components/WeeklyTimetableGrid.kt", "w") as f:
    f.write(content)
