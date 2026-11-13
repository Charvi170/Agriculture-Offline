package com.example.agricultureoffline.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.agricultureoffline.R
import com.example.agricultureoffline.domain.ScanInsights
import com.example.agricultureoffline.domain.ScanStatusKey
import com.example.agricultureoffline.i18n.DiseaseTranslator
import com.example.agricultureoffline.i18n.LocalAppLanguage
import com.example.agricultureoffline.ui.components.FieldBackground
import com.example.agricultureoffline.ui.components.GlassCard
import com.example.agricultureoffline.ui.theme.AgriColors
import java.io.File

@Composable
fun ResultScreen(
    disease: String,
    confidence: Int,
    symptoms: String,
    treatment: String,
    prevention: String = "",
    plantName: String = "",
    status: String = "Confirmed",
    scannedAt: String = "",
    imagePath: String? = null,
    className: String = "",
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val localized = DiseaseTranslator.localize(
        context = context,
        disease = com.example.agricultureoffline.data.local.Disease(
            className = className,
            plantName = plantName,
            diseaseName = disease,
            symptoms = symptoms,
            treatment = treatment,
            prevention = prevention
        ),
        language = language
    )

    val statusKey = ScanInsights.keyFor(
        rawStatus = status,
        predictedClass = className,
        diseaseName = disease,
        confidence = confidence / 100f,
        lowConfidenceThreshold = 0.50f
    )
    val isLowConfidence = statusKey == ScanStatusKey.LowConfidence
    val isHealthy = statusKey == ScanStatusKey.Healthy
    val isInfoUnavailable = ScanInsights.isInformationUnavailable(
        predictedClass = className,
        symptoms = symptoms,
        treatment = treatment
    )
    val statusLabel = when (statusKey) {
        ScanStatusKey.LowConfidence -> stringResource(R.string.status_low_confidence)
        ScanStatusKey.Uncertain -> stringResource(R.string.status_uncertain)
        ScanStatusKey.Healthy -> stringResource(R.string.status_healthy)
        ScanStatusKey.Confirmed -> stringResource(R.string.status_confirmed)
    }

    FieldBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = AgriColors.TextPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.diagnosis_result),
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                    color = AgriColors.TextPrimary
                )
            }

            if (!imagePath.isNullOrBlank()) {
                val imageFile = File(imagePath)
                GlassCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (imageFile.exists()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(imageFile).crossfade(true).build(),
                                contentDescription = stringResource(R.string.scanned_leaf),
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = AgriColors.Mint
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.image_unavailable),
                                    color = AgriColors.TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLowConfidence) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(R.string.status_low_confidence),
                            tint = AgriColors.Warning,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.low_confidence_title, confidence),
                                fontWeight = FontWeight.Bold,
                                color = AgriColors.Warning,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.low_confidence_body),
                                color = AgriColors.TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isLowConfidence) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = if (isLowConfidence) AgriColors.Warning else AgriColors.Success
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22FFFFFF)
                    ) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = AgriColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when {
                            isLowConfidence -> stringResource(R.string.uncertain_prediction)
                            isHealthy -> stringResource(R.string.crop_is_healthy)
                            else -> stringResource(R.string.disease_detected)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = AgriColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (localized.plantName.isNotBlank() &&
                        !localized.plantName.equals("General", ignoreCase = true)
                    ) {
                        Text(
                            text = localized.plantName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AgriColors.Mint
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = localized.diseaseName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isLowConfidence) AgriColors.Warning else AgriColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isLowConfidence) Color(0x55E65100) else Color(0x5532A852)
                        ) {
                            Text(
                                text = stringResource(R.string.confidence_label, confidence),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isLowConfidence) AgriColors.Warning else AgriColors.Mint
                            )
                        }
                        if (scannedAt.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = scannedAt, fontSize = 12.sp, color = AgriColors.TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.symptoms),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriColors.Mint
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = localized.symptoms.ifBlank { stringResource(R.string.symptoms_unavailable) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriColors.TextSecondary,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (!isInfoUnavailable && (!isLowConfidence || localized.treatment.isNotBlank())) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isHealthy) {
                            stringResource(R.string.care_advice)
                        } else {
                            stringResource(R.string.treatment_guidance)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriColors.Mint
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isLowConfidence) {
                            stringResource(R.string.low_confidence_treatment_prefix) + "\n\n" + localized.treatment
                        } else {
                            localized.treatment
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriColors.TextSecondary,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (localized.prevention.isNotBlank() && !isInfoUnavailable) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.preventive_measures),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriColors.Mint
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localized.prevention,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriColors.TextSecondary,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriColors.LeafBright)
            ) {
                Text(
                    text = stringResource(R.string.back_to_home),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
