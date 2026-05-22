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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eventpro.utils.QrCodeAnalyzer
import com.eventpro.viewmodels.ScannerViewModel
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scannedQrValue by viewModel.scannedQrValue.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

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

    Box(modifier = modifier.fillMaxSize()) {
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

        scannedQrValue?.let { qrValue ->
            AlertDialog(
                onDismissRequest = { viewModel.resetScanner() },
                title = { Text("¡Acceso Validado!") },
                text = { Text(qrValue) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetScanner() }) {
                        Text("Aceptar")
                    }
                },
            )
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
