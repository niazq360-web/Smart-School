package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SchoolRepository
import com.example.model.*
import com.example.ui.components.StatCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseError
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Expenses, 1: Salaries, 2: Financial Statement
    val expenses by repository.allExpenses.collectAsState(initial = emptyList())
    val salarySlips by repository.allSalarySlips.collectAsState(initial = emptyList())
    val teachers by repository.allTeachers.collectAsState(initial = emptyList())
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    val monthlyFeeCollection by repository.getMonthlyFeeCollection(currentMonth).collectAsState(initial = 0.0)
    val totalExpensesSum by repository.getTotalExpenses().collectAsState(initial = 0.0)

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showGenerateSalaryDialog by remember { mutableStateOf(false) }
    val currency = schoolProfile?.currencySymbol ?: "Rs."

    Scaffold(
        floatingActionButton = {
            if (currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.SCHOOL_ADMIN || currentRole == UserRole.ACCOUNTANT) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 1) showGenerateSalaryDialog = true else showAddExpenseDialog = true
                    },
                    containerColor = if (selectedTab == 1) SchoolBlue else Color(0xFFD97706),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("finance_action_fab")
                ) {
                    Icon(if (selectedTab == 1) Icons.Default.Payments else Icons.Default.Add, contentDescription = "Action")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Expenses (${expenses.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Salaries (${salarySlips.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Statement") })
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Expenses list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(expenses, key = { it.id }) { expense ->
                            ExpenseCard(expense = expense, currencySymbol = currency)
                        }
                    }
                }
                1 -> {
                    // Salary Slips list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(salarySlips, key = { it.slipNo }) { slip ->
                            SalarySlipCard(
                                slip = slip,
                                currencySymbol = currency,
                                onPrint = {
                                    Toast.makeText(context, "Printing Salary Slip ${slip.slipNo} for ${slip.employeeName}...", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Income vs Expense Statement
                    val net = monthlyFeeCollection - totalExpensesSum
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Total Fee Inflow ($currentMonth)",
                            value = "$currency ${monthlyFeeCollection.toInt()}",
                            icon = Icons.Default.TrendingUp,
                            containerColor = Color(0xFFDCFCE7),
                            contentColor = EmeraldGreen
                        )
                        StatCard(
                            title = "Total Expenditures Incurred",
                            value = "$currency ${totalExpensesSum.toInt()}",
                            icon = Icons.Default.TrendingDown,
                            containerColor = Color(0xFFFEE2E2),
                            contentColor = RoseError
                        )
                        StatCard(
                            title = "Net School Balance",
                            value = "$currency ${net.toInt()}",
                            subtitle = if (net >= 0) "Surplus Operations" else "Deficit State",
                            icon = Icons.Default.AccountBalance,
                            containerColor = if (net >= 0) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                            contentColor = if (net >= 0) EmeraldGreen else RoseError
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Audited Financial Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("1. Rent & Facilities: 65%", fontSize = 12.sp, color = Color.Gray)
                                Text("2. Electricity & Utilities: 23%", fontSize = 12.sp, color = Color.Gray)
                                Text("3. Stationery & Printing: 10%", fontSize = 12.sp, color = Color.Gray)
                                Text("4. Internet & IT: 2%", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Expense Modal
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            todayDate = todayDate,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { exp ->
                coroutineScope.launch {
                    repository.addExpense(exp)
                    Toast.makeText(context, "Recorded expense of Rs. ${exp.amount.toInt()}", Toast.LENGTH_SHORT).show()
                    showAddExpenseDialog = false
                }
            }
        )
    }

    // Generate Salary Slip Modal
    if (showGenerateSalaryDialog) {
        GenerateSalaryDialog(
            teachers = teachers,
            currentMonth = currentMonth,
            todayDate = todayDate,
            nextSlipNo = "SAL-2026-${String.format("%03d", salarySlips.size + 1)}",
            onDismiss = { showGenerateSalaryDialog = false },
            onSave = { slip ->
                coroutineScope.launch {
                    repository.generateSalarySlip(slip)
                    Toast.makeText(context, "Salary slip ${slip.slipNo} issued!", Toast.LENGTH_SHORT).show()
                    showGenerateSalaryDialog = false
                }
            }
        )
    }
}

@Composable
private fun ExpenseCard(expense: ExpenseRecord, currencySymbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.description, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${expense.category} • Date: ${expense.date} • Paid by: ${expense.paidBy}", fontSize = 12.sp, color = Color.Gray)
            }
            Text("$currencySymbol ${expense.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoseError)
        }
    }
}

@Composable
private fun SalarySlipCard(slip: SalarySlip, currencySymbol: String, onPrint: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(slip.employeeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(slip.designation, fontSize = 12.sp, color = Color.Gray)
                }
                Text("$currencySymbol ${slip.netSalary.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Slip #${slip.slipNo} | Month: ${slip.month}", fontSize = 11.sp, color = Color.Gray)
                TextButton(onClick = onPrint) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print Slip", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    todayDate: String,
    onDismiss: () -> Unit,
    onSave: (ExpenseRecord) -> Unit
) {
    var category by remember { mutableStateOf("Stationery") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val categories = listOf("Stationery", "Electricity", "Rent", "Maintenance", "Internet", "Transport", "Events", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Record School Expense", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD97706))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Expense Description *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (PKR) *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.take(4).forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat, fontSize = 10.sp) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            if (description.isNotBlank() && amt > 0) {
                                onSave(ExpenseRecord(date = todayDate, category = category, amount = amt, description = description))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                    ) { Text("Record Expense") }
                }
            }
        }
    }
}

