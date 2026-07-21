import re

with open('/app/applet/app/src/main/java/com/example/ui/labs/LabsScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace CoachView
coach_view = """@Composable
fun CoachView(dailyAnalytics: DailyAnalytics?, insights: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Good Morning 👋", color = Slate900, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today's Goal", color = Indigo100, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("6 Hours", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Confidence", color = Emerald100, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("92%", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Today's Recommendation", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Yellow600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start coding between 9:00–11:00 AM.", color = Slate900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Reason:", color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Your average focus score is highest during this period based on your usage history.", color = Slate500, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Smart Suggestions", color = Slate900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        items(insights) { insight ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo500)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(insight, color = Slate700, fontSize = 14.sp, lineHeight = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Emerald500, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("High Confidence (Local Data)", color = Slate400, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}"""

content = re.sub(r'@Composable\nfun CoachView.*?\}\n\}\n', coach_view + '\n', content, flags=re.DOTALL)

with open('/app/applet/app/src/main/java/com/example/ui/labs/LabsScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
