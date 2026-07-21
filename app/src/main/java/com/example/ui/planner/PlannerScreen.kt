package com.example.ui.planner

import android.Manifest
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ZielApplication
import com.example.database.TaskEntity
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class CalendarEvent(val title: String, val startTime: Long, val endTime: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = viewModel(
        factory = PlannerViewModel.provideFactory(
            (LocalContext.current.applicationContext as ZielApplication).container.usageRepository
        )
    )
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }
    
    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    val tasks by viewModel.tasks.collectAsState()
    
    var showAddTaskDialog by remember { mutableStateOf(false) }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            withContext(Dispatchers.IO) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                val startOfDay = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, 1)
                val endOfDay = cal.timeInMillis
                
                val projection = arrayOf(
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND
                )
                
                val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
                val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())
                
                try {
                    context.contentResolver.query(
                        CalendarContract.Events.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        "${CalendarContract.Events.DTSTART} ASC"
                    )?.use { cursor ->
                        val titleIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                        val startIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                        val endIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
                        
                        val eventList = mutableListOf<CalendarEvent>()
                        while (cursor.moveToNext()) {
                            eventList.add(
                                CalendarEvent(
                                    title = cursor.getString(titleIdx),
                                    startTime = cursor.getLong(startIdx),
                                    endTime = cursor.getLong(endIdx)
                                )
                            )
                        }
                        events = eventList
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Today", "Week", "Month", "Timeline")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Indigo600,
                contentColor = SurfaceWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        selectedContentColor = Indigo600,
                        unselectedContentColor = Slate500
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == 0) {
                    TodayView(
                        tasks = tasks,
                        events = events,
                        hasPermission = hasPermission,
                        onRequestPermission = { permissionLauncher.launch(Manifest.permission.READ_CALENDAR) },
                        onCompleteTask = { viewModel.completeTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("${tabs[selectedTab]} View Placeholder", color = Slate500)
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, desc, cat, prio, time, due, rem, rec ->
                viewModel.addTask(title, desc, cat, prio, time, due, rem, rec)
                showAddTaskDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayView(
    tasks: List<TaskEntity>,
    events: List<CalendarEvent>,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    val df = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(df.format(Date()), color = Indigo600, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Indigo600, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text("Today's Goal", color = Indigo100, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("6 Hours", color = SurfaceWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.4f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).align(Alignment.CenterHorizontally),
                    color = Emerald400,
                    trackColor = Indigo800
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Tasks", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        val pendingTasks = tasks.filter { it.status != "Completed" }
        if (pendingTasks.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No tasks for today. Tap + to add one.", color = Slate500)
                }
            }
        } else {
            items(pendingTasks, key = { it.taskId }) { task ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onCompleteTask(task)
                                true
                            }
                            SwipeToDismissBoxValue.EndToStart -> {
                                onDeleteTask(task)
                                true
                            }
                            else -> false
                        }
                    }
                )
                
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> Emerald500
                                SwipeToDismissBoxValue.EndToStart -> Rose500
                                else -> Color.Transparent
                            }, label = "swipe_color"
                        )
                        val icon = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                            SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                            else -> null
                        }
                        val alignment = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                            else -> Alignment.Center
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color, RoundedCornerShape(12.dp))
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            if (icon != null) {
                                Icon(icon, contentDescription = null, tint = SurfaceWhite)
                            }
                        }
                    },
                    content = {
                        TaskItem(task)
                    }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Calendar Events", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (!hasPermission) {
                    TextButton(onClick = onRequestPermission) {
                        Text("Sync")
                    }
                }
            }
        }
        
        if (events.isEmpty() && hasPermission) {
            item {
                Text("No calendar events today.", color = Slate500, modifier = Modifier.padding(top = 8.dp))
            }
        } else {
            items(events) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate50),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).background(Orange500, RoundedCornerShape(50)))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(event.title, style = MaterialTheme.typography.bodyLarge, color = Slate900)
                        }
                        Text(
                            "${tf.format(Date(event.startTime))} - ${tf.format(Date(event.endTime))}",
                            color = Slate500,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        val completedTasks = tasks.filter { it.status == "Completed" }
        if (completedTasks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Completed", color = Slate900, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            items(completedTasks, key = { it.taskId }) { task ->
                TaskItem(task = task, isCompleted = true)
            }
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity, isCompleted: Boolean = false) {
    val priorityColor = when (task.priority) {
        "Critical" -> Rose500
        "High" -> Orange500
        "Medium" -> Yellow500
        else -> Emerald500
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Slate50 else SurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(12.dp).background(priorityColor, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (isCompleted) Slate500 else Slate900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(task.description, color = Slate500, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskBadge(task.categoryId.toString(), Slate100, Slate700)
                    TaskBadge("${task.estimatedDuration} min", Indigo50, Indigo700)
                }
            }
            if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Emerald500)
            }
        }
    }
}

@Composable
fun TaskBadge(text: String, bgColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Long?, Long?, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Study") }
    var priority by remember { mutableStateOf("Medium") }
    var estimatedTime by remember { mutableStateOf("60") }
    
    val categories = listOf("Study", "Coding", "Gym", "Reading", "Work", "Personal", "Health")
    val priorities = listOf("Critical", "High", "Medium", "Low")
    
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = SurfaceWhite) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Add Task", style = MaterialTheme.typography.titleLarge, color = Slate900, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = !showCategoryMenu },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }
                
                ExposedDropdownMenuBox(
                    expanded = showPriorityMenu,
                    onExpandedChange = { showPriorityMenu = !showPriorityMenu },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPriorityMenu) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = { showPriorityMenu = false }
                    ) {
                        priorities.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    priority = selectionOption
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = estimatedTime,
                onValueChange = { estimatedTime = it },
                label = { Text("Estimated Time (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            title,
                            description,
                            category,
                            priority,
                            estimatedTime.toIntOrNull() ?: 0,
                            null, // dueTime
                            null, // reminderTime
                            "None" // recurrence
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                shape = RoundedCornerShape(16.dp),
                enabled = title.isNotBlank()
            ) {
                Text("Save Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
