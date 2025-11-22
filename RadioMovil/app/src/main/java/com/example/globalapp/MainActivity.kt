package com.example.globalapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.example.globalapp.navegation.AppNavegation
import com.example.globalapp.retrofit.openWebSocket
import com.example.globalapp.ui.theme.GlobalAppTheme
import com.example.globalapp.viewModels.ControllerDetallePropuesta
import com.example.globalapp.views.login.controllers.ControllerLogin
import com.example.globalapp.views.home.controllers.ControllerPropuestas
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {//: AppCompatActivity() {//: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: ControllerLogin by viewModels()
        val viewModelPropuestas: ControllerPropuestas by viewModels();
        val viewModelDetallesPropuesta: ControllerDetallePropuesta by viewModels();

        checkBiometricSupport(viewModel)
        authenticate(viewModel)

        setContent {
            AppNavegation(viewModel,viewModelPropuestas,viewModelDetallesPropuesta);
//            var auth by rememberSaveable { mutableStateOf(false) };
            /*Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.showDialogAutenticate()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Gray,
                    )
                ) {
                    Text("Entrar")
                }
                Text(if (viewModel.isAutenticate) "Logeado" else "Sin logear")
            }*/

        }

//        openWebSocket(viewModelPropuestas)
    }
    private fun checkBiometricSupport(viewModel: ControllerLogin) {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ) == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            viewModel.controllerAuthFingerprint.updateCanAutenticate(true);
            viewModel.controllerAuthFingerprint.promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Autenticación usando huella digital o patron")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
        } else {
            viewModel.controllerAuthFingerprint.updateCanAutenticate(false);
        }
    }

    private fun authenticate(viewModel: ControllerLogin) {//,onResult: (Boolean) -> Unit
        if (!viewModel.controllerAuthFingerprint.canAutenticate) {
//            onResult(false)
            viewModel.controllerAuthFingerprint.updateIsAutenticate(false)
            return
        }

        viewModel.controllerAuthFingerprint.biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    onResult(true)
                    viewModel.controllerAuthFingerprint.updateIsAutenticate(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    onResult(false)
                    viewModel.controllerAuthFingerprint.updateIsAutenticate(false)
                }
            }
        )
    }
}
