package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.print.PrintableFeeReceiptDialog
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Payment History, 1: Fee Structure
    val feePayments by repository.allFeePayments.collectAsState(initial = emptyList())
    val feeStructures by repository.allFeeStructures.collectAsState(initial = emptyList())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    var showCollectFeeDialog by remember { mutableStateOf(false) }
    var selectedReceiptForPrint by remember { mutableStateOf<FeePayment?>(null) }
    val currency = schoolProfile?.currencySymbol ?: "Rs."

    Scaffold(
        floatingActionButton = {
            if (currentRole != UserRole.PARENT) {
                FloatingActionButton(
                    onClick = { showCollectFeeDialog = true },
                    containerColor = EmeraldGreen,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("collect_fee_fab")
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = "Collect Fee")
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
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Fee Collections (${feePayments.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Class Fee Structures (${feeStructures.size})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(feePayments, key = { it.receiptNo }) { payment ->
                        FeePaymentCard(
                            payment = payment,
                            currencySymbol = currency,
                            onPrintClick = { selectedReceiptForPrint = payment },
                            onWhatsAppClick = {
                                val msg = StringsManager.generateFeePaidAlert(
                                    studentName = payment.studentName,
                                    amount = payment.paidAmount,
                                    receiptNo = payment.receiptNo,
                                    schoolName = schoolProfile?.name ?: "Smart School",
                                    language = currentLanguage
                                )
                                val student = students.find { it.id == payment.studentId }
                                val phone = student?.whatsappNo ?: "+923001234567"
                                val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=${URLEncoder.encode(msg, "UTF-8")}")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp Alert: $msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(feeStructures, key = { it.className }) { struct ->
                        FeeStructureCard(struct = struct, currencySymbol = currency)
                    }
                }
            }
        }
    }

    // Collect Fee Modal
    if (showCollectFeeDialog) {
        CollectFeeModal(
            students = students,
            currencySymbol = currency,
            currentMonth = currentMonth,
            todayDate = todayDate,
            nextReceiptNo = "REC-2026-${String.format("%03d", feePayments.size + 1)}",
            onDismiss = { showCollectFeeDialog = false },
            onSave = { payment ->
                coroutineScope.launch {
                    repository.collectFee(payment)
                    Toast.makeText(context, "Fee collected! Receipt #${payment.receiptNo}", Toast.LENGTH_SHORT).show()
                    showCollectFeeDialog = false
                    selectedReceiptForPrint = payment
                }
            }
        )
    }

    // Printable Receipt Dialog
    selectedReceiptForPrint?.let { payment ->
        PrintableFeeReceiptDialog(
            payment = payment,
            profile = schoolProfile ?: SchoolProfile(),
            onDismiss = { selectedReceiptForPrint = null }
        )
    }
}

@Composable
private fun FeePaymentCard(
    payment: FeePayment,
    currencySymbol: String,
    onPrintClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(payment.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${payment.className} - ${payment.section} • ID: ${payment.studentId}", fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$currencySymbol ${payment.paidAmount.toInt()}",
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Receipt: ${payment.receiptNo}", fontSize = 11.sp, color = Color.Gray)
                Text("Billing Month: ${payment.month}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SchoolBlue)
                Text("Via: ${payment.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
            }

            if (payment.remainingBalance > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Remaining Due Balance: $currencySymbol ${payment.remainingBalance.toInt()}",
                    color = Color(0xFFDC2626),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onWhatsAppClick) {
                    Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                }
                OutlinedButton(
                    onClick = onPrintClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Print Slip", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FeeStructureCard(struct: FeeStructure, currencySymbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(struct.className, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Monthly Tuition:", fontSize = 12.sp, color = Color.Gray)
                Text("$currencySymbol ${struct.monthlyFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Admission Fee:", fontSize = 12.sp, color = Color.Gray)
                Text("$currencySymbol ${struct.admissionFee.toInt()}", fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Computer / Lab Fee:", fontSize = 12.sp, color = Color.Gray)
                Text("$currencySymbol ${struct.computerFee.toInt()}", fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Transport Fee:", fontSize = 12.sp, color = Color.Gray)
                Text("$currencySymbol ${struct.transportFee.toInt()}", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectFeeModal(
    students: List<Student>,
    currencySymbol: String,
    currentMonth: String,
    todayDate: String,
    nextReceiptNo: String,
    onDismiss: () -> Unit,
    onSave: (FeePayment) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var billingMonth by remember { mutableStateOf(currentMonth) }
    var tuitionFee by remember { mutableStateOf("4000") }
    var transportFee by remember { mutableStateOf("1500") }
    var computerFee by remember { mutableStateOf("600") }
    var discount by remember { mutableStateOf("0") }
    var previousDue by remember { mutableStateOf("0") }
    var paidAmountInput by remember { mutableStateOf("6100") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var paymentRef by remember { mutableStateOf("CASH-PAY") }

    val tuition = tuitionFee.toDoubleOrNull() ?: 0.0
    val transport = transportFee.toDoubleOrNull() ?: 0.0
    val computer = computerFee.toDoubleOrNull() ?: 0.0
    val disc = discount.toDoubleOrNull() ?: 0.0
    val prev = previousDue.toDoubleOrNull() ?: 0.0
    val totalPayable = (tuition + transport + computer + prev - disc).coerceAtLeast(0.0)
    val paid = paidAmountInput.toDoubleOrNull() ?: totalPayable
    val remainingBalance = (totalPayable - paid).coerceAtLeast(0.0)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Collect Student Fee", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Select Student:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    students.take(4).forEach { st ->
                        FilterChip(
                            selected = selectedStudent?.id == st.id,
                            onClick = {
                                selectedStudent = st
                                tuitionFee = if (st.className == "Class 10") "5000" else "4000"
                            },
                            label = { Text("${st.rollNo}. ${st.name.split(" ").first()}") }
                        )
                    }
                }

                selectedStudent?.let { st ->
                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Text(
                            text = "${st.name} | ${st.className} - ${st.section} | ID: ${st.id} | Father: ${st.fatherName}",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = billingMonth,
                    onValueChange = { billingMonth = it },
                    label = { Text("Billing Month") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tuitionFee, onValueChange = { tuitionFee = it }, label = { Text("Tuition Fee") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = transportFee, onValueChange = { transportFee = it }, label = { Text("Transport Fee") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = computerFee, onValueChange = { computerFee = it }, label = { Text("Computer Lab") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Discount") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = previousDue, onValueChange = { previousDue = it }, label = { Text("Previous Due") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment summary
                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable:", fontWeight = FontWeight.Bold)
                            Text("$currencySymbol ${totalPayable.toInt()}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = paidAmountInput,
                    onValueChange = { paidAmountInput = it },
                    label = { Text("Amount Paid ($currencySymbol) *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Cash", "JazzCash", "EasyPaisa", "Bank").forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = paymentRef,
                    onValueChange = { paymentRef = it },
                    label = { Text("Transaction Reference #") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val st = selectedStudent ?: return@Button
                            val payment = FeePayment(
                                receiptNo = nextReceiptNo,
                                studentId = st.id,
                                studentName = st.name,
                                className = st.className,
                                section = st.section,
                                month = billingMonth,
                                monthlyFee = tuition,
                                transportFee = transport,
                                computerFee = computer,
                                discountAmount = disc,
                                previousDue = prev,
                                totalPayable = totalPayable,
                                paidAmount = paid,
                                remainingBalance = remainingBalance,
                                paymentMethod = paymentMethod,
                                paymentRef = paymentRef,
                                paymentDate = todayDate
                            )
                            onSave(payment)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        enabled = selectedStudent != null && paid > 0
                    ) {
                        Text("Record & Generate Receipt")
                    }
                }
            }
        }
    }
}
