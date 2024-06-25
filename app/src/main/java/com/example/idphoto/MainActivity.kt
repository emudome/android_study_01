package com.example.idphoto

import android.Manifest
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.idphoto.ui.theme.IDPhotoTheme

class MainActivity : ComponentActivity() {
    private val cameraViewModel: CameraViewModel by viewModels()
    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IDPhotoTheme {
                RequestCameraPermission {
                }
                CameraScreen(cameraViewModel)
            }
        }
    }
    @Composable
    private fun RequestCameraPermission(
        onPermissionGranted: () -> Unit,
    ) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (!isGranted) {
                finish()
            } else {
                onPermissionGranted()
            }
        }
        LaunchedEffect(key1 = Unit) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}


@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            CameraPreview(viewModel, modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
                    .wrapContentSize(align = Alignment.Center)
            ) {
                IconButton(
                    onClick = {
                        val fileName = SimpleDateFormat(MainActivity.FILENAME_FORMAT, java.util.Locale.US).format(System.currentTimeMillis())
                        viewModel.takePhoto(fileName)
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPreview(viewModel: CameraViewModel, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
            }

            viewModel.setImageCapture(
                ImageCapture.Builder()
                    .build()
            )

            val previewUseCase = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            viewModel.startCamera(
                lifecycleOwner = lifecycleOwner,
                previewUseCase
            )

            return@AndroidView previewView
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PhotoPreview() {
    IDPhotoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                IconButton(
                    onClick = { /* Do nothing for preview */ },
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}