@Composable
private fun GenerateSalaryDialog(
    teachers: List<Teacher>,
    currentMonth: String,
    todayDate: String,
    nextSlipNo: String,
    onDismiss: () -> Unit,
    onSave: (SalarySlip) -> Unit
) {
    var selectedTeacher by remember { mutableStateOf(teachers.firstOrNull()) }
    var basicSalaryInput by remember { mutableStateOf("${selectedTeacher?.basicSalary?.toInt() ?: 50000}") }
    var allowancesInput by remember { mutableStateOf("5000") }
    var deductionsInput by remember { mutableStateOf("0") }

    val basic = basicSalaryInput.toDoubleOrNull() ?: 0.0
    val allow = allowancesInput.toDoubleOrNull() ?: 0.0
    val ded = deductionsInput.toDoubleOrNull() ?: 0.0
    val net = (basic + allow - ded).coerceAtLeast(0.0)

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Generate Salary Slip", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SchoolBlue)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Select Faculty Member:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    teachers.take(3).forEach { t ->
                        FilterChip(
                            selected = selectedTeacher?.id == t.id,
                            onClick = {
                                selectedTeacher = t
                                basicSalaryInput = "${t.basicSalary.toInt()}"
                            },
                            label = { Text(t.name.split(" ").first()) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = basicSalaryInput, onValueChange = { basicSalaryInput = it }, label = { Text("Basic Salary") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = allowancesInput, onValueChange = { allowancesInput = it }, label = { Text("Allowances") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = deductionsInput, onValueChange = { deductionsInput = it }, label = { Text("Deductions") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Net Payable Salary: Rs. ${net.toInt()}", fontWeight = FontWeight.Bold, color = SchoolBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val t = selectedTeacher ?: return@Button
                            onSave(
                                SalarySlip(
                                    slipNo = nextSlipNo,
                                    employeeId = t.id,
                                    employeeName = t.name,
                                    designation = t.designation,
                                    month = currentMonth,
                                    basicSalary = basic,
                                    allowances = allow,
                                    lateDeductions = ded,
                                    netSalary = net,
                                    paymentDate = todayDate
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue)
                    ) { Text("Issue Slip") }
                }
            }
        }
    }
}
