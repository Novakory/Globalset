package com.example.globalapp.views.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.globalapp.R
import com.example.globalapp.components.GProgressBar
import com.example.globalapp.components.MyFloatingActionButton
import com.example.globalapp.components.OunTextFieldPassword
import com.example.globalapp.components.TextAlert
import com.example.globalapp.util.Constants
import com.example.globalapp.views.login.controllers.ControllerLogin
import com.example.globalapp.views.home.controllers.ControllerPropuestas
import com.example.globalapp.views.login.controllers.AuthPurpose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAutorizarDesautorizar(
    confirmText :String,
    navController: NavController,
    viewoModelPropuestas: ControllerPropuestas,
    viewModelLogin: ControllerLogin,
    onDismissRequest:()->Unit,
    onConfirmRequest: ()->Unit,
){
    AlertDialog(
        onDismissRequest,
        title = {
            Text(text = if(viewoModelPropuestas.switchAutorizando) "AUTORIZAR" else "DESAUTORIZAR",
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,

                color = colorResource(R.color.set_secundary),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            Content(navController,viewoModelPropuestas,viewModelLogin)
        },

        confirmButton = {
            Button(
                onConfirmRequest,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = colorResource(R.color.set_secundary),
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewoModelPropuestas.controllerDialogAutorizarDesautorizar.progressbarState.isLoading
            ) {
                Text(confirmText)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    navController: NavController,
    viewoModelPropuestas: ControllerPropuestas,
    viewModelLogin: ControllerLogin
) {
    val border = Modifier.border(1.dp, Color.Gray)
    val controllerDialogAutorizarDesautorizar = viewoModelPropuestas.controllerDialogAutorizarDesautorizar

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_radioformula1),
            contentDescription = "logo",
            modifier =
            Modifier
//                .size(100.dp)
                .width(200.dp)
                .height(100.dp)
                .clip(CircleShape)//HACE QUE SEA CIRCULAR
//                    .border(BorderStroke(1.dp,Color.Gray), shape = RoundedCornerShape(100.dp))
//                .background(colorResource(R.color.set_tertiary))
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))
                .clip(shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.background(color = colorResource(id = R.color.opaque))
                    .border(1.dp, Color.Gray)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                OunTextFieldPassword(viewoModelPropuestas.txtPasswordDialog, "Contraseña",
                OunTextFieldPassword(viewoModelPropuestas.controllerDialogAutorizarDesautorizar.txtPasswordDialog, "Contraseña",
                    enabled = !controllerDialogAutorizarDesautorizar.progressbarState.isLoading,
                    modifier = Modifier.fillMaxWidth()) {
                    viewoModelPropuestas.onValue(it,"txtPasswordDialog")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewoModelPropuestas.handlerAutorizarDesautorizar(viewModelLogin.idUsuario,viewModelLogin,viewModelLogin.user.trim(),
                            viewoModelPropuestas.controllerDialogAutorizarDesautorizar.txtPasswordDialog.toString())
//                            viewoModelPropuestas.txtPasswordDialog.toString())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !controllerDialogAutorizarDesautorizar.progressbarState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(R.color.set_secundary),
                    ),
//                    colors = ButtonColors(
//                        contentColor = Color.White,
//                        containerColor = Color.Gray,
//                        disabledContentColor = Color.Red,
//                        disabledContainerColor = Color.Black
//                    )
                ) {
                    Text("Aceptar")
                }
                if (controllerDialogAutorizarDesautorizar.progressbarState.isLoading) {
                    Spacer(Modifier.height(10.dp))
                    GProgressBar(controllerDialogAutorizarDesautorizar.progressbarState.message)
                }
                if(controllerDialogAutorizarDesautorizar.autorizarPropuestasResponse.SUCCESS == false) {
                    Spacer(Modifier.height(10.dp))
                    TextAlert(
                        controllerDialogAutorizarDesautorizar.autorizarPropuestasResponse.MESSAGE,
                        Constants.ICO_WARNING,
                        textAlign =  TextAlign.Center
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text("ó", fontSize = 24.sp//, fontWeight = FontWeight.Bold
                    , modifier = Modifier.padding(horizontal = 3.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }


                Spacer(modifier = Modifier.height(8.dp))

                MyFloatingActionButton(
                    enabled = !controllerDialogAutorizarDesautorizar.progressbarState.isLoading,
                    onClick = {
                        viewModelLogin.handlerFingerPrint(navController,viewModelLogin.user,
                            AuthPurpose.AUTORIZAR_PROPUESTAS)
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_fingerprint_24),
                        "Localized description",
                        modifier = border.size(46.dp)
                    )
                }
            }
        }
    }
}