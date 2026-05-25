package com.eventpro.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eventpro.model.ResultadoEscaneo
import com.eventpro.utils.QrCodeAnalyzer
import com.eventpro.viewmodels.ScannerViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onNavigateToPanel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

    var showManualInputDialog by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escáner de Acceso") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateToPanel) {
                        Text("Ver Panel", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showManualInputDialog = true },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("Ingreso Manual") }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            if (!hasCameraPermission) {
                CameraPermissionContent(
                    onGrantPermissionClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                )
            } else {
                val previewView = remember {
                    PreviewView(context).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                }
                val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize(),
                )

                DisposableEffect(lifecycleOwner, isScanning) {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                    val listener = Runnable {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        cameraProvider.unbindAll()

                        if (isScanning) {
                            imageAnalysis.setAnalyzer(
                                cameraExecutor,
                                QrCodeAnalyzer { qrValue ->
                                    viewModel.onQrScanned(qrValue)
                                },
                            )
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis,
                            )
                        } else {
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                            )
                        }
                    }

                    cameraProviderFuture.addListener(
                        listener,
                        ContextCompat.getMainExecutor(context),
                    )

                    onDispose {
                        runCatching { cameraProviderFuture.get().unbindAll() }
                        cameraExecutor.shutdown()
                    }
                }
            }

            // Overlay de Resultado
            AnimatedVisibility(
                visible = scanResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ResultOverlay(scanResult)
            }
        }
    }

    if (showManualInputDialog) {
        var idText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = { Text("Ingreso Manual") },
            text = {
                OutlinedTextField(
                    value = idText,
                    onValueChange = { idText = it },
                    label = { Text("ID del Asistente") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.onManualInput(idText)
                    showManualInputDialog = false
                }) {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ResultOverlay(result: ResultadoEscaneo?) {
    val backgroundColor = when (result) {
        is ResultadoEscaneo.Exito -> Color(0xFF4CAF50)
        is ResultadoEscaneo.Duplicado -> Color(0xFFFFEB3B)
        is ResultadoEscaneo.NoValido -> Color(0xFFF44336)
        null -> Color.Transparent
    }

    val icon = when (result) {
        is ResultadoEscaneo.Exito -> Icons.Default.CheckCircle
        is ResultadoEscaneo.Duplicado -> Icons.Default.Warning
        is ResultadoEscaneo.NoValido -> Icons.Default.Cancel
        null -> Icons.Default.Info
    }

    val title = when (result) {
        is ResultadoEscaneo.Exito -> "ACCESO APROBADO"
        is ResultadoEscaneo.Duplicado -> "ACCESO DUPLICADO"
        is ResultadoEscaneo.NoValido -> "QR NO VÁLIDO"
        null -> ""
    }

    val subtitle = when (result) {
        is ResultadoEscaneo.Exito -> result.asistente.nombre
        is ResultadoEscaneo.Duplicado -> "Este asistente ya ingresó\n${result.asistente.nombre}"
        is ResultadoEscaneo.NoValido -> "Este código no está registrado"
        null -> ""
    }

    val time = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = if (result is ResultadoEscaneo.Duplicado) Color.Black else Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (result is ResultadoEscaneo.Duplicado) Color.Black else Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = if (result is ResultadoEscaneo.Duplicado) Color.Black else Color.White
            )
            if (result is ResultadoEscaneo.Exito) {
                Text(
                    text = "Hora: $time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onGrantPermissionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Se requiere acceso a la cámara para escanear los QR",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onGrantPermissionClick,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text("Conceder permiso")
        }
    }
}
