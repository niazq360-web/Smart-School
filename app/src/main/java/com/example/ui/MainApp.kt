package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.AppLanguage
import com.example.model.SchoolProfile
import com.example.model.UserRole
import com.example.ui.components.LanguageSwitcherRow
import com.example.ui.screens.*
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val titleKey: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "dashboard", Icons.Default.Dashboard)
    object Students : Screen("students", "students", Icons.Default.People)
    object Teachers : Screen("teachers", "teachers", Icons.Default.School)
    object Attendance : Screen("attendance", "attendance", Icons.Default.QrCodeScanner)
    object Fees : Screen("fees", "fees", Icons.Default.Payments)
    object Exams : Screen("exams", "exams", Icons.Default.Assessment)
    object AiTools : Screen("ai_tools", "ai_tools", Icons.Default.AutoAwesome)
    object Timetable : Screen("timetable", "timetable", Icons.Default.CalendarMonth)
    object Finance : Screen("finance", "finance", Icons.Default.AccountBalance)
    object Administration : Screen("administration", "administration", Icons.Default.AdminPanelSettings)
    object Settings : Screen("settings", "settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(repository: SchoolRepository) {
    var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
    var currentRole by remember { mutableStateOf(UserRole.SUPER_ADMIN) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    // BackHandler to navigate back to Dashboard if in any subscreen
    BackHandler(enabled = currentScreen != Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    // Set LayoutDirection based on active language (RTL for Urdu and Sindhi)
    CompositionLocalProvider(LocalLayoutDirection provides currentLanguage.layoutDirection) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(310.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SchoolBlue)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = schoolProfile?.name ?: "Smart School",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Smart School Management System",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val allScreens = listOf(
                        Screen.Dashboard,
                        Screen.Students,
                        Screen.Teachers,
                        Screen.Attendance,
                        Screen.Fees,
                        Screen.Exams,
                        Screen.AiTools,
                        Screen.Timetable,
                        Screen.Finance,
                        Screen.Administration,
                        Screen.Settings
                    )

                    allScreens.forEach { screen ->
                        val isSelected = currentScreen.route == screen.route
                        NavigationDrawerItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = {
                                Text(
                                    text = StringsManager.get(screen.titleKey, currentLanguage),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                currentScreen = screen
                                coroutineScope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = StringsManager.get(currentScreen.titleKey, currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${currentRole.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SchoolBlue
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            LanguageSwitcherRow(
                                currentLanguage = currentLanguage,
                                onLanguageSelected = { currentLanguage = it },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        val bottomNavItems = listOf(
                            Screen.Dashboard,
                            Screen.Students,
                            Screen.Attendance,
                            Screen.Fees,
                            Screen.AiTools
                        )

                        bottomNavItems.forEach { screen ->
                            val selected = currentScreen.route == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentScreen = screen },
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = {
                                    Text(
                                        text = StringsManager.get(screen.titleKey, currentLanguage),
                                        maxLines = 1,
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        Screen.Dashboard -> DashboardScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole,
                            onNavigateTo = { route ->
                                when (route) {
                                    "students" -> currentScreen = Screen.Students
                                    "teachers" -> currentScreen = Screen.Teachers
                                    "attendance" -> currentScreen = Screen.Attendance
                                    "fees" -> currentScreen = Screen.Fees
                                    "finance" -> currentScreen = Screen.Finance
                                    "ai_tools" -> currentScreen = Screen.AiTools
                                }
                            }
                        )
                        Screen.Students -> StudentsScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Teachers -> TeachersScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Attendance -> AttendanceScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Fees -> FeesScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Exams -> ExamsScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.AiTools -> AiToolsScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Timetable -> TimetableScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Finance -> FinanceScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Administration -> AdministrationScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            currentRole = currentRole
                        )
                        Screen.Settings -> SettingsScreen(
                            repository = repository,
                            currentLanguage = currentLanguage,
                            onLanguageChange = { currentLanguage = it },
                            currentRole = currentRole,
                            onRoleChange = { currentRole = it }
                        )
                    }
                }
            }
        }
    }
}
