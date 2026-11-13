package com.example.agricultureoffline.ScreenUI.farmer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agricultureoffline.R
import com.example.agricultureoffline.i18n.LocalAppLanguage
import com.example.agricultureoffline.i18n.LocalSetAppLanguage
import com.example.agricultureoffline.ui.components.FieldBackground
import com.example.agricultureoffline.ui.components.GlassCard
import com.example.agricultureoffline.ui.components.LanguageChips
import com.example.agricultureoffline.ui.theme.AgriColors

private data class DialOption(val iso: String, val code: String, val label: String)

private val dialOptions = listOf(
    DialOption("IN", "+91", "IN +91"),
    DialOption("US", "+1", "US +1"),
    DialOption("GB", "+44", "UK +44"),
    DialOption("AE", "+971", "AE +971"),
    DialOption("BD", "+880", "BD +880"),
    DialOption("NP", "+977", "NP +977"),
    DialOption("PK", "+92", "PK +92"),
    DialOption("LK", "+94", "LK +94")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerScreen(
    onCreateProfile: (name: String, dialCode: String, nationalNumber: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dial by remember { mutableStateOf(dialOptions.first()) }
    var dialOpen by remember { mutableStateOf(false) }
    val language = LocalAppLanguage.current
    val setLanguage = LocalSetAppLanguage.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0x33FFFFFF),
        unfocusedContainerColor = Color(0x22FFFFFF),
        focusedBorderColor = AgriColors.Mint,
        unfocusedBorderColor = AgriColors.GlassStroke,
        focusedTextColor = AgriColors.TextPrimary,
        unfocusedTextColor = AgriColors.TextPrimary,
        focusedLeadingIconColor = AgriColors.Mint,
        unfocusedLeadingIconColor = AgriColors.TextSecondary,
        cursorColor = AgriColors.Mint,
        focusedPlaceholderColor = AgriColors.TextMuted,
        unfocusedPlaceholderColor = AgriColors.TextMuted,
        focusedPrefixColor = AgriColors.TextPrimary,
        unfocusedPrefixColor = AgriColors.TextPrimary
    )

    FieldBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LanguageChips(selected = language, onSelect = setLanguage)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "🌿 ${stringResource(R.string.brand_name)}",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = AgriColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.create_farmer_profile),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = AgriColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.farmer_tagline),
                fontSize = 13.sp,
                color = AgriColors.TextMuted
            )
            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.name)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = dialOpen,
                        onExpandedChange = { dialOpen = it },
                        modifier = Modifier.width(124.dp)
                    ) {
                        OutlinedTextField(
                            value = dial.label,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dialOpen) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = fieldColors
                        )
                        ExposedDropdownMenu(expanded = dialOpen, onDismissRequest = { dialOpen = false }) {
                            dialOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        dial = option
                                        dialOpen = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { newPhone ->
                            val maxLen = if (dial.code == "+91") 10 else 15
                            if (newPhone.length <= maxLen && newPhone.all { it.isDigit() }) {
                                phone = newPhone
                            }
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.phone_number)) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = fieldColors
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                val phoneValid = isValidPhone(dial.code, phone)

                Button(
                    onClick = { onCreateProfile(name.trim(), dial.code, phone.trim()) },
                    enabled = name.isNotBlank() && phoneValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriColors.LeafBright,
                        disabledContainerColor = Color(0x66C7D0C8),
                        disabledContentColor = AgriColors.TextMuted
                    )
                ) {
                    Text(
                        text = stringResource(R.string.create_profile),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun isValidPhone(countryCode: String, nationalNumber: String): Boolean {
    if (nationalNumber.isBlank() || nationalNumber.any { !it.isDigit() }) return false
    return if (countryCode == "+91") nationalNumber.length == 10 else nationalNumber.length in 8..15
}
