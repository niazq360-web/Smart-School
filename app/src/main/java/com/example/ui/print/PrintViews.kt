package com.example.ui.print

import android.content.Context
import android.print.PrintManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.components.QrCodeCanvas
import com.example.ui.theme.SchoolBlue

@Composable
fun PrintableFeeReceiptDialog(
    payment: FeePayment,
    profile: SchoolProfile,
    onDismiss: () -> Unit
) {
    var isThermalMode by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top control bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Fee Receipt",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = !isThermalMode,
                            onClick = { isThermalMode = false },
                            label = { Text("Standard A4") }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = isThermalMode,
                            onClick = { isThermalMode = true },
                            label = { Text("Thermal Slip (80mm)") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable printable receipt body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (isThermalMode) {
                        ThermalReceiptView(payment, profile)
                    } else {
                        StandardReceiptView(payment, profile)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Print Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Printing receipt ${payment.receiptNo} to printer queue...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                        modifier = Modifier.testTag("print_receipt_action")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isThermalMode) "Print Thermal Slip" else "Print / Save PDF")
                    }
                }
            }
        }
    }
}

@Composable
fun StandardReceiptView(payment: FeePayment, profile: SchoolProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SchoolBlue
                    )
                    Text(text = profile.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = "Tel: ${profile.phone} | Reg: ${profile.regNumber}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                QrCodeCanvas(content = "${payment.receiptNo}:${payment.studentId}", sizeDp = 75)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 2.dp, color = SchoolBlue)

            // Receipt meta
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("RECEIPT NO: ${payment.receiptNo}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("STUDENT ID: ${payment.studentId}", fontSize = 13.sp)
                    Text("STUDENT NAME: ${payment.studentName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("CLASS / SECTION: ${payment.className} - ${payment.section}", fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DATE: ${payment.paymentDate}", fontSize = 13.sp)
                    Text("BILLING MONTH: ${payment.month}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SchoolBlue)
                    Text("PAYMENT METHOD: ${payment.paymentMethod}", fontSize = 13.sp)
                    Text("REF: ${payment.paymentRef}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fee Items Table
            Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fee Particulars", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Amount (${profile.currencySymbol})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    FeeRow("Monthly Tuition Fee", payment.monthlyFee)
                    if (payment.transportFee > 0) FeeRow("Transport Fee", payment.transportFee)
                    if (payment.computerFee > 0) FeeRow("Computer / IT Lab Fee", payment.computerFee)
                    if (payment.examFee > 0) FeeRow("Examination Fee", payment.examFee)
                    if (payment.previousDue > 0) FeeRow("Previous Unpaid Dues", payment.previousDue)
                    if (payment.discountAmount > 0) FeeRow("Scholarship / Discount", -payment.discountAmount)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Totals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Payable:", fontWeight = FontWeight.SemiBold)
                Text("${profile.currencySymbol} ${payment.totalPayable.toInt()}", fontWeight = FontWeight.SemiBold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Amount Received:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
                Text("${profile.currencySymbol} ${payment.paidAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Remaining Dues Balance:", fontWeight = FontWeight.SemiBold, color = if (payment.remainingBalance > 0) Color(0xFFDC2626) else Color.Gray)
                Text("${profile.currencySymbol} ${payment.remainingBalance.toInt()}", fontWeight = FontWeight.SemiBold, color = if (payment.remainingBalance > 0) Color(0xFFDC2626) else Color.Gray)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Signatures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("___________________________", color = Color.Gray)
                    Text("Cashier / Accountant: ${payment.cashierName}", fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("___________________________", color = Color.Gray)
                    Text("Principal: ${profile.principalName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ThermalReceiptView(payment: FeePayment, profile: SchoolProfile) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .verticalScroll(rememberScrollState()),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFEFA)),
        shape = RoundedCornerShape(4.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(profile.name.uppercase(), fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(profile.phone, fontSize = 11.sp)
            Text("--------------------------------", fontFamily = FontFamily.Monospace)
            Text("FEE RECEIPT (OFFICE & STUDENT COPY)", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            Text("REC: ${payment.receiptNo} | DATE: ${payment.paymentDate}", fontSize = 11.sp)
            Text("--------------------------------", fontFamily = FontFamily.Monospace)

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text("Student: ${payment.studentName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("ID: ${payment.studentId} | Class: ${payment.className}", fontSize = 11.sp)
                Text("Month: ${payment.month}", fontSize = 11.sp)
            }

            Text("--------------------------------", fontFamily = FontFamily.Monospace)

            Column(modifier = Modifier.fillMaxWidth()) {
                ThermalRow("Tuition Fee", payment.monthlyFee)
                if (payment.transportFee > 0) ThermalRow("Transport", payment.transportFee)
                if (payment.computerFee > 0) ThermalRow("Computer Lab", payment.computerFee)
                if (payment.previousDue > 0) ThermalRow("Previous Due", payment.previousDue)
                if (payment.discountAmount > 0) ThermalRow("Discount", -payment.discountAmount)
                Text("--------------------------------", fontFamily = FontFamily.Monospace)
                ThermalRow("TOTAL DUE", payment.totalPayable, isBold = true)
                ThermalRow("PAID (${payment.paymentMethod})", payment.paidAmount, isBold = true)
                ThermalRow("BALANCE DUE", payment.remainingBalance, isBold = true)
            }

            Spacer(modifier = Modifier.height(12.dp))
            QrCodeCanvas(content = payment.receiptNo, sizeDp = 70)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Thank you for paying on time!", fontSize = 10.sp, textAlign = TextAlign.Center)
            Text("Computer Generated Receipt", fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun FeeRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp)
        Text(text = "${amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ThermalRow(label: String, amount: Double, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = "${amount.toInt()}", fontSize = 11.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}

/**
 * Printable Student ID Card (Front & Back)
 */
@Composable
fun StudentIdCardDialog(
    student: Student,
    profile: SchoolProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Student Identity Card",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SchoolBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                // FRONT OF CARD
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(180.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SchoolBlue)
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Box
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SchoolBlue, modifier = Modifier.size(48.dp))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text("S/D/O: ${student.fatherName}", fontSize = 11.sp, color = Color(0xFF475569))
                                Text("ID: ${student.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SchoolBlue)
                                Text("Class: ${student.className} - ${student.section} | Roll: ${student.rollNo}", fontSize = 11.sp)
                                Text("Emergency: ${student.emergencyContact}", fontSize = 10.sp, color = Color(0xFFDC2626))
                            }

                            QrCodeCanvas(content = student.id, sizeDp = 50)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BACK OF CARD
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(180.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("STUDENT IDENTITY RULES", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SchoolBlue)
                            Text("1. This card must be displayed at all times on campus.", fontSize = 9.sp)
                            Text("2. Required for QR attendance scan & library book checkout.", fontSize = 9.sp)
                            Text("3. If found, return to school administrative office.", fontSize = 9.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Address: ${profile.address}", fontSize = 9.sp, color = Color.Gray)
                            Text("Phone: ${profile.phone}", fontSize = 9.sp, color = Color.Gray)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Valid: ${profile.academicYear}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("__________________", fontSize = 8.sp, color = Color.Gray)
                                Text("Principal Signature", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Close") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Printing ID Card for ${student.name}...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print ID Card")
                    }
                }
            }
        }
    }
}

/**
 * Printable Result Card / Marksheet Dialog
 */
@Composable
fun ResultCardDialog(
    result: ExamResultItem,
    profile: SchoolProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Official Student Result Card", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(profile.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SchoolBlue, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text(result.examTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Student: ${result.studentName}", fontWeight = FontWeight.Bold)
                                Text("Student ID: ${result.studentId}")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Class: ${result.className}")
                                Text("Roll No: ${result.rollNo}")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Results summary
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Marks", color = Color.Gray, fontSize = 12.sp)
                                Text("${result.obtainedMarks} / ${result.totalMarks}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Percentage", color = Color.Gray, fontSize = 12.sp)
                                Text("${result.percentage}%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Grade", color = Color.Gray, fontSize = 12.sp)
                                Text(result.grade, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Position", color = Color.Gray, fontSize = 12.sp)
                                Text(result.position, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFD97706))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Teacher's Remarks: \"${result.remarks}\"", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Spacer(modifier = Modifier.height(28.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Class Teacher Signature: __________________", fontSize = 11.sp)
                            Text("Principal Signature: __________________", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Dismiss") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { Toast.makeText(context, "Printing Result Card...", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Export PDF")
                    }
                }
            }
        }
    }
}
